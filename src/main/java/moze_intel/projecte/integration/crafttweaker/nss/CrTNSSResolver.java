package moze_intel.projecte.integration.crafttweaker.nss;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.op.IDataOps;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
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
		return deserialize(JsonOps.INSTANCE, new JsonPrimitive(representation));
	}

	/**
	 * Creates a {@link NormalizedSimpleStack} based on its string representation.
	 *
	 * @param representation String representation as would be found in custom_emc.json
	 *
	 * @return A {@link NormalizedSimpleStack} based on its string representation.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack deserialize(IData representation) {
		return deserialize(IDataOps.INSTANCE, representation);
	}

	private static <T> NormalizedSimpleStack deserialize(DynamicOps<T> ops, T input) {
		DataResult<NormalizedSimpleStack> result = IPECodecHelper.INSTANCE.nssCodec().parse(ops, input);
		Optional<PartialResult<NormalizedSimpleStack>> error = result.error();
		if (error.isPresent()) {
			throw new IllegalArgumentException("Error deserializing NSS representation: " + error.get().message());
		}
		return result.result().orElseThrow();
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
	 * Create a {@link NormalizedSimpleStack} representing a given {@link KnownTag<Item>}.
	 *
	 * @param tag Item Tag to represent
	 *
	 * @return A {@link NormalizedSimpleStack} representing a given {@link KnownTag<Item>}.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack fromItemTag(KnownTag<Item> tag) {
		if (tag.exists()) {
			return NSSItem.createTag(tag.id());
		}
		throw new IllegalArgumentException("Item tag " + tag.getCommandString() + " does not exist.");
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
		return NSSFluid.createFluid(stack.<FluidStack>getInternal());
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
	 * Create a {@link NormalizedSimpleStack} representing a given {@link KnownTag<Fluid>}.
	 *
	 * @param tag Fluid Tag to represent
	 *
	 * @return A {@link NormalizedSimpleStack} representing a given {@link KnownTag<Fluid>}.
	 */
	@ZenCodeType.Method
	public static NormalizedSimpleStack fromFluidTag(KnownTag<Fluid> tag) {
		if (tag.exists()) {
			return NSSFluid.createTag(tag.id());
		}
		throw new IllegalArgumentException("Fluid tag " + tag.getCommandString() + " does not exist.");
	}
}