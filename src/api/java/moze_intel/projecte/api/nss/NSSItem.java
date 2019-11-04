package moze_intel.projecte.api.nss;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
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
		//Don't bother checking if it is empty as getItem returns AIR which will then fail anyways for being empty
		return createItem(stack.getItem(), stack.getTag());
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link IItemProvider}
	 */
	@Nonnull
	public static NSSItem createItem(@Nonnull IItemProvider itemProvider) {
		Item item = itemProvider.asItem();
		if (item == Items.AIR) {
			throw new IllegalArgumentException("Can't make NSSItem with empty stack");
		}
		//This should never be null or it would have crashed on being registered
		return createItem(item.getRegistryName());
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
	 * Helper method to create an {@link NSSItem} representing a tag from a {@link Tag<Item>}
	 */
	@Nonnull
	public static NSSItem createTag(@Nonnull Tag<Item> tag) {
		return createTag(tag.getId());
	}

	@Override
	protected boolean isInstance(AbstractNSSTag o) {
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
	protected TagCollection<Item> getTagCollection() {
		return ItemTags.getCollection();
	}

	@Override
	protected Function<Item, NormalizedSimpleStack> createNew() {
		return NSSItem::createItem;
	}
}