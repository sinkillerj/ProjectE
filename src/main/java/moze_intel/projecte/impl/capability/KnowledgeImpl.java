package moze_intel.projecte.impl.capability;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.gameObjs.items.Tome;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeSyncPKT;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public final class KnowledgeImpl {

	public static void init() {
		CapabilityManager.INSTANCE.register(IKnowledgeProvider.class, new Capability.IStorage<IKnowledgeProvider>() {
			@Override
			public CompoundNBT writeNBT(Capability<IKnowledgeProvider> capability, IKnowledgeProvider instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<IKnowledgeProvider> capability, IKnowledgeProvider instance, Direction side, INBT nbt) {
				if (nbt instanceof CompoundNBT) {
					instance.deserializeNBT((CompoundNBT) nbt);
				}
			}
		}, () -> new DefaultImpl(null));
	}

	private static class DefaultImpl implements IKnowledgeProvider {

		@Nullable
		private final PlayerEntity player;
		private final Set<ItemInfo> knowledge = new HashSet<>();
		private final IItemHandlerModifiable inputLocks = new ItemStackHandler(9);
		private BigInteger emc = BigInteger.ZERO;
		private boolean fullKnowledge = false;

		private DefaultImpl(@Nullable PlayerEntity player) {
			this.player = player;
		}

		private void fireChangedEvent() {
			if (player != null && !player.world.isRemote) {
				MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
			}
		}

		@Override
		public boolean hasFullKnowledge() {
			return fullKnowledge;
		}

		@Override
		public void setFullKnowledge(boolean fullKnowledge) {
			boolean changed = this.fullKnowledge != fullKnowledge;
			this.fullKnowledge = fullKnowledge;
			if (changed) {
				fireChangedEvent();
			}
		}

		@Override
		public void clearKnowledge() {
			knowledge.clear();
			fullKnowledge = false;
			fireChangedEvent();
		}

		@Override
		public boolean hasKnowledge(@Nonnull ItemInfo info) {
			if (fullKnowledge) {
				return true;
			}
			return knowledge.contains(NBTManager.getPersistentInfo(info));
		}

		@Override
		public boolean addKnowledge(@Nonnull ItemInfo info) {
			if (fullKnowledge) {
				return false;
			}
			if (info.getItem() instanceof Tome) {
				if (info.hasNBT()) {
					//Make sure we don't have any NBT as it doesn't have any effect for the tome
					info = ItemInfo.fromItem(info.getItem());
				}
				if (!hasKnowledge(info)) {
					knowledge.add(info);
				}
				fullKnowledge = true;
				fireChangedEvent();
				return true;
			}

			ItemInfo cleanedInfo = NBTManager.getPersistentInfo(info);
			if (!hasKnowledge(cleanedInfo)) {
				knowledge.add(cleanedInfo);
				fireChangedEvent();
				return true;
			}

			return false;
		}

		@Override
		public boolean removeKnowledge(@Nonnull ItemInfo info) {
			if (info.getItem() instanceof Tome) {
				setFullKnowledge(false);
				return true;
			}
			if (fullKnowledge) {
				return false;
			}
			ItemInfo cleanedInfo = NBTManager.getPersistentInfo(info);
			if (knowledge.remove(cleanedInfo)) {
				fireChangedEvent();
				return true;
			}
			return false;
		}

		@Nonnull
		@Override
		public Set<ItemInfo> getKnowledge() {
			if (fullKnowledge) {
				Set<ItemInfo> allKnowledge = EMCMappingHandler.getMappedItems();
				//Make sure we include any extra items they have learned such as various enchanted items.
				allKnowledge.addAll(knowledge);
				return Collections.unmodifiableSet(allKnowledge);
			}
			return Collections.unmodifiableSet(knowledge);
		}

		@Nonnull
		@Override
		public IItemHandlerModifiable getInputAndLocks() {
			return inputLocks;
		}

		@Override
		public BigInteger getEmc() {
			return emc;
		}

		@Override
		public void setEmc(BigInteger emc) {
			this.emc = emc;
		}

		@Override
		public void sync(@Nonnull ServerPlayerEntity player) {
			PacketHandler.sendTo(new KnowledgeSyncPKT(serializeNBT()), player);
		}

		@Override
		public CompoundNBT serializeNBT() {
			CompoundNBT properties = new CompoundNBT();
			properties.putString("transmutationEmc", emc.toString());

			ListNBT knowledgeWrite = new ListNBT();
			for (ItemInfo i : knowledge) {
				CompoundNBT tag = i.write(new CompoundNBT());
				knowledgeWrite.add(tag);
			}

			properties.put("knowledge", knowledgeWrite);
			properties.put("inputlock", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inputLocks, null));
			properties.putBoolean("fullknowledge", fullKnowledge);
			return properties;
		}

		@Override
		public void deserializeNBT(CompoundNBT properties) {
			String transmutationEmc = properties.getString("transmutationEmc");
			emc = transmutationEmc.isEmpty() ? BigInteger.ZERO : new BigInteger(transmutationEmc);

			ListNBT list = properties.getList("knowledge", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				ItemInfo info = ItemInfo.read(list.getCompound(i));
				if (info != null) {
					knowledge.add(info);
				}
			}

			pruneStaleKnowledge();

			for (int i = 0; i < inputLocks.getSlots(); i++) {
				inputLocks.setStackInSlot(i, ItemStack.EMPTY);
			}

			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inputLocks, null, properties.getList("inputlock", Constants.NBT.TAG_COMPOUND));
			fullKnowledge = properties.getBoolean("fullknowledge");
		}

		private void pruneStaleKnowledge() {
			List<ItemInfo> toRemove = new ArrayList<>();
			List<ItemInfo> toAdd = new ArrayList<>();
			for (ItemInfo info : knowledge) {
				ItemInfo persistentInfo = NBTManager.getPersistentInfo(info);
				if (!info.equals(persistentInfo)) {
					//If something about the persistence changed and the item we have is no longer directly learnable
					// we remove it from our knowledge
					toRemove.add(info);
					//If the new persistent variant has an EMC value though we add it because that is what they would have learned
					// had they tried to consume the item now instead of before
					if (EMCHelper.doesItemHaveEmc(persistentInfo)) {
						toAdd.add(persistentInfo);
					}
				} else if (!EMCHelper.doesItemHaveEmc(info)) {
					//If the items do match but it just no longer has an EMC value, then we remove it as well
					toRemove.add(info);
				}
			}
			knowledge.removeAll(toRemove);
			knowledge.addAll(toAdd);
		}
	}

	public static class Provider implements ICapabilitySerializable<CompoundNBT> {

		public static final ResourceLocation NAME = new ResourceLocation(PECore.MODID, "knowledge");

		private final DefaultImpl impl;
		private final LazyOptional<IKnowledgeProvider> cap;

		public Provider(PlayerEntity player) {
			impl = new DefaultImpl(player);
			cap = LazyOptional.of(() -> impl);
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
			if (capability == ProjectEAPI.KNOWLEDGE_CAPABILITY) {
				return cap.cast();
			}
			return LazyOptional.empty();
		}

		@Override
		public CompoundNBT serializeNBT() {
			return impl.serializeNBT();
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			impl.deserializeNBT(nbt);
		}
	}

	private KnowledgeImpl() {
	}
}