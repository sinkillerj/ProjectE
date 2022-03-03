package moze_intel.projecte.api.nss;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Item}s.
 */
public final class NSSItem extends AbstractNBTNSSTag<Item> {

	private NSSItem(@Nonnull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundTag nbt) {
		super(resourceLocation, isTag, nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemStack}
	 */
	@Nonnull
	public static NSSItem createItem(@Nonnull ItemStack stack) {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Can't make NSSItem with empty stack");
		}
		if (stack.isDamageableItem() && stack.hasTag()) {
			//If the stack is damageable check if the NBT is identical to what it would be without the damage attached
			// as creating a new ItemStack auto sets the NBT for the damage value, which we ideally do not want as it may
			// throw off various calculations
			if (stack.getOrCreateTag().equals(new ItemStack(stack.getItem()).getTag())) {
				//Skip including the NBT for the item as it auto gets added on stack creation anyways
				return createItem(stack.getItem(), null);
			}
		}
		return createItem(stack.getItem(), stack.getTag());
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemLike}
	 */
	@Nonnull
	public static NSSItem createItem(@Nonnull ItemLike itemProvider) {
		return createItem(itemProvider, null);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemLike} and an optional {@link CompoundTag}
	 */
	@Nonnull
	public static NSSItem createItem(@Nonnull ItemLike itemProvider, @Nullable CompoundTag nbt) {
		Item item = itemProvider.asItem();
		if (item == Items.AIR) {
			throw new IllegalArgumentException("Can't make NSSItem with empty stack");
		}
		//This should never be null or it would have crashed on being registered
		return createItem(item.getRegistryName(), nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceLocation}
	 */
	@Nonnull
	public static NSSItem createItem(@Nonnull ResourceLocation itemID) {
		return createItem(itemID, null);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceLocation} and an optional {@link CompoundTag}
	 */
	@Nonnull
	public static NSSItem createItem(@Nonnull ResourceLocation itemID, @Nullable CompoundTag nbt) {
		return new NSSItem(itemID, false, nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing a tag from a {@link ResourceLocation}
	 */
	@Nonnull
	public static NSSItem createTag(@Nonnull ResourceLocation tagId) {
		return new NSSItem(tagId, true, null);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing a tag from a {@link TagKey<Item>}
	 */
	@Nonnull
	public static NSSItem createTag(@Nonnull TagKey<Item> tag) {
		return createTag(tag.location());
	}

	@Override
	protected boolean isInstance(AbstractNSSTag<?> o) {
		return o instanceof NSSItem;
	}

	@Nonnull
	@Override
	public String getJsonPrefix() {
		//We prefer no prefix for NSSItem even though we do support ITEM|
		return "";
	}

	@Nonnull
	@Override
	public String getType() {
		return "Item";
	}

	@Nonnull
	@Override
	protected Optional<Named<Item>> getTag() {
		return getTag(Registry.ITEM);
	}

	@Override
	protected Function<Item, NormalizedSimpleStack> createNew() {
		return NSSItem::createItem;
	}
}