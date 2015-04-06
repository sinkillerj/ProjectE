package moze_intel.projecte.utils;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SetFlyPKT;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public final class Utils 
{


	public static Object getRandomListEntry(List<?> list, Object toExclude)
	{
		Object obj;
		
		do
		{
			int random = randomIntInRange(list.size() - 1, 0);
			obj = list.get(random);
		}
		while(obj.equals(toExclude));
		
		return obj;
	}
	
	public static Entity getNewEntityInstance(Class c, World world)
	{
		try 
		{
			Constructor constr = c.getConstructor(World.class);
			Entity ent = (Entity) constr.newInstance(world);
			
			if (ent instanceof EntitySkeleton)
			{
				if (world.rand.nextInt(2) == 0)
				{
					((EntitySkeleton) ent).setSkeletonType(1);
					ent.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
				}
				else 
				{
					ent.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
				}
			}
			else if (ent instanceof EntityPigZombie)
			{
				ent.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
			}
			
			return ent;
		}
		catch (Exception e)
		{
			PELogger.logFatal("Could not create new entity instance for: "+c.getCanonicalName());
			e.printStackTrace();
		}
		
		return null;
	}

	public static ArrayList<ItemStack> getBlockDrops(World world, EntityPlayer player, Block block, ItemStack stack, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		
		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0 && block.canSilkHarvest(world, player, x, y, z, meta))
		{
			ArrayList<ItemStack> list = new ArrayList<ItemStack>();
			list.add(new ItemStack(block, 1, meta));
			return list;
		}
		
		return block.getDrops(world, x, y, z, meta, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
	}

	public static boolean canFillTank(IFluidHandler tank, Fluid fluid, int side)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(side);

		if (tank.canFill(dir, fluid))
		{
			boolean canFill = false;

			for (FluidTankInfo tankInfo : tank.getTankInfo(dir))
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

	public static void fillTank(IFluidHandler tank, Fluid fluid, int side, int quantity)
	{
		tank.fill(ForgeDirection.getOrientation(side), new FluidStack(fluid, quantity), true);
	}

	public static void closeStream(Closeable c)
	{
		if (c != null)
		{
			try
			{
				c.close();
			}
			catch (IOException e)
			{
				PELogger.logFatal("IO Error: couldn't close stream!");
				e.printStackTrace();
			}
		}
	}

	public static int randomIntInRange(int max, int min)
	{
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	public static double tickToSec(int ticks)
	{
		return ticks / 20.0D;
	}

	public static String tickToSecFormatted(int ticks)
	{
		double result = tickToSec(ticks);
		if (result == 0.0D)
		{
			return result + " " + StatCollector.translateToLocal("pe.misc.seconds") + " (" + StatCollector.translateToLocal("pe.misc.every_tick") + ")";
		}
		else
		{
			return result + " " + StatCollector.translateToLocal("pe.misc.seconds");
		}
	}

	public static int secToTicks(double secs)
	{
		return (int) Math.round(secs * 20.0D);
	}
}
