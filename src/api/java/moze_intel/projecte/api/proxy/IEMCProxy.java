package moze_intel.projecte.api.proxy;

import java.util.Objects;
import java.util.ServiceLoader;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface IEMCProxy {

	/**
	 * The proxy for EMC-based API queries.
	 */
	IEMCProxy INSTANCE = ServiceLoader.load(IEMCProxy.class).findFirst()
			.orElseThrow(() -> new IllegalStateException("No valid ServiceImpl for IEMCProxy found, ProjectE may be absent, damaged, or outdated"));

	/**
	 * Queries the EMC value registry if the given item has an EMC value
	 * <p>
	 * Can be called at any time, but will only return valid results if a world is loaded
	 * <p>
	 * Can be called on both sides
	 *
	 * @param itemLike The item we want to query
	 *
	 * @return Whether the item has an emc value
	 */
	default boolean hasValue(@NotNull ItemLike itemLike) {
		return Objects.requireNonNull(itemLike).asItem() != Items.AIR && hasValue(ItemInfo.fromItem(itemLike));
	}

	/**
	 * Queries the EMC value registry if the given ItemStack has an EMC value
	 * <p>
	 * This will also use the damage value to check if the Item has an EMC value
	 * <p>
	 * Can be called at any time, but will only return valid results if a world is loaded
	 * <p>
	 * Can be called on both sides
	 *
	 * @param stack The stack we want to query
	 *
	 * @return Whether the ItemStack has an emc value
	 */
	default boolean hasValue(@NotNull ItemStack stack) {
		return !Objects.requireNonNull(stack).isEmpty() && hasValue(ItemInfo.fromStack(stack));
	}

	/**
	 * Queries the EMC value registry if the given ItemInfo has an EMC value
	 * <p>
	 * Can be called at any time, but will only return valid results if a world is loaded
	 * <p>
	 * Can be called on both sides
	 *
	 * @param info The ItemInfo we want to query
	 *
	 * @return Whether the ItemInfo has an emc value
	 */
	default boolean hasValue(@NotNull ItemInfo info) {
		return getValue(Objects.requireNonNull(info)) > 0;
	}

	/**
	 * Queries the EMC value for the provided item
	 * <p>
	 * Can be called at any time, but will only return valid results if a world is loaded
	 * <p>
	 * Can be called on both sides
	 *
	 * @param itemLike The item we want to query
	 *
	 * @return The item's EMC value, or 0 if there is none
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	default long getValue(@NotNull ItemLike itemLike) {
		return Objects.requireNonNull(itemLike).asItem() == Items.AIR ? 0 : getValue(ItemInfo.fromItem(itemLike));
	}

	/**
	 * Queries the EMC value for the provided stack
	 * <p>
	 * Can be called at any time, but will only return valid results if a world is loaded
	 * <p>
	 * Can be called on both sides
	 * <p>
	 * This takes into account bonuses such as stored emc in power items and enchantments
	 *
	 * @param stack The stack we want to query
	 *
	 * @return The stack's EMC value, or 0 if there is none
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	default long getValue(@NotNull ItemStack stack) {
		return Objects.requireNonNull(stack).isEmpty() ? 0 : getValue(ItemInfo.fromStack(stack));
	}

	/**
	 * Queries the EMC value for the provided ItemInfo
	 * <p>
	 * Can be called at any time, but will only return valid results if a world is loaded
	 * <p>
	 * Can be called on both sides
	 * <p>
	 * This takes into account bonuses such as stored emc in power items and enchantments
	 *
	 * @param info The ItemInfo we want to query
	 *
	 * @return The stack's EMC value, or 0 if there is none
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	long getValue(@NotNull ItemInfo info);

	/**
	 * Queries the EMC sell-value for the provided stack
	 * <p>
	 * Can be called at any time, but will only return valid results if a world is loaded
	 * <p>
	 * Can be called on both sides
	 *
	 * @param stack The stack we want to query
	 *
	 * @return EMC the stack should yield when burned by transmutation, condensers, or relays
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	default long getSellValue(@NotNull ItemStack stack) {
		return Objects.requireNonNull(stack).isEmpty() ? 0 : getSellValue(ItemInfo.fromStack(stack));
	}

	/**
	 * Queries the EMC sell-value for the provided ItemInfo
	 * <p>
	 * Can be called at any time, but will only return valid results if a world is loaded
	 * <p>
	 * Can be called on both sides
	 *
	 * @param info The ItemInfo we want to query
	 *
	 * @return EMC the stack should yield when burned by transmutation, condensers, or relays
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	long getSellValue(@NotNull ItemInfo info);

	/**
	 * Gets an {@link ItemInfo} with the {@link net.minecraft.nbt.CompoundTag} reduced to what will be saved to knowledge/used for condensing.
	 *
	 * @param info The ItemInfo we want to trim to the data that will be used for persistence.
	 *
	 * @return An {@link ItemInfo} for the same item as the input info, but with a potentially reduced {@link net.minecraft.nbt.CompoundTag}, containing whatever data is
	 * persistent/matters.
	 */
	@NotNull
	ItemInfo getPersistentInfo(@NotNull ItemInfo info);
}