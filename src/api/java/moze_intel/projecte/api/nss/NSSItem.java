package moze_intel.projecte.api.nss;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Item}s.
 */
public final class NSSItem extends AbstractNBTNSSTag<Item> {

	private NSSItem(@Nonnull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundNBT nbt) {
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
		if (stack.isDamageable() && stack.hasTag()) {
			//If the stack is damageable check if the NBT is identical to what it would be without the damage attached
			// as creating a new ItemStack auto sets the NBT for the damage value, which we ideally do not want as it may
			// throw off various calculations
			if (stack.getTag().equals(new ItemStack(stack.getItem()).getTag())) {
				//Skip including the NBT for the item as it auto gets added on stack creation anyways
				return createItem(stack.getItem(), null);
			}
		}
		return createItem(stack.getItem(), stack.getTag());
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link IItemProvider}
	 */
	@Nonnull
	public static NSSItem createItem(@Nonnull IItemProvider itemProvider) {
		return createItem(itemProvider, null);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link IItemProvider} and an optional {@link CompoundNBT}
	 */
	@Nonnull
	public static NSSItem createItem(@Nonnull IItemProvider itemProvider, @Nullable CompoundNBT nbt) {
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
	 * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceLocation} and an optional {@link CompoundNBT}
	 */
	@Nonnull
	public static NSSItem createItem(@Nonnull ResourceLocation itemID, @Nullable CompoundNBT nbt) {
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
	 * Helper method to create an {@link NSSItem} representing a tag from a {@link ITag<Item>}
	 */
	@Nonnull
	public static NSSItem createTag(@Nonnull ITag<Item> tag) {
		//TODO - 1.16: Evaluate if this should use ItemTags#getCollection. I believe the below is correct as this happens in a reload listener so before ItemTags is updated
		ResourceLocation tagLocation = TagCollectionManager.func_242178_a().func_241836_b().func_232973_a_(tag);
		if (tagLocation == null) {
			throw new IllegalArgumentException("Can't make NSSItem with a tag that does not exist");
		}
		return createTag(tagLocation);
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
	protected ITagCollection<Item> getTagCollection() {
		return ItemTags.getCollection();
	}

	@Override
	protected Function<Item, NormalizedSimpleStack> createNew() {
		return NSSItem::createItem;
	}
}