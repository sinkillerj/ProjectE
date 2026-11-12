package moze_intel.projecte.emc.components.processor;

import java.util.function.ToLongFunction;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.DataComponentProcessor;
import moze_intel.projecte.api.components.IComponentProcessorHelper;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import java.util.ArrayList;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@DataComponentProcessor
public class FireworkProcessor extends PersistentComponentProcessor<Fireworks> {

	@DataComponentProcessor.Instance
	public static final FireworkProcessor INSTANCE = new FireworkProcessor();
	private static final ResourceKey<Item> FIREWORK_ROCKET = BuiltInRegistries.ITEM.getResourceKey(Items.FIREWORK_ROCKET).orElseThrow();

	//Lookup min emc value for these ingredients, so that if any mods expand on them with ATs, we can make use of it
	private long powderEmc;

	@Override
	public String getName() {
		return PEConfigTranslations.DCP_FIREWORK.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.DCP_FIREWORK.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.DCP_FIREWORK.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	protected long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC, @NotNull Fireworks fireworks) throws ArithmeticException {
		int flightDuration = fireworks.flightDuration();
		if (flightDuration > 1) {//Greater than default flight duration, factor in the extra pieces of gunpowder necessary
			if (powderEmc == 0) {//No emc value for gunpowder
				return 0;
			}
			currentEMC = Math.addExact(currentEMC, Math.multiplyExact(flightDuration - 1, powderEmc));
		}

		for (FireworkExplosion explosion : fireworks.explosions()) {
			//TODO: Should we support people expanding the star ingredient? (FireworkRocketRecipe.STAR_INGREDIENT)
			long starEmc = IEMCProxy.INSTANCE.getValue(ItemInfo.fromItem(Items.FIREWORK_STAR, DataComponentPatch.builder()
					.set(DataComponents.FIREWORK_EXPLOSION, explosion).build()));
			if (starEmc == 0) {
				//No emc representation of this star, bail out
				return 0;
			}
			currentEMC = Math.addExact(currentEMC, starEmc);
		}
		return currentEMC;
	}

	@Override
	public void updateCachedValues(@Nullable ToLongFunction<ItemInfo> emcLookup) {
		if (emcLookup == null) {
			powderEmc = 0;
			return;
		}
		//TODO: Do we even want to be supporting this, or just getting the value for gunpowder?
		powderEmc = IComponentProcessorHelper.INSTANCE.getMinEmcFor(emcLookup, Ingredient.of(Items.GUNPOWDER));
	}

	@Override
	protected boolean validItem(@NotNull ItemInfo info) {
		return info.getItem().is(FIREWORK_ROCKET);
	}

	@Override
	protected DataComponentType<Fireworks> getComponentType(@NotNull ItemInfo info) {
		return DataComponents.FIREWORKS;
	}

	@Override
	protected boolean shouldPersist(@NotNull ItemInfo info, @NotNull Fireworks component) {
		return component.flightDuration() != 1 || !component.explosions().isEmpty();
	}
}
