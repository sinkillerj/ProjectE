package moze_intel.projecte.api.nss;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Item}s.
 */
public final class NSSItem extends AbstractNBTNSSTag<Item> {

	private NSSItem(@NotNull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundTag nbt) {
		super(resourceLocation, isTag, nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemStack}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ItemStack stack) {
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
	@NotNull
	public static NSSItem createItem(@NotNull ItemLike itemProvider) {
		return createItem(itemProvider, null);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemLike} and an optional {@link CompoundTag}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ItemLike itemProvider, @Nullable CompoundTag nbt) {
		Item item = itemProvider.asItem();
		if (item == Items.AIR) {
			throw new IllegalArgumentException("Can't make NSSItem with empty stack");
		}
		ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
		if (registryName == null) {
			throw new IllegalArgumentException("Can't make an NSSItem with an unregistered item");
		}
		//This should never be null, or it would have crashed on being registered
		return createItem(registryName, nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceLocation}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ResourceLocation itemID) {
		return createItem(itemID, null);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceLocation} and an optional {@link CompoundTag}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ResourceLocation itemID, @Nullable CompoundTag nbt) {
		return new NSSItem(itemID, false, nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing a tag from a {@link ResourceLocation}
	 */
	@NotNull
	public static NSSItem createTag(@NotNull ResourceLocation tagId) {
		return new NSSItem(tagId, true, null);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing a tag from a {@link TagKey<Item>}
	 */
	@NotNull
	public static NSSItem createTag(@NotNull TagKey<Item> tag) {
		return createTag(tag.location());
	}

	@Override
	protected boolean isInstance(AbstractNSSTag<?> o) {
		return o instanceof NSSItem;
	}

	@NotNull
	@Override
	public String getJsonPrefix() {
		//We prefer no prefix for NSSItem even though we do support ITEM|
		return "";
	}

	@NotNull
	@Override
	public String getType() {
		return "Item";
	}

	@NotNull
	@Override
	protected Optional<Either<Named<Item>, ITag<Item>>> getTag() {
		return getTag(ForgeRegistries.ITEMS);
	}

	@Override
	protected Function<Item, NormalizedSimpleStack> createNew() {
		return NSSItem::createItem;
	}
}