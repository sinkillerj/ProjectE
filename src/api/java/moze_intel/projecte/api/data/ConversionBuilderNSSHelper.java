package moze_intel.projecte.api.data;

import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fluids.FluidStack;

/**
 * Helper interface to hold some helper wrapper methods to make it cleaner to interact with various built in types of {@link NormalizedSimpleStack}s.
 *
 * @implNote The reason this is an interface is to keep the main {@link ConversionBuilder} file cleaner to read.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
interface ConversionBuilderNSSHelper<BUILDER extends ConversionBuilder<BUILDER>> {

	/**
	 * Adds a {@link NormalizedSimpleStack} as an input ingredient to this conversion with the given amount.
	 *
	 * @param input  Stack used in the conversion.
	 * @param amount Amount of the input, can be negative.
	 */
	BUILDER ingredient(NormalizedSimpleStack input, int amount);

	/**
	 * Adds a {@link NormalizedSimpleStack} as an input ingredient to this conversion.
	 *
	 * @param input Stack used in the conversion.
	 */
	default BUILDER ingredient(NormalizedSimpleStack input) {
		return ingredient(input, 1);
	}

	/**
	 * Helper method to wrap an {@link ItemStack} into a {@link NormalizedSimpleStack} and amount and add it as an input ingredient.
	 *
	 * @param input Stack used in the conversion.
	 *
	 * @apiNote Either this method or {@link #ingredient(NormalizedSimpleStack, int)} using {@link NSSItem#createItem(IItemProvider, net.minecraft.nbt.CompoundNBT)}
	 * should be used if NBT specifics are needed.
	 */
	default BUILDER ingredient(ItemStack input) {
		return ingredient(NSSItem.createItem(input), input.getCount());
	}

	/**
	 * Helper method to wrap an {@link IItemProvider} into a {@link NormalizedSimpleStack} and add it as an input ingredient.
	 *
	 * @param input Item used in the conversion.
	 */
	default BUILDER ingredient(IItemProvider input) {
		return ingredient(input, 1);
	}

	/**
	 * Helper method to wrap an {@link IItemProvider} into a {@link NormalizedSimpleStack} and amount and add it as an input ingredient.
	 *
	 * @param input  Item used in the conversion.
	 * @param amount Amount of the input, can be negative.
	 */
	default BUILDER ingredient(IItemProvider input, int amount) {
		return ingredient(NSSItem.createItem(input), amount);
	}

	/**
	 * Helper method to wrap an {@link ITag<Item>} into a {@link NormalizedSimpleStack} and add it as an input ingredient.
	 *
	 * @param input Item tag used in the conversion.
	 */
	default BUILDER ingredient(ITag<Item> input) {
		return ingredient(input, 1);
	}

	/**
	 * Helper method to wrap an {@link ITag<Item>} into a {@link NormalizedSimpleStack} and amount and add it as an input ingredient.
	 *
	 * @param input  Item tag used in the conversion.
	 * @param amount Amount of the input, can be negative.
	 */
	default BUILDER ingredient(ITag<Item> input, int amount) {
		return ingredient(NSSItem.createTag(input), amount);
	}

	/**
	 * Helper method to wrap a {@link FluidStack} into a {@link NormalizedSimpleStack} and amount and add it as an input ingredient.
	 *
	 * @param input Stack used in the conversion.
	 *
	 * @apiNote Either this method or {@link #ingredient(NormalizedSimpleStack, int)} using {@link NSSFluid#createFluid(Fluid, net.minecraft.nbt.CompoundNBT)} should be
	 * used if NBT specifics are needed.
	 */
	default BUILDER ingredient(FluidStack input) {
		return ingredient(NSSFluid.createFluid(input), input.getAmount());
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and add it as an input ingredient.
	 *
	 * @param input Fluid used in the conversion.
	 */
	default BUILDER ingredient(Fluid input) {
		return ingredient(input, 1);
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and amount and add it as an input ingredient.
	 *
	 * @param input  Fluid used in the conversion.
	 * @param amount Amount of the input, can be negative.
	 */
	default BUILDER ingredient(Fluid input, int amount) {
		return ingredient(NSSFluid.createFluid(input), amount);
	}

	/**
	 * Helper method to wrap an {@link ITag<Fluid>} into a {@link NormalizedSimpleStack} and add it as an input ingredient.
	 *
	 * @param input Fluid tag used in the conversion.
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default BUILDER ingredientFluid(ITag<Fluid> input) {
		return ingredientFluid(input, 1);
	}

	/**
	 * Helper method to wrap an {@link ITag<Fluid>} into a {@link NormalizedSimpleStack} and amount and add it as an input ingredient.
	 *
	 * @param input  Fluid tag used in the conversion.
	 * @param amount Amount of the input, can be negative.
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default BUILDER ingredientFluid(ITag<Fluid> input, int amount) {
		return ingredient(NSSFluid.createTag(input), amount);
	}

	/**
	 * Helper method to create a "fake" {@link NormalizedSimpleStack} and then add it as an input ingredient.
	 *
	 * @param fake Description of the "fake" {@link NormalizedSimpleStack}.
	 */
	default BUILDER ingredient(String fake) {
		return ingredient(fake, 1);
	}

	/**
	 * Helper method to create a "fake" {@link NormalizedSimpleStack} and then add it as an input ingredient with the given amount.
	 *
	 * @param fake   Description of the "fake" {@link NormalizedSimpleStack}.
	 * @param amount Amount of the input, can be negative.
	 */
	default BUILDER ingredient(String fake, int amount) {
		return ingredient(NSSFake.create(fake), amount);
	}
}