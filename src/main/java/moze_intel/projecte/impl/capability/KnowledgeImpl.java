package moze_intel.projecte.impl.capability;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.gameObjs.items.Tome;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.network.PacketUtils;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncChangePKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncEmcPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncInputsAndLocksPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncPKT;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KnowledgeImpl implements IKnowledgeProvider {

	public static IKnowledgeProvider wrapAttachment(KnowledgeAttachment attachment) {
		return new KnowledgeImpl(null) {
			@Override
			protected KnowledgeAttachment attachment() {
				return attachment;
			}
		};
	}

	private final Player player;

	public KnowledgeImpl(Player player) {
		this.player = player;
	}

	protected KnowledgeAttachment attachment() {
		//Force overriding if player is null
		Objects.requireNonNull(this.player);
		return this.player.getData(PEAttachmentTypes.KNOWLEDGE);
	}

	protected void fireChangedEvent() {
		if (player != null && !player.level().isClientSide) {
			NeoForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
		}
	}

	@Override
	public boolean hasFullKnowledge() {
		return attachment().fullKnowledge;
	}

	@Override
	public void setFullKnowledge(boolean fullKnowledge) {
		KnowledgeAttachment attachment = attachment();
		if (attachment.fullKnowledge != fullKnowledge) {
			attachment.fullKnowledge = fullKnowledge;
			fireChangedEvent();
		}
	}

	@Override
	public void clearKnowledge() {
		KnowledgeAttachment attachment = attachment();
		boolean hasKnowledge = attachment.fullKnowledge || !attachment.knowledge.isEmpty();
		attachment.knowledge.clear();
		attachment.fullKnowledge = false;
		if (hasKnowledge) {
			//If we previously had any knowledge fire the fact that our knowledge changed
			fireChangedEvent();
		}
	}

	@Nullable
	private ItemInfo getIfPersistent(@NotNull ItemInfo info) {
		if (!info.hasNBT() || EMCMappingHandler.hasEmcValue(info)) {
			//If we have no NBT or the base mapping has an emc value for our item with the given NBT
			// then we don't have an extended state
			return null;
		}
		ItemInfo cleanedInfo = NBTManager.getPersistentInfo(info);
		if (cleanedInfo.hasNBT() && !EMCMappingHandler.hasEmcValue(cleanedInfo)) {
			//If we still have NBT after unimportant parts being stripped, and it doesn't
			// directly have an EMC value, then we know it has some persistent information
			return cleanedInfo;
		}
		return null;
	}

	@Override
	public boolean hasKnowledge(@NotNull ItemInfo info) {
		KnowledgeAttachment attachment = attachment();
		if (attachment.fullKnowledge) {
			//If we have all knowledge, check if the item has extra data and
			// may not actually be in our knowledge set but can be added to it
			ItemInfo persistentInfo = getIfPersistent(info);
			return persistentInfo == null || attachment.knowledge.contains(persistentInfo);
		}
		return attachment.knowledge.contains(NBTManager.getPersistentInfo(info));
	}

	@Override
	public boolean addKnowledge(@NotNull ItemInfo info) {
		KnowledgeAttachment attachment = attachment();
		if (attachment.fullKnowledge) {
			ItemInfo persistentInfo = getIfPersistent(info);
			if (persistentInfo == null) {
				//If the item doesn't have extra data, and we have all knowledge, don't actually add any
				return false;
			}
			//If it does have extra data, pretend we don't have full knowledge and try adding it as what we have is persistent.
			// Note: We ignore the tome here being a separate entity because it should not have any persistent info
			return tryAdd(attachment, persistentInfo);
		}
		if (info.getItem() instanceof Tome) {
			if (info.hasNBT()) {
				//Make sure we don't have any NBT as it doesn't have any effect for the tome
				info = ItemInfo.fromItem(info.getItem());
			}
			//Note: We don't bother checking if we already somehow know the tome without having full knowledge
			// as we are learning it without any NBT which means that it doesn't have any extra persistent info
			// so can just check if it is already in it by nature of it being a set
			attachment.knowledge.add(info);
			attachment.fullKnowledge = true;
			fireChangedEvent();
			return true;
		}
		return tryAdd(attachment, NBTManager.getPersistentInfo(info));
	}

	private boolean tryAdd(@NotNull KnowledgeAttachment attachment, @NotNull ItemInfo cleanedInfo) {
		if (attachment.knowledge.add(cleanedInfo)) {
			fireChangedEvent();
			return true;
		}
		return false;
	}

	@Override
	public boolean removeKnowledge(@NotNull ItemInfo info) {
		KnowledgeAttachment attachment = attachment();
		if (attachment.fullKnowledge) {
			if (info.getItem() instanceof Tome) {
				//If we have full knowledge and are trying to remove the tome allow it
				if (info.hasNBT()) {
					//Make sure we don't have any NBT as it doesn't have any effect for the tome
					info = ItemInfo.fromItem(info.getItem());
				}
				attachment.knowledge.remove(info);
				attachment.fullKnowledge = false;
				fireChangedEvent();
				return true;
			}
			//Otherwise check if we have any persistent information, and if so try removing that
			// as we may have it known as an "extra" item
			ItemInfo persistentInfo = getIfPersistent(info);
			return persistentInfo != null && tryRemove(attachment, persistentInfo);
		}
		return tryRemove(attachment, NBTManager.getPersistentInfo(info));
	}

	private boolean tryRemove(@NotNull KnowledgeAttachment attachment, @NotNull ItemInfo cleanedInfo) {
		if (attachment.knowledge.remove(cleanedInfo)) {
			fireChangedEvent();
			return true;
		}
		return false;
	}

	@NotNull
	@Override
	public Set<ItemInfo> getKnowledge() {
		KnowledgeAttachment attachment = attachment();
		if (attachment.fullKnowledge) {
			Set<ItemInfo> allKnowledge = EMCMappingHandler.getMappedItems();
			//Make sure we include any extra items they have learned such as various enchanted items.
			allKnowledge.addAll(attachment.knowledge);
			return Collections.unmodifiableSet(allKnowledge);
		}
		return Collections.unmodifiableSet(attachment.knowledge);
	}

	@NotNull
	@Override
	public IItemHandlerModifiable getInputAndLocks() {
		return attachment().inputLocks;
	}

	@Override
	public BigInteger getEmc() {
		return attachment().emc;
	}

	@Override
	public void setEmc(BigInteger emc) {
		attachment().emc = emc;
	}

	@Override
	public void sync(@NotNull ServerPlayer player) {
		KnowledgeAttachment attachment = attachment();
		PacketUtils.sendTo(new KnowledgeSyncPKT(attachment.serializeNBT()), player);
	}

	@Override
	public void syncEmc(@NotNull ServerPlayer player) {
		PacketUtils.sendTo(new KnowledgeSyncEmcPKT(getEmc()), player);
	}

	@Override
	public void syncKnowledgeChange(@NotNull ServerPlayer player, ItemInfo change, boolean learned) {
		PacketUtils.sendTo(new KnowledgeSyncChangePKT(change, learned), player);
	}

	@Override
	public void syncInputAndLocks(@NotNull ServerPlayer player, List<Integer> slotsChanged, TargetUpdateType updateTargets) {
		if (!slotsChanged.isEmpty()) {
			KnowledgeAttachment attachment = attachment();
			int slots = attachment.inputLocks.getSlots();
			Map<Integer, ItemStack> stacksToSync = new HashMap<>();
			for (int slot : slotsChanged) {
				if (slot >= 0 && slot < slots) {
					//Validate the slot is a valid index
					stacksToSync.put(slot, attachment.inputLocks.getStackInSlot(slot));
				}
			}
			if (!stacksToSync.isEmpty()) {
				//Validate it is not empty in case we were fed bad indices
				PacketUtils.sendTo(new KnowledgeSyncInputsAndLocksPKT(stacksToSync, updateTargets), player);
			}
		}
	}

	@Override
	public void receiveInputsAndLocks(Map<Integer, ItemStack> changes) {
		KnowledgeAttachment attachment = attachment();
		int slots = attachment.inputLocks.getSlots();
		for (Map.Entry<Integer, ItemStack> entry : changes.entrySet()) {
			int slot = entry.getKey();
			if (slot >= 0 && slot < slots) {
				//Validate the slot is a valid index
				attachment.inputLocks.setStackInSlot(slot, entry.getValue());
			}
		}
	}

	//Note: We call this for all players when EMC changes rather than pruning on load as EMC may not be done being calculated yet
	// when on an integrated server
	public final boolean pruneStaleKnowledge() {
		KnowledgeAttachment attachment = attachment();
		List<ItemInfo> toAdd = new ArrayList<>();
		boolean hasRemoved = attachment.knowledge.removeIf(info -> {
			ItemInfo persistentInfo = NBTManager.getPersistentInfo(info);
			if (!info.equals(persistentInfo)) {
				//If the new persistent variant has an EMC value though we add it because that is what they would have learned
				// had they tried to consume the item now instead of before
				if (EMCHelper.doesItemHaveEmc(persistentInfo)) {
					toAdd.add(persistentInfo);
				}
				//If something about the persistence changed and the item we have is no longer directly learnable
				// we remove it from our knowledge
				return true;
			}
			//If the items do match but it just no longer has an EMC value, then we remove it as well
			return !EMCHelper.doesItemHaveEmc(info);
		});
		return attachment.knowledge.addAll(toAdd) || hasRemoved;
	}

	public static class KnowledgeAttachment implements INBTSerializable<CompoundTag> {

		private final Set<ItemInfo> knowledge = new HashSet<>();
		private final ItemStackHandler inputLocks = new ItemStackHandler(9);
		private BigInteger emc = BigInteger.ZERO;
		private boolean fullKnowledge = false;

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

			for (int i = 0; i < inputLocks.getSlots(); i++) {
				inputLocks.setStackInSlot(i, ItemStack.EMPTY);
			}
			inputLocks.deserializeNBT(properties.getCompound("inputlock"));
			fullKnowledge = properties.getBoolean("fullknowledge");
		}
	}
}