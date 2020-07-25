package moze_intel.projecte.api.capabilities.item;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

/**
 * This interface specifies items that perform a specific function every tick when inside an activated Dark Matter Pedestal
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link ItemStack#getCapability(Capability, Direction)}.
 *
 * @author williewillus
 */
public interface IPedestalItem {

	ITextComponent TOOLTIP_DISABLED = new TranslationTextComponent("pe.pedestal.item_disabled").mergeStyle(TextFormatting.RED);

	/***
	 * Called on both client and server each time an active DMPedestalTile ticks with this item inside
	 */
	void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos);

	/***
	 * Called clientside when inside the pedestal gui to add special function descriptions
	 * @return Brief strings describing the item's function in an activated pedestal
	 */
	@Nonnull
	List<ITextComponent> getPedestalDescription();
}