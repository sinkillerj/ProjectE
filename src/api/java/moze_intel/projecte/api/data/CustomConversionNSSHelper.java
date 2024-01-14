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
 * @param <BUILDER> Type of the {@link ConversionBuilder} that will be returned by the conversion methods.
 *
 * @implNote The reason this is an interface is to reduce the duplicate code between the various implementers and also keep the files cleaner to read.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
interface CustomConversionNSSHelper<BUILDER extends ConversionBuilder<?>> {

	/**
	 * Creates a {@link BUILDER} that outputs the given amount of the given {@link NormalizedSimpleStack}.
	 *
	 * @param output Stack produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 */
	BUILDER conversion(NormalizedSimpleStack output, int amount);

	/**
	 * Creates a {@link BUILDER} that outputs one of the given {@link NormalizedSimpleStack}.
	 *
	 * @param output Stack produced by the conversion.
	 */
	default BUILDER conversion(NormalizedSimpleStack output) {
		return conversion(output, 1);
	}

	/**
	 * Helper method to wrap an {@link ItemStack} into a {@link NormalizedSimpleStack} and output amount, and then creates a {@link BUILDER} representing it.
	 *
	 * @param output Stack produced by the conversion.
	 *
	 * @apiNote Either this method or {@link #conversion(NormalizedSimpleStack, int)} using {@link NSSItem#createItem(ItemLike, net.minecraft.nbt.CompoundTag)}
	 * should be used if NBT specifics are needed.
	 */
	default BUILDER conversion(ItemStack output) {
		return conversion(NSSItem.createItem(output), output.getCount());
	}

	/**
	 * Helper method to wrap an {@link ItemLike} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param output Item produced by the conversion.
	 */
	default BUILDER conversion(ItemLike output) {
		return conversion(output, 1);
	}

	/**
	 * Helper method to wrap an {@link ItemLike} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param output Item produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 */
	default BUILDER conversion(ItemLike output, int amount) {
		return conversion(NSSItem.createItem(output), amount);
	}

	/**
	 * Helper method to wrap an {@link TagKey<Item>} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param output Item tag produced by the conversion.
	 */
	default BUILDER conversion(TagKey<Item> output) {
		return conversion(output, 1);
	}

	/**
	 * Helper method to wrap an {@link TagKey<Item>} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param output Item tag produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 */
	default BUILDER conversion(TagKey<Item> output, int amount) {
		return conversion(NSSItem.createTag(output), amount);
	}

	/**
	 * Helper method to wrap a {@link FluidStack} into a {@link NormalizedSimpleStack} and output amount, and then creates a {@link BUILDER} representing it.
	 *
	 * @param output Stack produced by the conversion.
	 *
	 * @apiNote Either this method or {@link #conversion(NormalizedSimpleStack, int)} using {@link NSSFluid#createFluid(Fluid, net.minecraft.nbt.CompoundTag)} should be
	 * used if NBT specifics are needed.
	 */
	default BUILDER conversion(FluidStack output) {
		return conversion(NSSFluid.createFluid(output), output.getAmount());
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param output Fluid produced by the conversion.
	 */
	default BUILDER conversion(Fluid output) {
		return conversion(output, 1);
	}

	/**
	 * Helper method to wrap a {@link Fluid} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param output Fluid produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 */
	default BUILDER conversion(Fluid output, int amount) {
		return conversion(NSSFluid.createFluid(output), amount);
	}

	/**
	 * Helper method to wrap an {@link TagKey<Fluid>} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param output Fluid tag produced by the conversion.
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default BUILDER conversionFluid(TagKey<Fluid> output) {
		return conversionFluid(output, 1);
	}

	/**
	 * Helper method to wrap an {@link TagKey<Fluid>} into a {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param output Fluid tag produced by the conversion.
	 * @param amount Amount the conversion outputs.
	 *
	 * @apiNote The naming of this method is slightly different due to type erasure, and fluid tags being less likely to be used than item tags.
	 */
	default BUILDER conversionFluid(TagKey<Fluid> output, int amount) {
		return conversion(NSSFluid.createTag(output), amount);
	}

	/**
	 * Helper method to wrap a "fake" {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it.
	 *
	 * @param fake Description of the "fake" {@link NormalizedSimpleStack}.
	 */
	default BUILDER conversion(String fake) {
		return conversion(fake, 1);
	}

	/**
	 * Helper method to wrap a "fake" {@link NormalizedSimpleStack} and then create a {@link BUILDER} representing it and the given amount.
	 *
	 * @param fake   Description of the "fake" {@link NormalizedSimpleStack}.
	 * @param amount Amount the conversion outputs.
	 */
	default BUILDER conversion(String fake, int amount) {
		return conversion(NSSFake.create(fake), amount);
	}
}