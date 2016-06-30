package moze_intel.projecte.utils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Helper class for anything having to do with Fluids
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class FluidHelper
{
	public static void tryFillTank(TileEntity tile, Fluid fluid, EnumFacing side, int quantity)
	{
		if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side))
		{
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
			handler.fill(new FluidStack(fluid, quantity), true);
		}
	}
}
