package team.chisel.api.carving;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IChiselMode {

	/**
	 * NOTE: This method is responsible for increasing the "Blocks Chiseled" statistic found in the Statistics class.
	 */
	void chiselAll(EntityPlayer player, World world, int x, int y, int z, EnumFacing side, ICarvingVariation variation);

	/**
	 * Implemented implicitly by enums. If your IChiselMode is not an enum constant, this needs to be implemented explicitly.
	 * 
	 * @return The name of the mode.
	 */
	String name();
}
