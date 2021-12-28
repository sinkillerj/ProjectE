/*package moze_intel.projecte.integration.crafttweaker.nss;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.blamejared.crafttweaker_annotations.annotations.TypedExpansion;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import org.openzen.zencode.java.ZenCodeType;

/**
 * Helper classes to expand various basic CrT types to allow for implicit casting
 *
public class ExpandCrTTypes {

	private ExpandCrTTypes() {
	}

	@ZenRegister
	@TypedExpansion(Item.class)
	public static class ItemExpansion {

		private ItemExpansion() {
		}

		/**
		 * Allows for casting {@link Item}s to {@link NormalizedSimpleStack} without even needing to specify the cast.
		 *
		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(Item _this) {
			return CrTNSSResolver.fromItem(_this);
		}
	}

	@ZenRegister
	@TypedExpansion(IItemStack.class)
	public static class IItemStackExpansion {

		private IItemStackExpansion() {
		}

		/**
		 * Allows for casting {@link IItemStack}s to {@link NormalizedSimpleStack} without even needing to specify the cast.
		 *
		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(IItemStack _this) {
			return CrTNSSResolver.fromItem(_this);
		}
	}

	@ZenRegister
	@ZenCodeType.Expansion("crafttweaker.api.tag.MCTag<crafttweaker.api.item.MCItemDefinition>")
	public static class ItemTagExpansion {

		private ItemTagExpansion() {
		}

		/**
		 * Allows for casting {@link MCTag<Item>}s to {@link NormalizedSimpleStack} without even needing to specify the cast.
		 *
		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(MCTag<Item> _this) {
			return CrTNSSResolver.fromItemTag(_this);
		}
	}

	@ZenRegister
	@TypedExpansion(Fluid.class)
	public static class FluidExpansion {

		private FluidExpansion() {
		}

		/**
		 * Allows for casting {@link Fluid}s to {@link NormalizedSimpleStack} without even needing to specify the cast.
		 *
		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(Fluid _this) {
			return CrTNSSResolver.fromFluid(_this);
		}
	}

	@ZenRegister
	@TypedExpansion(IFluidStack.class)
	public static class IFluidStackExpansion {

		private IFluidStackExpansion() {
		}

		/**
		 * Allows for casting {@link IFluidStack}s to {@link NormalizedSimpleStack} without even needing to specify the cast.
		 *
		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(IFluidStack _this) {
			return CrTNSSResolver.fromFluid(_this);
		}
	}

	@ZenRegister
	@ZenCodeType.Expansion("crafttweaker.api.tag.MCTag<crafttweaker.api.fluid.MCFluid>")
	public static class FluidTagExpansion {

		private FluidTagExpansion() {
		}

		/**
		 * Allows for casting {@link MCTag<Fluid>}s to {@link NormalizedSimpleStack} without even needing to specify the cast.
		 *
		@ZenCodeType.Caster(implicit = true)
		public static NormalizedSimpleStack asNormalizedSimpleStack(MCTag<Fluid> _this) {
			return CrTNSSResolver.fromFluidTag(_this);
		}
	}
}*/