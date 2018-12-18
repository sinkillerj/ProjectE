package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.api.state.enums.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class RedPick extends DarkPick
{
	public RedPick()
	{
		super("rm_pick", (byte)3, new String[] {
				"pe.redpick.mode1", "pe.redpick.mode2",
				"pe.redpick.mode3", "pe.redpick.mode4"});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.toolClasses.add("pickaxe");
		this.harvestMaterials.add(Material.IRON);
		this.harvestMaterials.add(Material.ANVIL);
		this.harvestMaterials.add(Material.ROCK);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state)
	{
		Block b = state.getBlock();
		if (b == ObjHandler.matterBlock && state.getValue(PEStateProps.TIER_PROP) == EnumMatterType.RED_MATTER
				|| b == ObjHandler.rmFurnaceOff
				|| b == ObjHandler.rmFurnaceOn)
		{
			return 1200000.0F;
		}
		return super.getDestroySpeed(stack, state);
	}
}
