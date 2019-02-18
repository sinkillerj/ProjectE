package moze_intel.projecte.api.item;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This interface specifies items that perform a specific function every tick when inside an activated Dark Matter Pedestal
 *
 * @author williewillus
 */
public interface IPedestalItem {
	ITextComponent TOOLTIPDISABLED = new TextComponentTranslation("pe.pedestal.item_disabled").applyTextStyle(TextFormatting.RED);

	/***
	 * Called on both client and server each time an active DMPedestalTile ticks with this item inside
	 */
    void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos);

	/***
	 * Called clientside when inside the pedestal gui to add special function descriptions
	 * @return Brief strings describing the item's function in an activated pedestal
	 */
	@Nonnull List<ITextComponent> getPedestalDescription();
}
