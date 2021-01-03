package moze_intel.projecte.integration.crafttweaker.nss;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.blamejared.crafttweaker.impl.tag.manager.TagManagerFluid;
import com.blamejared.crafttweaker.impl.tag.manager.TagManagerItem;
import com.google.gson.JsonParseException;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.emc.json.NSSSerializer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.NSSResolver")
public class CrTNSSResolver {

	@ZenCodeType.Method
	public static NSSCrT deserialize(String representation) {
		try {
			return new NSSCrT(NSSSerializer.INSTANCE.deserialize(representation));
		} catch (JsonParseException e) {
			throw new IllegalArgumentException("Error deserializing NSS string representation", e);
		}
	}

	@ZenCodeType.Method
	public static NSSCrT fromItem(Item item) {
		return new NSSCrT(NSSItem.createItem(item));
	}

	@ZenCodeType.Method
	public static NSSCrT fromItem(IItemStack stack) {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Cannot make an NSS Representation from an empty item stack.");
		}
		return new NSSCrT(NSSItem.createItem(stack.getInternal()));
	}

	@ZenCodeType.Method
	public static NSSCrT fromItemTag(MCTag<Item> tag) {
		ITag<Item> itemTag = TagManagerItem.INSTANCE.getInternal(tag);
		if (itemTag == null) {
			throw new IllegalArgumentException("Item tag " + tag.getCommandString() + " does not exist.");
		}
		return new NSSCrT(NSSItem.createTag(itemTag));
	}

	@ZenCodeType.Method
	public static NSSCrT fromFluid(IFluidStack stack) {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Cannot make an NSS Representation from an empty fluid stack.");
		}
		return new NSSCrT(NSSFluid.createFluid(stack.getInternal()));
	}

	@ZenCodeType.Method
	public static NSSCrT fromFluid(Fluid fluid) {
		if (fluid == Fluids.EMPTY) {
			throw new IllegalArgumentException("Cannot make an NSS Representation from an empty fluid.");
		}
		return new NSSCrT(NSSFluid.createFluid(fluid));
	}

	@ZenCodeType.Method
	public static NSSCrT fromFluidTag(MCTag<Fluid> tag) {
		ITag<Fluid> fluidTag = TagManagerFluid.INSTANCE.getInternal(tag);
		if (fluidTag == null) {
			throw new IllegalArgumentException("Fluid tag " + tag.getCommandString() + " does not exist.");
		}
		return new NSSCrT(NSSFluid.createTag(fluidTag));
	}
}