package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.server.ServerLifecycleHooks;

public class TransmutationOffline {

	private static final IKnowledgeProvider NOT_FOUND_PROVIDER = immutableCopy(KnowledgeImpl.getDefault());

	private static final Map<UUID, IKnowledgeProvider> cachedKnowledgeProviders = new HashMap<>();

	public static void cleanAll() {
		cachedKnowledgeProviders.clear();
	}

	public static void clear(UUID playerUUID) {
		cachedKnowledgeProviders.remove(playerUUID);
	}

	static IKnowledgeProvider forPlayer(UUID playerUUID) {
		if (!cachedKnowledgeProviders.containsKey(playerUUID)) {
			if (!cacheOfflineData(playerUUID)) {
				cachedKnowledgeProviders.put(playerUUID, NOT_FOUND_PROVIDER);
			}
		}

		return cachedKnowledgeProviders.get(playerUUID);
	}

	private static boolean cacheOfflineData(UUID playerUUID) {
		Preconditions.checkState(Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER, "CRITICAL: Trying to read filesystem on client!!");
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		File playerData = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
		if (playerData.exists()) {
			File player = new File(playerData, playerUUID.toString() + ".dat");
			if (player.exists() && player.isFile()) {
				try (FileInputStream in = new FileInputStream(player)) {
					CompoundTag playerDat = NbtIo.readCompressed(in); // No need to create buffered stream, that call does it for us
					CompoundTag knowledgeProvider = playerDat.getCompound("ForgeCaps").getCompound(KnowledgeImpl.Provider.NAME.toString());

					IKnowledgeProvider provider = KnowledgeImpl.getDefault();
					provider.deserializeNBT(knowledgeProvider);
					cachedKnowledgeProviders.put(playerUUID, immutableCopy(provider));

					PECore.debugLog("Caching offline data for UUID: {}", playerUUID);
					return true;
				} catch (IOException e) {
					PECore.LOGGER.warn("Failed to cache offline data for API calls for UUID: {}", playerUUID);
				}
			}
		}

		return false;
	}

	private static IKnowledgeProvider immutableCopy(final IKnowledgeProvider toCopy) {
		return new IKnowledgeProvider() {
			final Set<ItemInfo> immutableKnowledge = ImmutableSet.copyOf(toCopy.getKnowledge());
			final IItemHandlerModifiable immutableInputLocks = ItemHelper.immutableCopy(toCopy.getInputAndLocks());

			@Override
			public boolean hasFullKnowledge() {
				return toCopy.hasFullKnowledge();
			}

			@Override
			public void setFullKnowledge(boolean fullKnowledge) {
			}

			@Override
			public void clearKnowledge() {
			}

			@Override
			public boolean hasKnowledge(@Nonnull ItemInfo info) {
				return toCopy.hasKnowledge(info);
			}

			@Override
			public boolean addKnowledge(@Nonnull ItemInfo info) {
				return false;
			}

			@Override
			public boolean removeKnowledge(@Nonnull ItemInfo info) {
				return false;
			}

			@Nonnull
			@Override
			public Set<ItemInfo> getKnowledge() {
				return immutableKnowledge;
			}

			@Nonnull
			@Override
			public IItemHandler getInputAndLocks() {
				return immutableInputLocks;
			}

			@Override
			public BigInteger getEmc() {
				return toCopy.getEmc();
			}

			@Override
			public void setEmc(BigInteger emc) {
			}

			@Override
			public void sync(@Nonnull ServerPlayer player) {
				toCopy.sync(player);
			}

			@Override
			public void syncEmc(@Nonnull ServerPlayer player) {
				toCopy.syncEmc(player);
			}

			@Override
			public void syncKnowledgeChange(@Nonnull ServerPlayer player, ItemInfo change, boolean learned) {
				toCopy.syncKnowledgeChange(player, change, learned);
			}

			@Override
			public void syncInputAndLocks(@Nonnull ServerPlayer player, List<Integer> slotsChanged, TargetUpdateType updateTargets) {
				toCopy.syncInputAndLocks(player, slotsChanged, updateTargets);
			}

			@Override
			public void receiveInputsAndLocks(Map<Integer, ItemStack> changes) {
			}

			@Override
			public CompoundTag serializeNBT() {
				return toCopy.serializeNBT();
			}

			@Override
			public void deserializeNBT(CompoundTag nbt) {
			}
		};
	}
}