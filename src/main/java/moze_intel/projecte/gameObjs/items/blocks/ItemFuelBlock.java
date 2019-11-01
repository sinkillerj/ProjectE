package moze_intel.projecte.gameObjs.items.blocks;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumFuelType;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class ItemFuelBlock extends BlockItem {

	@Nonnull
	private final EnumFuelType type;

	public ItemFuelBlock(Block block, Properties props, @Nonnull EnumFuelType type) {
		super(block, props);
		this.type = type;
	}

	@Override
	public int getBurnTime(ItemStack stack) {
		int burnTime = type.getBurnTime();
		return burnTime == -1 ? -1 : burnTime * 9;
	}
}