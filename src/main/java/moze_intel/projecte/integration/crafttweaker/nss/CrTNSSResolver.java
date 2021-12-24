package moze_intel.projecte.integration.crafttweaker.nss;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.blamejared.crafttweaker.impl.tag.manager.TagManagerFluid;
import com.blamejared.crafttweaker.impl.tag.manager.TagManagerItem;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.google.gson.JsonParseException;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.json.NSSSerializer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/ProjectE/NSSResolver")
@ZenCodeType.Name("mods.projecte.NSSResolver")
public class CrTNSSResolver {

	private CrTNSSResolver() {
	}

	/**
	 * Creates a {@link NormalizedSimpleStack} based on its string representation.
	 *
	 * @param representation String representation as would be found in custom_emc.json
	 *
	 * @return A {@link NormalizedSimpleStack} based on its string representation.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack deserialize(String representation) {
		try {
			return NSSSerializer.INSTANCE.deserialize(representation);
		} catch (JsonParseException e) {
			throw new IllegalArgumentException("Error deserializing NSS string representation", e);
		}
	}

	/**
	 * Create a {@link NormalizedSimpleStack} representing a given {@link Item}.
	 *
	 * @param item Item to represent
	 *
	 * @return A {@link NormalizedSimpleStack} representing a given {@link Item}.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack fromItem(Item item) {
		if (item == Items.AIR) {
			throw new IllegalArgumentException("Cannot make an NSS Representation from the empty item.");
		}
		return NSSItem.createItem(item);
	}

	/**
	 * Creates a {@link NormalizedSimpleStack} that matches the given stack's item and NBT.
	 *
	 * @param stack Stack to match the item and NBT of
	 *
	 * @return A {@link NormalizedSimpleStack} that matches the given stack's item and NBT.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack fromItem(IItemStack stack) {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Cannot make an NSS Representation from an empty item stack.");
		}
		return NSSItem.createItem(stack.getInternal());
	}

	/**
	 * Create a {@link NormalizedSimpleStack} representing a given {@link MCTag<Item>}.
	 *
	 * @param tag Item Tag to represent
	 *
	 * @return A {@link NormalizedSimpleStack} representing a given {@link MCTag<Item>}.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack fromItemTag(MCTag<Item> tag) {
		ITag<Item> itemTag = TagManagerItem.INSTANCE.getInternal(tag);
		if (itemTag == null) {
			throw new IllegalArgumentException("Item tag " + tag.getCommandString() + " does not exist.");
		}
		return NSSItem.createTag(itemTag);
	}

	/**
	 * Creates a {@link NormalizedSimpleStack} that matches the given stack's fluid and NBT.
	 *
	 * @param stack Stack to match the fluid and NBT of
	 *
	 * @return A {@link NormalizedSimpleStack} that matches the given stack's fluid and NBT.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack fromFluid(IFluidStack stack) {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Cannot make an NSS Representation from an empty fluid stack.");
		}
		return NSSFluid.createFluid(stack.getInternal());
	}

	/**
	 * Create a {@link NormalizedSimpleStack} representing a given {@link Fluid}.
	 *
	 * @param fluid Fluid to represent
	 *
	 * @return A {@link NormalizedSimpleStack} representing a given {@link Fluid}.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack fromFluid(Fluid fluid) {
		if (fluid == Fluids.EMPTY) {
			throw new IllegalArgumentException("Cannot make an NSS Representation from the empty fluid.");
		}
		return NSSFluid.createFluid(fluid);
	}

	/**
	 * Create a {@link NormalizedSimpleStack} representing a given {@link MCTag<Fluid>}.
	 *
	 * @param tag Fluid Tag to represent
	 *
	 * @return A {@link NormalizedSimpleStack} representing a given {@link MCTag<Fluid>}.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack fromFluidTag(MCTag<Fluid> tag) {
		ITag<Fluid> fluidTag = TagManagerFluid.INSTANCE.getInternal(tag);
		if (fluidTag == null) {
			throw new IllegalArgumentException("Fluid tag " + tag.getCommandString() + " does not exist.");
		}
		return NSSFluid.createTag(fluidTag);
	}
}