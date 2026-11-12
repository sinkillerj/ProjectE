package moze_intel.projecte.emc.components.processor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongMaps;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;
import java.util.Map;
import java.util.function.ToLongFunction;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.DataComponentProcessor;
import moze_intel.projecte.api.components.IComponentProcessorHelper;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.utils.Constants;
import net.minecraft.util.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@DataComponentProcessor
public class FireworkStarProcessor extends PersistentComponentProcessor<FireworkExplosion> {

	@DataComponentProcessor.Instance
	public static final FireworkStarProcessor INSTANCE = new FireworkStarProcessor();
	private static final ResourceKey<Item> FIREWORK_STAR = BuiltInRegistries.ITEM.getResourceKey(Items.FIREWORK_STAR).orElseThrow();
	private static final Map<Item, FireworkExplosion.Shape> VANILLA_SHAPE_ITEMS = Map.of(
			Items.FIRE_CHARGE, FireworkExplosion.Shape.LARGE_BALL,
			Items.GOLD_NUGGET, FireworkExplosion.Shape.STAR,
			Items.CREEPER_HEAD, FireworkExplosion.Shape.CREEPER,
			Items.FEATHER, FireworkExplosion.Shape.BURST
	);
	private static final Int2ObjectMap<DyeColor> REVERSE_COLOR_LOOKUP = Util.make(new Int2ObjectOpenHashMap<>(), reverseLookup -> {
		for (DyeColor color : Constants.COLORS) {
			reverseLookup.putIfAbsent(color.getFireworkColor(), color);
		}
	});

	@NotNull
	private Reference2LongMap<FireworkExplosion.Shape> shapeEmcLookup = Reference2LongMaps.emptyMap();
	//Lookup min emc value for these ingredients, so that if any mods expand on them with ATs, we can make use of it
	private long trailEmc, twinkleEmc;

	@Override
	public String getName() {
		return PEConfigTranslations.DCP_FIREWORK_STAR.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.DCP_FIREWORK_STAR.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.DCP_FIREWORK_STAR.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC, @NotNull FireworkExplosion explosion) throws ArithmeticException {
		if (explosion.hasTrail() && trailEmc == 0 || explosion.hasTwinkle() && twinkleEmc == 0) {
			//No emc for certain ingredients that are present, which means we can't calculate an emc value overall
			return 0;
		}

		//If we have a shape that required an item to create, add the corresponding emc value for it
		FireworkExplosion.Shape shape = explosion.shape();
		if (shape != FireworkExplosion.Shape.SMALL_BALL) {
			long shapeEmc = shapeEmcLookup.getLong(shape);
			if (shapeEmc == 0) {//No emc value for what it takes to create this shape
				return 0;
			}
			currentEMC = Math.addExact(currentEMC, shapeEmc);
		}

		if (explosion.hasTrail()) {
			currentEMC = Math.addExact(currentEMC, trailEmc);
		}
		if (explosion.hasTwinkle()) {
			currentEMC = Math.addExact(currentEMC, twinkleEmc);
		}

		IntList colors = explosion.colors();
		if (!colors.isEmpty()) {
			long colorsEmc = getUsedDyesEmc(colors);
			if (colorsEmc == 0) {//No emc for at least one of the applied colors, that means we can't calculate an emc value overall
				return 0;
			}
			currentEMC = Math.addExact(currentEMC, colorsEmc);
		}

		IntList fadeColors = explosion.fadeColors();
		if (!fadeColors.isEmpty()) {
			long fadeEmc = getUsedDyesEmc(fadeColors);
			if (fadeEmc == 0) {//No emc for at least one of the applied fade colors, that means we can't calculate an emc value overall
				return 0;
			}
			currentEMC = Math.addExact(currentEMC, fadeEmc);
		}
		return currentEMC;
	}

	private long getUsedDyesEmc(IntList colors) {
		long totalEmc = 0;
		for (IntListIterator iterator = colors.iterator(); iterator.hasNext(); ) {
			int color = iterator.nextInt();
			DyeColor dyeColor = REVERSE_COLOR_LOOKUP.get(color);
			if (dyeColor == null) {
				//Unknown color, fail
				return 0;
			}
			long dyeEmc = IComponentProcessorHelper.INSTANCE.getColorEmc(dyeColor);
			if (dyeEmc == 0) {//Dye doesn't have an EMC representation available
				return 0;
			}
			totalEmc = Math.addExact(totalEmc, dyeEmc);
		}
		return totalEmc;
	}

	@Override
	public void updateCachedValues(@Nullable ToLongFunction<ItemInfo> emcLookup) {
		if (emcLookup == null) {
			shapeEmcLookup = Reference2LongMaps.emptyMap();
			trailEmc = twinkleEmc = 0;
			return;
		}
		//Note: We subtract one from the length, as SMALL_BALL does not require any, and won't be processed or stored
		shapeEmcLookup = new Reference2LongOpenHashMap<>(FireworkExplosion.Shape.values().length - 1);
		for (Map.Entry<Item, FireworkExplosion.Shape> entry : VANILLA_SHAPE_ITEMS.entrySet()) {
			FireworkExplosion.Shape shape = entry.getValue();
			if (shape != FireworkExplosion.Shape.SMALL_BALL) {
				long emc = emcLookup.applyAsLong(ItemInfo.fromItem(entry.getKey()));
				if (emc > 0) {
					shapeEmcLookup.mergeLong(shape, emc, Math::min);
				}
			}
		}
		trailEmc = IComponentProcessorHelper.INSTANCE.getMinEmcFor(emcLookup, Ingredient.of(Items.DIAMOND));
		twinkleEmc = IComponentProcessorHelper.INSTANCE.getMinEmcFor(emcLookup, Ingredient.of(Items.GLOWSTONE_DUST));
	}

	@Override
	protected boolean validItem(@NotNull ItemInfo info) {
		return info.getItem().is(FIREWORK_STAR);
	}

	@Override
	protected boolean shouldPersist(@NotNull ItemInfo info, @NotNull FireworkExplosion component) {
		return !component.equals(FireworkExplosion.DEFAULT);
	}

	@Override
	protected DataComponentType<FireworkExplosion> getComponentType(@NotNull ItemInfo info) {
		return DataComponents.FIREWORK_EXPLOSION;
	}
}
