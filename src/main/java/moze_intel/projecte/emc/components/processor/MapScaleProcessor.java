package moze_intel.projecte.emc.components.processor;

import java.util.function.ToLongFunction;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.DataComponentProcessor;
import moze_intel.projecte.api.components.IDataComponentProcessor;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@DataComponentProcessor
public class MapScaleProcessor implements IDataComponentProcessor {

	@DataComponentProcessor.Instance
	public static final MapScaleProcessor INSTANCE = new MapScaleProcessor();

	private long paperEmc, lockEmc;

	@Override
	public String getName() {
		return PEConfigTranslations.DCP_MAP_EXTENSION.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.DCP_MAP_EXTENSION.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.DCP_MAP_EXTENSION.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC) throws ArithmeticException {
		if (!(info.getItem().value() instanceof MapItem)) {//Not a map, skip trying to do anything
			return currentEMC;
		}
		ItemStack fakeStack = info.createStack();
		MapPostProcessing postProcessing = fakeStack.get(DataComponents.MAP_POST_PROCESSING);
		Level level = tryGetLevel();
		MapItemSavedData data = level == null ? null : MapItem.getSavedData(fakeStack, level);
		boolean locked = postProcessing == MapPostProcessing.LOCK;
		int scale = postProcessing == MapPostProcessing.SCALE ? 1 : 0;
		if (data != null) {
			scale += data.scale;
			if (scale > MapItemSavedData.MAX_SCALE) {
				scale = MapItemSavedData.MAX_SCALE;
			}
			if (!locked) {
				locked = data.locked;
			}
		}
		if (locked) {
			if (lockEmc == 0) {//Glass panes can't be transmuted, so this item shouldn't have an emc value
				return 0;
			}
			currentEMC = Math.addExact(currentEMC, lockEmc);
		}
		if (scale > 0) {
			if (paperEmc == 0) {//Paper can't be transmuted, so this item shouldn't have an emc value
				return 0;
			}
			//Note: While extending a map in a crafting table takes eight pieces of paper,
			// extending it in a cartography table only takes a single piece of paper per scale
			currentEMC = Math.addExact(currentEMC, Math.multiplyExact(paperEmc, scale));
		}
		return currentEMC;
	}

	@Override
	public void updateCachedValues(@Nullable ToLongFunction<ItemInfo> emcLookup) {
		if (emcLookup == null) {
			lockEmc = paperEmc = 0;
			return;
		}
		//Calculate base decorated pot (four bricks) emc
		paperEmc = emcLookup.applyAsLong(ItemInfo.fromItem(Items.PAPER));
		//TODO: Re-evaluate this, as the reason glass panes don't have an emc value is because it is less than zero
		// so realistically they should be considered free, though I don't think it is worth keeping track of
		lockEmc = emcLookup.applyAsLong(ItemInfo.fromItem(Items.GLASS_PANE));
	}

	@Nullable
	private static Level tryGetLevel() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			//Note: This is not ideal, but because ServerLevel#getMapData, sends the map id via the overworld's data storage
			// that means the level doesn't really matter on the server side as it will all be from the overworld anyway
			return server.overworld();
		} else if (FMLEnvironment.getDist().isClient()) {
			return ClientLevelHelper.getLevel();
		}
		return null;
	}

	private static class ClientLevelHelper {

		@Nullable
		public static Level getLevel() {
			return Minecraft.getInstance().level;
		}
	}
}
