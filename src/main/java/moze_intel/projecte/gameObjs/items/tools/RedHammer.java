package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class RedHammer extends DarkHammer
{
	public RedHammer(Properties props)
	{
		super(props, (byte)3, new String[]{});
		this.peToolMaterial = EnumMatterType.RED_MATTER;
		this.harvestMaterials.add(Material.IRON);
		this.harvestMaterials.add(Material.ANVIL);
		this.harvestMaterials.add(Material.ROCK);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state)
	{
		Block block = state.getBlock();
		if ((block == ObjHandler.rmBlock) || block == ObjHandler.rmFurnaceOff)
		{
			return 1200000.0F;
		}

		return super.getDestroySpeed(stack, state);
	}
}
