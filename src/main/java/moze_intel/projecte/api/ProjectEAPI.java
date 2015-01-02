package moze_intel.projecte.api;

import cpw.mods.fml.common.event.FMLInterModComms;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Class for basic mod interactions with ProjectE.<br>
 * For now, it's very simplistic, will be expanded in the future.<br>
 */
public final class ProjectEAPI
{
	/**
	 * Register an EMC value for the specified itemstack.<br>
	 * If the emcValue is <= 0, then the ItemStack will be blacklisted from any EMC mapping.<br>
	 * The ItemStack's NBT data is completely ignored in registration.<br>
	 * Users can still modify inter-mod EMC registration via command/configuration file.<br>
	 * Can be called during pre-init, init or post-init.
	 */
	public static void registerCustomEMC(ItemStack stack, int emcValue)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		stack.writeToNBT(nbt);
		nbt.setInteger("EMC", emcValue);
		
		FMLInterModComms.sendMessage("ProjectE", "registeremc", nbt);
	}

	/**
	 * Blacklist an entity for the interdiction torches.<br> 
	 * Can be called during pre-init, init or post-init.
	 */
	public static void registerInterdictionBlacklist(Class entityClass)
	{
		FMLInterModComms.sendMessage("ProjectE", "interdictionblacklist", entityClass.getCanonicalName());
	}

	/**
	 * Make an ItemStack keep it's NBT data when condensed.<br>
	 * Can be called during pre-init, init or post-init.
	 */
	public static void registerCondenserNBTException(ItemStack stack)
	{
		FMLInterModComms.sendMessage("ProjectE", "condensernbtcopy", stack);
	}
}
