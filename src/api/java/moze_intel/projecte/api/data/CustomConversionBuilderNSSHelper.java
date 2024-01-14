package moze_intel.projecte.api.data;

import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Helper interface to hold some helper wrapper methods to make it cleaner to interact with various built in types of {@link NormalizedSimpleStack}s.
 *
 * @implNote The reason this is an interface is to keep the main {@link CustomConversionBuilder} file cleaner to read.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
interface CustomConversionBuilderNSSHelper extends CustomConversionNSSHelper<ConversionBuilder<CustomConversionBuilder>> {

	/**
	 * Adds a "before" emc mapping value to the given {@link NormalizedSimpleStack}.
	 *
	 * @param stack Stack to set EMC value of.
	 * @param emc   Value
	 */
	CustomConversionBuilder before(NormalizedSimpleStack stack, long emc);

	/**
	 * Adds a "before" emc mapping value of "free" to the given {@link NormalizedSimpleStack}.
	 *
	 * @param stack Stack to set as "free" in conversions.
	 */
	CustomConversionBuilder before(NormalizedSimpleStack stack);

	/**
	 * Adds an "after" emc mapping value to the given {@link NormalizedSimpleStack}.
	 *
	 * @param stack Stack to set EMC value of.
	 * @param emc   Value
	 */
	CustomConversionBuilder after(NormalizedSimpleStack stack, long emc);

	/**
	 * Adds an "after" emc mapping value of "free" to the given {@link NormalizedSimpleStack}.
	 *
	 * @param stack Stack to set as "free" in conversions.
	 */
	CustomConversionBuilder after(NormalizedSimpleStack stack);

	/**
	 * Helper method to wrap an {@link ItemStack} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value to it.
	 *
	 * @param stack Stack to set EMC value of.
	 * @param emc   Value
	 *
	 * @apiNote Either this method or {@link #before(NormalizedSimpleStack, long)} using {@link NSSItem#createItem(ItemLike, net.minecraft.nbt.CompoundTag)} should
	 * be used if NBT specifics are needed.
	 */
	default CustomConversionBuilder before(ItemStack stack, long emc) {
		return before(NSSItem.createItem(stack), emc);
	}

	/**
	 * Helper method to wrap an {@link ItemLike} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value to it.
	 *
	 * @param itemProvider Item to set EMC value of.
	 * @param emc          Value
	 */
	default CustomConversionBuilder before(ItemLike itemProvider, long emc) {
		return before(NSSItem.createItem(itemProvider), emc);
	}

	/**
	 * Helper method to wrap an {@link TagKey<Item>} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value to it.
	 *
	 * @param tag Item tag to set EMC value of.
	 * @param emc Value
	 */
	default CustomConversionBuilder before(TagKey<Item> tag, long emc) {
		return before(NSSItem.createTag(tag), emc);
	}

	/**
	 * Helper method to wrap an {@link ItemStack} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value of "free" to it.
	 *
	 * @param stack Stack to set as "free" in conversions.
	 *
	 * @apiNote Either this method or {@link #before(NormalizedSimpleStack)} using {@link NSSItem#createItem(ItemLike, net.minecraft.nbt.CompoundTag)} should be used
	 * if NBT specifics are needed.
	 */
	default CustomConversionBuilder before(ItemStack stack) {
		return before(NSSItem.createItem(stack));
	}

	/**
	 * Helper method to wrap an {@link ItemLike} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value of "free" to it.
	 *
	 * @param itemProvider Item to set as "free" in conversions.
	 */
	default CustomConversionBuilder before(ItemLike itemProvider) {
		return before(NSSItem.createItem(itemProvider));
	}

	/**
	 * Helper method to wrap an {@link TagKey<Item>} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value of "free" to it.
	 *
	 * @param tag Item tag to set as "free" in conversions.
	 */
	default CustomConversionBuilder before(TagKey<Item> tag) {
		return before(NSSItem.createTag(tag));
	}

	/**
	 * Helper method to wrap an {@link ItemStack} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value to it.
	 *
	 * @param stack Stack to set EMC value of.
	 * @param emc   Value
	 *
	 * @apiNote Either this method or {@link #after(NormalizedSimpleStack, long)} using {@link NSSItem#createItem(ItemLike, net.minecraft.nbt.CompoundTag)} should be
	 * used if NBT specifics are needed.
	 */
	default CustomConversionBuilder after(ItemStack stack, long emc) {
		return after(NSSItem.createItem(stack), emc);
	}

	/**
	 * Helper method to wrap an {@link ItemLike} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value to it.
	 *
	 * @param itemProvider Item to set EMC value of.
	 * @param emc          Value
	 */
	default CustomConversionBuilder after(ItemLike itemProvider, long emc) {
		return after(NSSItem.createItem(itemProvider), emc);
	}

	/**
	 * Helper method to wrap an {@link TagKey<Item>} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value to it.
	 *
	 * @param tag Item tag to set EMC value of.
	 * @param emc Value
	 */
	default CustomConversionBuilder after(TagKey<Item> tag, long emc) {
		return after(NSSItem.createTag(tag), emc);
	}

	/**
	 * Helper method to wrap an {@link ItemStack} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value of "free" to it.
	 *
	 * @param stack Stack to set as "free" in conversions.
	 *
	 * @apiNote Either this method or {@link #after(NormalizedSimpleStack)} using {@link NSSItem#createItem(ItemLike, net.minecraft.nbt.CompoundTag)} should be used
	 * if NBT specifics are needed.
	 */
	default CustomConversionBuilder after(ItemStack stack) {
		return after(NSSItem.createItem(stack));
	}

	/**
	 * Helper method to wrap an {@link ItemLike} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value of "free" to it.
	 *
	 * @param itemProvider Item to set as "free" in conversions.
	 */
	default CustomConversionBuilder after(ItemLike itemProvider) {
		return after(NSSItem.createItem(itemProvider));
	}

	/**
	 * Helper method to wrap an {@link TagKey<Item>} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value of "free" to it.
	 *
	 * @param tag Item tag to set as "free" in conversions.
	 */
	default CustomConversionBuilder after(TagKey<Item> tag) {
		return after(NSSItem.createTag(tag));
	}

	/**
	 * Helper method to wrap a {@link FluidStack} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value to it.
	 *
	 * @param stack Stack to set EMC value of.
	 * @param emc   Value
	 *
	 * @apiNote Either this method or {@link #before(NormalizedSimpleStack, long)} using {@link NSSFluid#createFluid(Fluid, net.minecraft.nbt.CompoundTag)} should be used
	 * if NBT specifics are needed.
	 */
	default CustomConversionBuilder before(FluidStack stack, long emc) {
		return before(NSSFluid.createFluid(stack), emc);
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value to it.
	 *
	 * @param fluid Fluid to set EMC value of.
	 * @param emc   Value
	 */
	default CustomConversionBuilder before(Fluid fluid, long emc) {
		return before(NSSFluid.createFluid(fluid), emc);
	}

	/**
	 * Helper method to wrap an {@link TagKey<Fluid>} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value to it.
	 *
	 * @param tag Fluid tag to set EMC value of.
	 * @param emc Value
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default CustomConversionBuilder beforeFluid(TagKey<Fluid> tag, long emc) {
		return before(NSSFluid.createTag(tag), emc);
	}

	/**
	 * Helper method to wrap a {@link FluidStack} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value of "free" to it.
	 *
	 * @param stack Stack to set as "free" in conversions.
	 *
	 * @apiNote Either this method or {@link #before(NormalizedSimpleStack)} using {@link NSSFluid#createFluid(Fluid, net.minecraft.nbt.CompoundTag)} should be used if
	 * NBT specifics are needed.
	 */
	default CustomConversionBuilder before(FluidStack stack) {
		return before(NSSFluid.createFluid(stack));
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value of "free" to it.
	 *
	 * @param fluid Fluid to set as "free" in conversions.
	 */
	default CustomConversionBuilder before(Fluid fluid) {
		return before(NSSFluid.createFluid(fluid));
	}

	/**
	 * Helper method to wrap an {@link TagKey<Fluid>} into a {@link NormalizedSimpleStack} and then add a "before" emc mapping value to it.
	 *
	 * @param tag Fluid tag to set as "free" in conversions.
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default CustomConversionBuilder beforeFluid(TagKey<Fluid> tag) {
		return before(NSSFluid.createTag(tag));
	}

	/**
	 * Helper method to wrap a {@link FluidStack} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value to it.
	 *
	 * @param stack Stack to set EMC value of.
	 * @param emc   Value
	 *
	 * @apiNote Either this method or {@link #after(NormalizedSimpleStack, long)} using {@link NSSFluid#createFluid(Fluid, net.minecraft.nbt.CompoundTag)} should be used
	 * if NBT specifics are needed.
	 */
	default CustomConversionBuilder after(FluidStack stack, long emc) {
		return after(NSSFluid.createFluid(stack), emc);
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value to it.
	 *
	 * @param fluid Fluid to set EMC value of.
	 * @param emc   Value
	 */
	default CustomConversionBuilder after(Fluid fluid, long emc) {
		return after(NSSFluid.createFluid(fluid), emc);
	}

	/**
	 * Helper method to wrap an {@link TagKey<Fluid>} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value to it.
	 *
	 * @param tag Fluid tag to set EMC value of.
	 * @param emc Value
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default CustomConversionBuilder afterFluid(TagKey<Fluid> tag, long emc) {
		return after(NSSFluid.createTag(tag), emc);
	}

	/**
	 * Helper method to wrap a {@link FluidStack} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value of "free" to it.
	 *
	 * @param stack Stack to set as "free" in conversions.
	 *
	 * @apiNote Either this method or {@link #before(NormalizedSimpleStack)} using {@link NSSFluid#createFluid(Fluid, net.minecraft.nbt.CompoundTag)} should be used if
	 * NBT specifics are needed.
	 */
	default CustomConversionBuilder after(FluidStack stack) {
		return after(NSSFluid.createFluid(stack));
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value of "free" to it.
	 *
	 * @param fluid Fluid to set as "free" in conversions.
	 */
	default CustomConversionBuilder after(Fluid fluid) {
		return after(NSSFluid.createFluid(fluid));
	}

	/**
	 * Helper method to wrap an {@link TagKey<Fluid>} into a {@link NormalizedSimpleStack} and then add an "after" emc mapping value to it.
	 *
	 * @param tag Fluid tag to set as "free" in conversions.
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default CustomConversionBuilder afterFluid(TagKey<Fluid> tag) {
		return after(NSSFluid.createTag(tag));
	}

	/**
	 * Helper method to create a "fake" {@link NormalizedSimpleStack} and then add a "before" emc mapping value to it.
	 *
	 * @param fake Description of the "fake" {@link NormalizedSimpleStack}.
	 * @param emc  Value
	 */
	default CustomConversionBuilder before(String fake, long emc) {
		return before(NSSFake.create(fake), emc);
	}

	/**
	 * Helper method to create a "fake" {@link NormalizedSimpleStack} and then add a "before" emc mapping value of "free" to it.
	 *
	 * @param fake Description of the "fake" {@link NormalizedSimpleStack} to set as "free" in conversions.
	 */
	default CustomConversionBuilder before(String fake) {
		return before(NSSFake.create(fake));
	}

	/**
	 * Helper method to create a "fake" {@link NormalizedSimpleStack} and then add an "after" emc mapping value to it.
	 *
	 * @param fake Description of the "fake" {@link NormalizedSimpleStack}.
	 * @param emc  Value
	 */
	default CustomConversionBuilder after(String fake, long emc) {
		return after(NSSFake.create(fake), emc);
	}

	/**
	 * Helper method to create a "fake" {@link NormalizedSimpleStack} and then add an "after" emc mapping value of "free" to it.
	 *
	 * @param fake Description of the "fake" {@link NormalizedSimpleStack} to set as "free" in conversions.
	 */
	default CustomConversionBuilder after(String fake) {
		return after(NSSFake.create(fake));
	}
}