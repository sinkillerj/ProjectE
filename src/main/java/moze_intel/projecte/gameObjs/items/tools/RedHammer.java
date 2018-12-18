package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.api.state.enums.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class RedHammer extends DarkHammer
{
	public RedHammer() 
	{
		super("rm_hammer", (byte)3, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.harvestMaterials.add(Material.IRON);
		this.harvestMaterials.add(Material.ANVIL);
		this.harvestMaterials.add(Material.ROCK);

		this.toolClasses.add("hammer");
		this.toolClasses.add("pickaxe");
		this.toolClasses.add("chisel");
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state)
	{
		Block block = state.getBlock();
		if ((block == ObjHandler.matterBlock && state.getValue(PEStateProps.TIER_PROP) == EnumMatterType.RED_MATTER)
				|| block == ObjHandler.rmFurnaceOff
				|| block == ObjHandler.rmFurnaceOn)
		{
			return 1200000.0F;
		}

		return super.getDestroySpeed(stack, state);
	}
}
