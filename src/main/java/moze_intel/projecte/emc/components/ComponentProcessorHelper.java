package moze_intel.projecte.emc.components;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.IComponentProcessorHelper;
import moze_intel.projecte.utils.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class ComponentProcessorHelper implements IComponentProcessorHelper {

	private static final Function<Holder<Item>, ItemInfo> HOLDER_TO_INFO = ItemInfo::fromItem;
	private static final Function<ItemStack, @Nullable ItemInfo> STACK_TO_INFO = stack -> stack.isEmpty() ? null : ItemInfo.fromStack(stack);

	@Nullable
	private Object2LongMap<DyeColor> colorEmc;

	public static ComponentProcessorHelper instance() {
		return (ComponentProcessorHelper) IComponentProcessorHelper.INSTANCE;
	}

	void updateCachedValues(@Nullable ToLongFunction<ItemInfo> emcLookup) {
		if (emcLookup == null) {
			colorEmc = null;
		} else {
			//Calculate and store the min emc value needed for specific dye colors for use in data component processors
			colorEmc = new Object2LongOpenHashMap<>();
			for (DyeColor color : Constants.COLORS) {
				long minColorEmc = getMinValue(emcLookup, BuiltInRegistries.ITEM.getTagOrEmpty(color.getTag()), HOLDER_TO_INFO);
				if (minColorEmc > 0) {
					colorEmc.put(color, minColorEmc);
				}
			}
		}
	}

	@NotNull
	@Override
	public ItemInfo getPersistentInfo(@NotNull ItemInfo info) {
		return DataComponentManager.getPersistentInfo(info);
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getColorEmc(@NotNull DyeColor color) {
		return colorEmc == null ? 0 : colorEmc.getLong(color);
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getMinEmcFor(@NotNull ToLongFunction<ItemInfo> emcLookup, @NotNull Ingredient ingredient) {
		return getMinValue(emcLookup, Arrays.asList(getMatchingStacks(ingredient)), STACK_TO_INFO);
	}

	private ItemStack[] getMatchingStacks(Ingredient ingredient) {
		try {
			return ingredient.items().map(holder -> new ItemStack(holder.value())).toArray(ItemStack[]::new);
		} catch (Exception e) {
			//Note: In theory this should never throw as it is called after all the reload listeners have fired, but in case it does error: catch it
			ICustomIngredient customIngredient = ingredient.getCustomIngredient();
			if (customIngredient != null) {//Should basically always be the case
				Identifier name = NeoForgeRegistries.INGREDIENT_TYPES.getKey(customIngredient.getType());
				if (name == null) {
					PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Ingredient of type: {} crashed when getting the matching stacks. Please report this to the ingredient's creator.",
							customIngredient.getClass(), e);
				} else {
					PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Ingredient of type: {} crashed when getting the matching stacks. Please report this to the ingredient's creator ({}).",
							name, name.getNamespace(), e);
				}
			} else {
				PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Crashed when getting the matching stacks.", e);
			}
			return new ItemStack[0];
		}
	}

	@Range(from = 0, to = Long.MAX_VALUE)
	private <TYPE> long getMinValue(@NotNull ToLongFunction<ItemInfo> emcLookup, @NotNull Iterable<TYPE> iterable, @NotNull Function<TYPE, @Nullable ItemInfo> infoCreator) {
		long minEmc = 0;
		for (TYPE type : iterable) {
			ItemInfo info = infoCreator.apply(type);
			if (info != null) {
				long enc = emcLookup.applyAsLong(info);
				if (enc != 0 && (minEmc == 0 || enc < minEmc)) {
					minEmc = enc;
				}
			}
		}
		return minEmc;
	}
}
