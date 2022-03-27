package moze_intel.projecte.gameObjs.items.blocks;

import moze_intel.projecte.gameObjs.EnumFuelType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemFuelBlock extends BlockItem {

	@NotNull
	private final EnumFuelType type;

	public ItemFuelBlock(Block block, Properties props, @NotNull EnumFuelType type) {
		super(block, props);
		this.type = type;
	}

	@Override
	public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		int burnTime = type.getBurnTime();
		return burnTime == -1 ? -1 : burnTime * 9;
	}
}