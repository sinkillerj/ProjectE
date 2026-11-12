package moze_intel.projecte.emc.components.processor;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.DataComponentProcessor;
import moze_intel.projecte.api.components.IDataComponentProcessor;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@DataComponentProcessor
public class DecoratedShieldProcessor implements IDataComponentProcessor {

	@DataComponentProcessor.Instance
	public static final DecoratedShieldProcessor INSTANCE = new DecoratedShieldProcessor();

	@Override
	public String getName() {
		return PEConfigTranslations.DCP_DECORATED_SHIELD.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.DCP_DECORATED_SHIELD.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.DCP_DECORATED_SHIELD.tooltip();
	}

	@Override
	public final boolean hasPersistentComponents() {
		return true;
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public final long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC) throws ArithmeticException {
		if (info.getItem().is(Tags.Items.TOOLS_SHIELD)) {
			DyeColor baseColor = info.getOrNull(DataComponents.BASE_COLOR);
			if (baseColor != null) {
				ItemStack banner = new ItemStack(getBannerItem(baseColor));
				BannerPatternLayers patternLayers = info.getOrNull(DataComponents.BANNER_PATTERNS);
				if (patternLayers != null && !patternLayers.equals(BannerPatternLayers.EMPTY)) {
					//If there is any pattern stored, set it so that we can get the value of the banner when it has that pattern stored
					banner.set(DataComponents.BANNER_PATTERNS, patternLayers);
				}
				long bannerValue = IEMCProxy.INSTANCE.getValue(banner);
				if (bannerValue == 0) {
					//No valid value for the attached banner, don't allow the shield to be converted to emc
					return 0;
				}
				return Math.addExact(currentEMC, bannerValue);
			}
		}
		return currentEMC;
	}

	private static Item getBannerItem(DyeColor color) {
		return Items.BANNER.pick(color);
	}

	@Override
	public final void collectPersistentComponents(@NotNull ItemInfo info, @NotNull DataComponentPatch.Builder builder) {
		if (info.getItem().is(Tags.Items.TOOLS_SHIELD)) {
			DyeColor baseColor = info.getOrNull(DataComponents.BASE_COLOR);
			if (baseColor != null) {
				builder.set(DataComponents.BASE_COLOR, baseColor);
				BannerPatternLayers patternLayers = info.getOrNull(DataComponents.BANNER_PATTERNS);
				if (patternLayers != null && !patternLayers.equals(BannerPatternLayers.EMPTY)) {
					//If there is any pattern stored, set it so that we can get the value of the banner when it has that pattern stored
					builder.set(DataComponents.BANNER_PATTERNS, patternLayers);
				}
			}
		}
	}
}
