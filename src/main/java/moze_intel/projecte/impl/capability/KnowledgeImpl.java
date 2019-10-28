package moze_intel.projecte.impl.capability;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeSyncPKT;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
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
		private final List<ItemStack> knowledge = new ArrayList<>();
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
		public boolean hasKnowledge(@Nonnull ItemStack stack) {
			if (stack.isEmpty()) {
				return false;
			}

			if (fullKnowledge) {
				return true;
			}

			for (ItemStack s : knowledge) {
				if (s.getItem() == stack.getItem()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean addKnowledge(@Nonnull ItemStack stack) {
			if (fullKnowledge) {
				return false;
			}

			if (stack.getItem() == ObjHandler.tome) {
				if (!hasKnowledge(stack)) {
					knowledge.add(stack);
				}
				fullKnowledge = true;
				fireChangedEvent();
				return true;
			}

			if (!hasKnowledge(stack)) {
				knowledge.add(stack);
				fireChangedEvent();
				return true;
			}

			return false;
		}

		@Override
		public boolean removeKnowledge(@Nonnull ItemStack stack) {
			boolean removed = false;

			if (stack.getItem() == ObjHandler.tome) {
				fullKnowledge = false;
				removed = true;
			}

			if (fullKnowledge) {
				return false;
			}

			Iterator<ItemStack> iter = knowledge.iterator();

			while (iter.hasNext()) {
				if (stack.getItem() == iter.next().getItem()) {
					iter.remove();
					removed = true;
				}
			}

			if (removed) {
				fireChangedEvent();
			}
			return removed;
		}

		@Override
		public @Nonnull
		List<ItemStack> getKnowledge() {
			return fullKnowledge ? Transmutation.getCachedTomeKnowledge() : Collections.unmodifiableList(knowledge);
		}

		@Override
		public @Nonnull
		IItemHandlerModifiable getInputAndLocks() {
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
			for (ItemStack i : knowledge) {
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
				ItemStack item = ItemStack.read(list.getCompound(i));
				if (!item.isEmpty()) {
					knowledge.add(item);
				}
			}

			pruneStaleKnowledge();
			pruneDuplicateKnowledge();

			for (int i = 0; i < inputLocks.getSlots(); i++) {
				inputLocks.setStackInSlot(i, ItemStack.EMPTY);
			}

			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inputLocks, null, properties.getList("inputlock", Constants.NBT.TAG_COMPOUND));
			fullKnowledge = properties.getBoolean("fullknowledge");
		}

		private void pruneDuplicateKnowledge() {
			ItemHelper.removeEmptyTags(knowledge);
			ItemHelper.compactItemListNoStacksize(knowledge);
			for (ItemStack s : knowledge) {
				if (s.getCount() > 1) {
					s.setCount(1);
				}
			}
		}

		private void pruneStaleKnowledge() {
			knowledge.removeIf(stack -> !EMCHelper.doesItemHaveEmc(stack));
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