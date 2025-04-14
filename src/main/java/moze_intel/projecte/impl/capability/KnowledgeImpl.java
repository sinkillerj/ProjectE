package moze_intel.projecte.impl.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.impl.codec.PECodecHelper;
import moze_intel.projecte.network.PEStreamCodecs;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncChangePKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncEmcPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncInputsAndLocksPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncPKT;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
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
		if (!info.hasModifiedComponents() || EMCMappingHandler.hasEmcValue(info)) {
			//If we have no custom components or the base mapping has an emc value for our item with the given components
			// then we don't have an extended state
			return null;
		}
		ItemInfo cleanedInfo = IEMCProxy.INSTANCE.getPersistentInfo(info);
		if (cleanedInfo.hasModifiedComponents() && !EMCMappingHandler.hasEmcValue(cleanedInfo)) {
			//If we still have custom components after unimportant parts being stripped, and it doesn't
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
		return attachment.knowledge.contains(IEMCProxy.INSTANCE.getPersistentInfo(info));
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
			// Note: We ignore the tome here being a separate entity because it should not have any persistent item
			return tryAdd(attachment, persistentInfo);
		}
		if (info.getItem().is(PEItems.TOME_OF_KNOWLEDGE.getKey())) {
			//Make sure we don't have any data components as it doesn't have any effect for the tome
			info = info.itemOnly();
			//Note: We don't bother checking if we already somehow know the tome without having full knowledge
			// as we are learning it without any data components which means that it doesn't have any extra
			// persistent item so can just check if it is already in it by nature of it being a set
			attachment.knowledge.add(info);
			attachment.fullKnowledge = true;
			fireChangedEvent();
			return true;
		}
		return tryAdd(attachment, IEMCProxy.INSTANCE.getPersistentInfo(info));
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
			if (info.getItem().is(PEItems.TOME_OF_KNOWLEDGE.getKey())) {
				//If we have full knowledge and are trying to remove the tome allow it.
				//Make sure we don't have any data components as it doesn't have any effect for the tome
				info = info.itemOnly();
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
		return tryRemove(attachment, IEMCProxy.INSTANCE.getPersistentInfo(info));
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
		PacketDistributor.sendToPlayer(player, new KnowledgeSyncPKT(attachment()));
	}

	@Override
	public void syncEmc(@NotNull ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new KnowledgeSyncEmcPKT(getEmc()));
	}

	@Override
	public void syncKnowledgeChange(@NotNull ServerPlayer player, ItemInfo change, boolean learned) {
		PacketDistributor.sendToPlayer(player, new KnowledgeSyncChangePKT(change, learned));
	}

	@Override
	public void syncInputAndLocks(@NotNull ServerPlayer player, IntList slotsChanged, TargetUpdateType updateTargets) {
		if (!slotsChanged.isEmpty()) {
			KnowledgeAttachment attachment = attachment();
			int slots = attachment.inputLocks.getSlots();
			Int2ObjectMap<ItemStack> stacksToSync = new Int2ObjectOpenHashMap<>();
			for (int slot : slotsChanged) {
				if (slot >= 0 && slot < slots) {
					//Validate the slot is a valid index
					stacksToSync.put(slot, attachment.inputLocks.getStackInSlot(slot));
				}
			}
			if (!stacksToSync.isEmpty()) {
				//Validate it is not empty in case we were fed bad indices
				PacketDistributor.sendToPlayer(player, new KnowledgeSyncInputsAndLocksPKT(stacksToSync, updateTargets));
			}
		}
	}

	@Override
	public void receiveInputsAndLocks(Int2ObjectMap<ItemStack> changes) {
		KnowledgeAttachment attachment = attachment();
		int slots = attachment.inputLocks.getSlots();
		for (Iterator<Int2ObjectMap.Entry<ItemStack>> iterator = Int2ObjectMaps.fastIterator(changes); iterator.hasNext(); ) {
			Int2ObjectMap.Entry<ItemStack> entry = iterator.next();
			int slot = entry.getIntKey();
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
		boolean hasRemoved = false;
		for (Iterator<ItemInfo> iterator = attachment.knowledge.iterator(); iterator.hasNext(); ) {
			ItemInfo info = iterator.next();
			ItemInfo persistentInfo = IEMCProxy.INSTANCE.getPersistentInfo(info);
			if (!info.equals(persistentInfo)) {
				//If the new persistent variant has an EMC value though we add it because that is what they would have learned
				// had they tried to consume the item now instead of before
				if (IEMCProxy.INSTANCE.hasValue(persistentInfo)) {
					toAdd.add(persistentInfo);
				}
				//If something about the persistence changed and the item we have is no longer directly learnable
				// we remove it from our knowledge
				iterator.remove();
				hasRemoved = true;
			} else if (!IEMCProxy.INSTANCE.hasValue(info)) {
				//If the items do match but it just no longer has an EMC value, then we remove it as well
				iterator.remove();
				hasRemoved = true;
			}
		}
		return attachment.knowledge.addAll(toAdd) || hasRemoved;
	}

	public static class KnowledgeAttachment {

		private static final int LOCK_SLOTS = 9;

		private static final Codec<Set<ItemInfo>> MUTABLE_KNOWLEDGE_CODEC = ItemInfo.CODEC.listOf()
			.promotePartial(error -> PECore.LOGGER.error("Failed to load stored knowledge: {}", error))
			.xmap(HashSet::new, List::copyOf);
		public static final Codec<KnowledgeAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				MUTABLE_KNOWLEDGE_CODEC.fieldOf("knowledge").forGetter(attachment -> attachment.knowledge),
				PECodecHelper.MUTABLE_HANDLER_CODEC.fieldOf("input_locks").forGetter(attachment -> attachment.inputLocks),
				IPECodecHelper.INSTANCE.nonNegativeBigInt().optionalFieldOf("emc", BigInteger.ZERO).forGetter(attachment -> attachment.emc),
				Codec.BOOL.optionalFieldOf("full_knowledge", false).forGetter(attachment -> attachment.fullKnowledge)
		).apply(instance, KnowledgeAttachment::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgeAttachment> STREAM_CODEC = StreamCodec.composite(
				ItemInfo.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), attachment -> attachment.knowledge,
				PEStreamCodecs.handlerStreamCodec(LOCK_SLOTS), attachment -> attachment.inputLocks,
				PEStreamCodecs.EMC_VALUE, attachment -> attachment.emc,
				ByteBufCodecs.BOOL, attachment -> attachment.fullKnowledge,
				KnowledgeAttachment::new
		);

		private final ItemStackHandler inputLocks;
		private final Set<ItemInfo> knowledge;
		private boolean fullKnowledge;
		private BigInteger emc;

		public KnowledgeAttachment() {
			this(new HashSet<>(), new ItemStackHandler(LOCK_SLOTS), BigInteger.ZERO, false);
		}

		private KnowledgeAttachment(Set<ItemInfo> knowledge, ItemStackHandler inputLocks, BigInteger emc, boolean fullKnowledge) {
			this.knowledge = knowledge;
			this.inputLocks = inputLocks;
			this.emc = emc;
			this.fullKnowledge = fullKnowledge;
		}

		@Nullable
		public KnowledgeAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
			//Note: ItemInfo and BigInteger are both immutable, so we can just add them directly
			return new KnowledgeAttachment(new HashSet<>(knowledge), PEAttachmentTypes.copyHandler(inputLocks, ItemStackHandler::new), emc, fullKnowledge);
		}
	}
}