package moze_intel.projecte.impl.capability;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.capability.managing.SerializableCapabilityResolver;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.gameObjs.items.Tome;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncChangePKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncEmcPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncInputsAndLocksPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncPKT;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public final class KnowledgeImpl {

	public static IKnowledgeProvider getDefault() {
		return new DefaultImpl(null);
	}

	private static class DefaultImpl implements IKnowledgeProvider {

		@Nullable
		private final Player player;
		private final Set<ItemInfo> knowledge = new HashSet<>();
		private final ItemStackHandler inputLocks = new ItemStackHandler(9);
		private BigInteger emc = BigInteger.ZERO;
		private boolean fullKnowledge = false;

		private DefaultImpl(@Nullable Player player) {
			this.player = player;
		}

		private void fireChangedEvent() {
			if (player != null && !player.level.isClientSide) {
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
			boolean hasKnowledge = fullKnowledge || !knowledge.isEmpty();
			knowledge.clear();
			fullKnowledge = false;
			if (hasKnowledge) {
				//If we previously had any knowledge fire the fact that our knowledge changed
				fireChangedEvent();
			}
		}

		@Nullable
		private ItemInfo getIfPersistent(@Nonnull ItemInfo info) {
			if (!info.hasNBT() || EMCMappingHandler.hasEmcValue(info)) {
				//If we have no NBT or the base mapping has an emc value for our item with the given NBT
				// then we don't have an extended state
				return null;
			}
			ItemInfo cleanedInfo = NBTManager.getPersistentInfo(info);
			if (cleanedInfo.hasNBT() && !EMCMappingHandler.hasEmcValue(cleanedInfo)) {
				//If we still have NBT after unimportant parts being stripped and it doesn't
				// directly have an EMC value, then we it has some persistent information
				return cleanedInfo;
			}
			return null;
		}

		@Override
		public boolean hasKnowledge(@Nonnull ItemInfo info) {
			if (fullKnowledge) {
				//If we have all knowledge, check if the item has extra data and
				// may not actually be in our knowledge set but can be added to it
				ItemInfo persistentInfo = getIfPersistent(info);
				return persistentInfo == null || knowledge.contains(persistentInfo);
			}
			return knowledge.contains(NBTManager.getPersistentInfo(info));
		}

		@Override
		public boolean addKnowledge(@Nonnull ItemInfo info) {
			if (fullKnowledge) {
				ItemInfo persistentInfo = getIfPersistent(info);
				if (persistentInfo == null) {
					//If the item doesn't have extra data, and we have all knowledge, don't actually add any
					return false;
				}
				//If it does have extra data, pretend we don't have full knowledge and try adding it as what we have is persistent.
				// Note: We ignore the tome here being a separate entity because it should not have any persistent info
				return tryAdd(persistentInfo);
			}
			if (info.getItem() instanceof Tome) {
				if (info.hasNBT()) {
					//Make sure we don't have any NBT as it doesn't have any effect for the tome
					info = ItemInfo.fromItem(info.getItem());
				}
				//Note: We don't bother checking if we already somehow know the tome without having full knowledge
				// as we are learning it without any NBT which means that it doesn't have any extra persistent info
				// so can just check if it is already in it by nature of it being a set
				knowledge.add(info);
				fullKnowledge = true;
				fireChangedEvent();
				return true;
			}
			return tryAdd(NBTManager.getPersistentInfo(info));
		}

		private boolean tryAdd(@Nonnull ItemInfo cleanedInfo) {
			if (knowledge.add(cleanedInfo)) {
				fireChangedEvent();
				return true;
			}
			return false;
		}

		@Override
		public boolean removeKnowledge(@Nonnull ItemInfo info) {
			if (fullKnowledge) {
				if (info.getItem() instanceof Tome) {
					//If we have full knowledge and are trying to remove the tome allow it
					if (info.hasNBT()) {
						//Make sure we don't have any NBT as it doesn't have any effect for the tome
						info = ItemInfo.fromItem(info.getItem());
					}
					knowledge.remove(info);
					fullKnowledge = false;
					fireChangedEvent();
					return true;
				}
				//Otherwise check if we have any persistent information, and if so try removing that
				// as we may have it known as an "extra" item
				ItemInfo persistentInfo = getIfPersistent(info);
				return persistentInfo != null && tryRemove(persistentInfo);
			}
			return tryRemove(NBTManager.getPersistentInfo(info));
		}

		private boolean tryRemove(@Nonnull ItemInfo cleanedInfo) {
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
		public void sync(@Nonnull ServerPlayer player) {
			PacketHandler.sendTo(new KnowledgeSyncPKT(serializeNBT()), player);
		}

		@Override
		public void syncEmc(@Nonnull ServerPlayer player) {
			PacketHandler.sendTo(new KnowledgeSyncEmcPKT(getEmc()), player);
		}

		@Override
		public void syncKnowledgeChange(@Nonnull ServerPlayer player, ItemInfo change, boolean learned) {
			PacketHandler.sendTo(new KnowledgeSyncChangePKT(change, learned), player);
		}

		@Override
		public void syncInputAndLocks(@Nonnull ServerPlayer player, List<Integer> slotsChanged, TargetUpdateType updateTargets) {
			if (!slotsChanged.isEmpty()) {
				int slots = inputLocks.getSlots();
				Map<Integer, ItemStack> stacksToSync = new HashMap<>();
				for (int slot : slotsChanged) {
					if (slot >= 0 && slot < slots) {
						//Validate the slot is a valid index
						stacksToSync.put(slot, inputLocks.getStackInSlot(slot));
					}
				}
				if (!stacksToSync.isEmpty()) {
					//Validate it is not empty in case we were fed bad indices
					PacketHandler.sendTo(new KnowledgeSyncInputsAndLocksPKT(stacksToSync, updateTargets), player);
				}
			}
		}

		@Override
		public void receiveInputsAndLocks(Map<Integer, ItemStack> changes) {
			int slots = inputLocks.getSlots();
			for (Map.Entry<Integer, ItemStack> entry : changes.entrySet()) {
				int slot = entry.getKey();
				if (slot >= 0 && slot < slots) {
					//Validate the slot is a valid index
					inputLocks.setStackInSlot(slot, entry.getValue());
				}
			}
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag properties = new CompoundTag();
			properties.putString("transmutationEmc", emc.toString());

			ListTag knowledgeWrite = new ListTag();
			for (ItemInfo i : knowledge) {
				knowledgeWrite.add(i.write(new CompoundTag()));
			}

			properties.put("knowledge", knowledgeWrite);
			properties.put("inputlock", inputLocks.serializeNBT());
			properties.putBoolean("fullknowledge", fullKnowledge);
			return properties;
		}

		@Override
		public void deserializeNBT(CompoundTag properties) {
			String transmutationEmc = properties.getString("transmutationEmc");
			emc = transmutationEmc.isEmpty() ? BigInteger.ZERO : new BigInteger(transmutationEmc);

			ListTag list = properties.getList("knowledge", Tag.TAG_COMPOUND);
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
			inputLocks.deserializeNBT(properties.getCompound("inputlock"));
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
			toRemove.forEach(knowledge::remove);
			knowledge.addAll(toAdd);
		}
	}

	public static class Provider extends SerializableCapabilityResolver<IKnowledgeProvider> {

		public static final ResourceLocation NAME = PECore.rl("knowledge");

		public Provider(Player player) {
			super(new DefaultImpl(player));
		}

		@Nonnull
		@Override
		public Capability<IKnowledgeProvider> getMatchingCapability() {
			return PECapabilities.KNOWLEDGE_CAPABILITY;
		}
	}

	private KnowledgeImpl() {
	}
}