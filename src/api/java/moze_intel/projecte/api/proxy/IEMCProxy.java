package moze_intel.projecte.api.proxy;

import java.util.Objects;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public interface IEMCProxy {

	/**
	 * Queries the EMC value registry if the given block has an EMC value
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * @param block The block we want to query
	 *
	 * @return Whether the block has an emc value
	 */
	default boolean hasValue(@Nonnull Block block) {
		return hasValue(Objects.requireNonNull(block).asItem());
	}

	/**
	 * Queries the EMC value registry if the given item has an EMC value
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * @param item The item we want to query
	 *
	 * @return Whether the item has an emc value
	 */
	default boolean hasValue(@Nonnull Item item) {
		return Objects.requireNonNull(item) != Items.AIR && hasValue(ItemInfo.fromItem(item));
	}

	/**
	 * Queries the EMC value registry if the given ItemStack has an EMC value
	 *
	 * This will also use the damage value to check if the Item has an EMC value
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * @param stack The stack we want to query
	 *
	 * @return Whether the ItemStack has an emc value
	 */
	default boolean hasValue(@Nonnull ItemStack stack) {
		return !Objects.requireNonNull(stack).isEmpty() && hasValue(ItemInfo.fromStack(stack));
	}

	/**
	 * Queries the EMC value registry if the given ItemInfo has an EMC value
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * @param info The ItemInfo we want to query
	 *
	 * @return Whether the ItemInfo has an emc value
	 */
	default boolean hasValue(@Nonnull ItemInfo info) {
		return getValue(Objects.requireNonNull(info)) > 0;
	}

	/**
	 * Queries the EMC value for the provided block
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * @param block The block we want to query
	 *
	 * @return The block's EMC value, or 0 if there is none
	 */
	default long getValue(@Nonnull Block block) {
		return getValue(Objects.requireNonNull(block).asItem());
	}

	/**
	 * Queries the EMC value for the provided item
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * @param item The item we want to query
	 *
	 * @return The item's EMC value, or 0 if there is none
	 */
	default long getValue(@Nonnull Item item) {
		return Objects.requireNonNull(item) == Items.AIR ? 0 : getValue(ItemInfo.fromItem(item));
	}

	/**
	 * Queries the EMC value for the provided stack
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * This takes into account bonuses such as stored emc in power items and enchantments
	 *
	 * @param stack The stack we want to query
	 *
	 * @return The stack's EMC value, or 0 if there is none
	 */
	default long getValue(@Nonnull ItemStack stack) {
		return Objects.requireNonNull(stack).isEmpty() ? 0 : getValue(ItemInfo.fromStack(stack));
	}

	/**
	 * Queries the EMC value for the provided ItemInfo
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * This takes into account bonuses such as stored emc in power items and enchantments
	 *
	 * @param info The ItemInfo we want to query
	 *
	 * @return The stack's EMC value, or 0 if there is none
	 */
	long getValue(@Nonnull ItemInfo info);

	/**
	 * Queries the EMC sell-value for the provided stack
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * @param stack The stack we want to query
	 *
	 * @return EMC the stack should yield when burned by transmutation, condensers, or relays
	 */
	default long getSellValue(@Nonnull ItemStack stack) {
		return Objects.requireNonNull(stack).isEmpty() ? 0 : getSellValue(ItemInfo.fromStack(stack));
	}

	/**
	 * Queries the EMC sell-value for the provided ItemInfo
	 *
	 * Can be called at any time, but will only return valid results if a world is loaded
	 *
	 * Can be called on both sides
	 *
	 * @param info The ItemInfo we want to query
	 *
	 * @return EMC the stack should yield when burned by transmutation, condensers, or relays
	 */
	long getSellValue(@Nonnull ItemInfo info);
}