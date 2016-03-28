package moze_intel.projecte.api.item;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.List;

/**
 * This interface specifies items that perform a specific function every tick when inside an activated Dark Matter Pedestal
 *
 * @author williewillus
 */
public interface IPedestalItem {

	String TOOLTIPDISABLED = TextFormatting.RED + I18n.translateToLocal("pe.pedestal.item_disabled");

	/***
	 * Called on both client and server each time an active DMPedestalTile ticks with this item inside
	 */
    void updateInPedestal(World world, BlockPos pos);

	/***
	 * Called clientside when inside the pedestal gui to add special function descriptions
	 * @return Brief strings describing the item's function in an activated pedestal
	 */
	List<String> getPedestalDescription();
}
