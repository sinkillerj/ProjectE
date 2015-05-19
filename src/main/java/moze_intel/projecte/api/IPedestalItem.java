package moze_intel.projecte.api;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Used by items that provide special functionality in an activated DM pedestal.
 */
public interface IPedestalItem {

	String TOOLTIPDISABLED = EnumChatFormatting.RED + StatCollector.translateToLocal("pe.pedestal.item_disabled");
	/***
	 * Called on both client and server each time an active DMPedestalTile ticks with this item inside.
	 * @param world
	 * @param pos
	 */
    void updateInPedestal(World world, BlockPos pos);

	/***
	 * Called clientside when inside the pedestal gui to add special function descriptions
	 * @return Brief string describing item function in pedestal.
	 */
	List<String> getPedestalDescription();
}
