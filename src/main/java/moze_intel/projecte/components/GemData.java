package moze_intel.projecte.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import moze_intel.projecte.PECore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import org.jetbrains.annotations.ApiStatus.Internal;

public record GemData(boolean isWhitelist, Set<ItemStack> whitelist, List<ItemStack> consumed) {

	public static final GemData EMPTY = new GemData(false, Collections.emptySet(), Collections.emptyList());

	public static final Codec<GemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("isWhitelist").forGetter(GemData::isWhitelist),
			ItemStack.CODEC.sizeLimitedListOf(9).promotePartial(error -> PECore.LOGGER.error("Failed to load gem whitelist: {}", error)).<Set<ItemStack>>xmap(list -> {
				if (list.isEmpty()) {
					return Collections.emptySet();
				}
				//Ensure the backing set when loading gem data from save properly handles comparing the type and ignores count
				Set<ItemStack> whitelist = ItemStackLinkedSet.createTypeAndComponentsSet();
				whitelist.addAll(list);
				return whitelist;
			}, List::copyOf).fieldOf("whitelist").forGetter(GemData::whitelist),
			ItemStack.CODEC.listOf().promotePartial(error -> PECore.LOGGER.error("Failed to load gem consumed contents: {}", error)).fieldOf("consumed").forGetter(GemData::consumed)
	).apply(instance, GemData::new));
	//TODO: Theoretically it will work as is because neo has builtin packet splitting for everything now
	// but we may want to evaluate moving this off to world save data (and also removing the ItemHelper method)
	public static final StreamCodec<RegistryFriendlyByteBuf, GemData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, GemData::isWhitelist,
			ItemStack.STREAM_CODEC.apply(
					//Like ItemStackLinkedSet.createTypeAndComponentsSet() except makes use of the expected size
					ByteBufCodecs.collection(size -> new ObjectLinkedOpenCustomHashSet<>(size, ItemStackLinkedSet.TYPE_AND_TAG))
			), GemData::whitelist,
			ItemStack.OPTIONAL_LIST_STREAM_CODEC, GemData::consumed,
			GemData::new
	);

	public GemData {
		whitelist = whitelist.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(whitelist);
		consumed = consumed.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(consumed);
	}

	public boolean whitelistMatches(ItemStack stack) {
		return !stack.isEmpty() && whitelist.contains(stack);
	}

	public GemData withWhitelist(boolean isWhitelist) {
		if (isWhitelist == isWhitelist()) {
			return this;
		}
		return new GemData(isWhitelist, whitelist, consumed);
	}

	public GemData withWhitelist(Set<ItemStack> whitelist) {
		Set<ItemStack> newWhitelist = ItemStackLinkedSet.createTypeAndComponentsSet();
		for (ItemStack stack : whitelist) {
			newWhitelist.add(stack.copyWithCount(1));
		}
		return withWhitelistSafe(newWhitelist);
	}

	@Internal
	public GemData withWhitelistSafe(Set<ItemStack> whitelist) {
		if (whitelist.isEmpty() && whitelist().isEmpty()) {
			return this;
		}
		return new GemData(isWhitelist, whitelist, consumed);
	}

	public GemData clearConsumed() {
		if (consumed().isEmpty()) {
			return this;
		}
		return new GemData(isWhitelist, whitelist, Collections.emptyList());
	}

	/**
	 * @param stack May be modified by the method
	 */
	public GemData addConsumed(ItemStack stack) {
		if (stack.isEmpty()) {
			//Nothing to do, just return this element
			return this;
		}
		//Note: We make a shallow copy so that we don't need to use as much memory keeping track of individual stacks
		// when we modify a stack we copy it then
		List<ItemStack> modifiableConsumed = new ArrayList<>(consumed);
		for (int i = 0, size = modifiableConsumed.size(); i < size; i++) {
			ItemStack existing = modifiableConsumed.get(i);
			int maxStackSize = existing.getMaxStackSize();
			if (existing.getCount() < maxStackSize && ItemStack.isSameItemSameComponents(existing, stack)) {
				int spaceAvailable = maxStackSize - existing.getCount();
				if (stack.getCount() <= spaceAvailable) {
					//Replace the element that we are merging into with a fresh copy so that we don't affect the old data
					existing = existing.copyWithCount(existing.getCount() + stack.getCount());
					modifiableConsumed.set(i, existing);
					return new GemData(isWhitelist, whitelist, modifiableConsumed);
				} else {
					//Replace the element that we are merging into with a fresh copy so that we don't affect the old data
					existing = existing.copyWithCount(existing.getCount() + spaceAvailable);
					modifiableConsumed.set(i, existing);
					//Note: We shrink the existing stack that we were passed as we can mutate it
					stack.shrink(spaceAvailable);
				}
			}
		}
		//Add whatever remains of the stack to the end of the list
		modifiableConsumed.add(stack);
		return new GemData(isWhitelist, whitelist, modifiableConsumed);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GemData other = (GemData) o;
		return isWhitelist == other.isWhitelist && whitelist.equals(other.whitelist) && ItemStack.listMatches(consumed, other.consumed);
	}

	@Override
	@SuppressWarnings("deprecation")
	public int hashCode() {
		int hash = 31 * Boolean.hashCode(isWhitelist) + hashStackSet(whitelist);
		return 31 * hash + ItemStack.hashStackList(consumed);
	}

	private static int hashStackSet(Set<ItemStack> set) {
		int i = 0;
		for (ItemStack stack : set) {
			i = i * 31 + ItemStack.hashItemAndComponents(stack);
		}
		return i;
	}
}
