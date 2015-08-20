package moze_intel.projecte.api.item;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * This interface specifies items that perform a specific function every tick when inside an activated Dark Matter Pedestal
 *
 * @author williewillus
 */
public interface IPedestalItem {

	String TOOLTIPDISABLED = EnumChatFormatting.RED + StatCollector.translateToLocal("pe.pedestal.item_disabled");

	/***
	 * Called on both client and server each time an active DMPedestalTile ticks with this item inside
	 */
    void updateInPedestal(World world, int x, int y, int z);

	/***
	 * Called clientside when inside the pedestal gui to add special function descriptions
	 * @return Brief strings describing the item's function in an activated pedestal
	 */
	List<String> getPedestalDescription();
}
