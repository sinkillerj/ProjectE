package moze_intel.projecte.api.capabilities.item;

import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.tile.IDMPedestal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;

/**
 * This interface specifies items that perform a specific function every tick when inside an activated Dark Matter Pedestal
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link net.minecraft.world.item.ItemStack#getCapability(Capability, net.minecraft.core.Direction)}.
 *
 * @author williewillus
 */
public interface IPedestalItem {

	/***
	 * Called on both client and server each time an active DMPedestalTile ticks with this item inside
	 * @return {@code true} if the passed in stack had its NBT modified to ensure it can be saved.
	 */
	<PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull PEDESTAL pedestal);

	/***
	 * Called clientside when inside the pedestal gui to add special function descriptions
	 * @return Brief strings describing the item's function in an activated pedestal
	 */
	@Nonnull
	List<Component> getPedestalDescription();
}