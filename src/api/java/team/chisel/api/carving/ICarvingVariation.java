package team.chisel.api.carving;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * Represents a variation of a chiselable block.
 */
public interface ICarvingVariation {

	/**
	 * The base block of this variation.
	 * 
	 * @return A {@link Block} that is the base of this variation
	 */
	@Nullable
	Block getBlock();

	IBlockState getBlockState();

	/**
	 * The {@link ItemStack} of this variation. This can be customized to allow for variations that differ on NBT alone.
	 * <p>
	 * This ItemStack should be a copy (or a new instance) of the stack, callers of this method are not required to leave the stack unmodified.
	 * 
	 * @return An {@link ItemStack} that represents this variation.
	 */
	@Nonnull
	ItemStack getStack();

	/**
	 * The "order" of this variation. Represents its position in the list of variations held by a group.
	 * 
	 * @return An integer to represent the position of this variation in the list of all variations in the group
	 */
	int getOrder();
}
