package moze_intel.projecte.emc.components.processor;

import java.util.function.ToLongFunction;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.DataComponentProcessor;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import java.util.Optional;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.PotDecorations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@DataComponentProcessor
public class DecoratedPotProcessor extends PersistentComponentProcessor<PotDecorations> {

	@DataComponentProcessor.Instance
	public static final DecoratedPotProcessor INSTANCE = new DecoratedPotProcessor();
	private static final ResourceKey<Item> DECORATED_POT = BuiltInRegistries.ITEM.getResourceKey(Items.DECORATED_POT).orElseThrow();

	private long undecoratedEmc;

	@Override
	public String getName() {
		return PEConfigTranslations.DCP_DECORATED_POT.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.DCP_DECORATED_POT.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.DCP_DECORATED_POT.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	protected long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC, @NotNull PotDecorations decorations) throws ArithmeticException {
		long totalDecorationEmc = 0;
		for (Item decoration : sherds(decorations)) {
			long decorationEmc = IEMCProxy.INSTANCE.getValue(decoration);
			if (decorationEmc == 0) {
				//At least one sherd doesn't have an EMC value, so we can't calculate the value of the pot as a whole
				return 0;
			}
			totalDecorationEmc = Math.addExact(totalDecorationEmc, decorationEmc);
		}
		//Subtract the undecorated emc value from our current value. We do this in case this isn't the first processor that runs on some pot,
		// and another processor has adjusted the emc of it
		return Math.addExact(currentEMC - undecoratedEmc, totalDecorationEmc);
	}

	@Override
	public void updateCachedValues(@Nullable ToLongFunction<ItemInfo> emcLookup) {
		if (emcLookup == null) {
			undecoratedEmc = 0;
			return;
		}
		//Calculate base decorated pot (four bricks) emc
		undecoratedEmc = emcLookup.applyAsLong(ItemInfo.fromItem(Items.DECORATED_POT));
	}

	@Override
	protected boolean validItem(@NotNull ItemInfo info) {
		return info.getItem().is(DECORATED_POT);
	}

	@Override
	protected DataComponentType<PotDecorations> getComponentType(@NotNull ItemInfo info) {
		return DataComponents.POT_DECORATIONS;
	}

	@Override
	protected boolean shouldPersist(@NotNull ItemInfo info, @NotNull PotDecorations component) {
		return !component.equals(PotDecorations.EMPTY);
	}

	private static Iterable<Item> sherds(PotDecorations decorations) {
		java.util.List<Item> items = new java.util.ArrayList<>(4);
		for (Optional<ItemStackTemplate> side : java.util.List.of(decorations.back(), decorations.left(), decorations.right(), decorations.front())) {
			side.map(template -> template.item().value()).ifPresent(items::add);
		}
		return items;
	}
}
