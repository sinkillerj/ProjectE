package moze_intel.projecte.utils;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * Helper class for anything having to do with Fluids
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class FluidHelper
{
	public static boolean canFillTank(IFluidHandler tank, Fluid fluid, EnumFacing side)
	{
		if (tank.canFill(side, fluid))
		{
			boolean canFill = false;

			for (FluidTankInfo tankInfo : tank.getTankInfo(side))
			{
				if (tankInfo.fluid == null)
				{
					canFill = true;
					break;
				}

				if (tankInfo.fluid.getFluid() == fluid && tankInfo.fluid.amount < tankInfo.capacity)
				{
					canFill = true;
					break;
				}
			}

			return canFill;
		}

		return false;
	}

	public static void fillTank(IFluidHandler tank, Fluid fluid, EnumFacing side, int quantity)
	{
		tank.fill(side, new FluidStack(fluid, quantity), true);
	}
}
