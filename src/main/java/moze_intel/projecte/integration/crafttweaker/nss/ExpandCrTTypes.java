package moze_intel.projecte.integration.crafttweaker.nss;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.blamejared.crafttweaker_annotations.annotations.TypedExpansion;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import org.openzen.zencode.java.ZenCodeType;

/**
 * Helper classes to expand various basic CrT types to allow for implicit casting
 */
public class ExpandCrTTypes {

	@ZenRegister
	@TypedExpansion(Item.class)
	public static class ItemExpansion {

		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(Item _this) {
			return CrTNSSResolver.fromItem(_this);
		}
	}

	@ZenRegister
	@TypedExpansion(IItemStack.class)
	public static class IItemStackExpansion {

		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(IItemStack _this) {
			return CrTNSSResolver.fromItem(_this);
		}
	}

	@ZenRegister
	@ZenCodeType.Expansion("crafttweaker.api.tag.MCTag<crafttweaker.api.item.MCItemDefinition>")
	public static class ItemTagExpansion {

		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(MCTag<Item> _this) {
			return CrTNSSResolver.fromItemTag(_this);
		}
	}

	@ZenRegister
	@TypedExpansion(Fluid.class)
	public static class FluidExpansion {

		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(Fluid _this) {
			return CrTNSSResolver.fromFluid(_this);
		}
	}

	@ZenRegister
	@TypedExpansion(IFluidStack.class)
	public static class IFluidStackExpansion {

		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(IFluidStack _this) {
			return CrTNSSResolver.fromFluid(_this);
		}
	}

	@ZenRegister
	@ZenCodeType.Expansion("crafttweaker.api.tag.MCTag<crafttweaker.api.fluid.MCFluid>")
	public static class FluidTagExpansion {

		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(MCTag<Fluid> _this) {
			return CrTNSSResolver.fromFluidTag(_this);
		}
	}
}