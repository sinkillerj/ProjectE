package moze_intel.projecte.utils;

import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Helper class for anything having to do with Fluids Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class FluidHelper {

	public static void tryFillTank(TileEntity tile, Fluid fluid, Direction side, int quantity) {
		tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)
				.ifPresent(handler -> handler.fill(new FluidStack(fluid, quantity), IFluidHandler.FluidAction.EXECUTE));
	}
}