package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.api.state.enums.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class RedHammer extends DarkHammer
{
	public RedHammer(Builder builder)
	{
		super(builder, (byte)3, new String[]{});
		this.peToolMaterial = EnumMatterType.RED_MATTER;
		this.harvestMaterials.add(Material.IRON);
		this.harvestMaterials.add(Material.ANVIL);
		this.harvestMaterials.add(Material.ROCK);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state)
	{
		Block block = state.getBlock();
		if ((block == ObjHandler.rmBlock) || block == ObjHandler.rmFurnaceOff)
		{
			return 1200000.0F;
		}

		return super.getDestroySpeed(stack, state);
	}
}
