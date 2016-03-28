package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import moze_intel.projecte.utils.AchievementHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class RedPick extends DarkPick
{
	public RedPick()
	{
		super("rm_pick", (byte)3, new String[] {
				I18n.translateToLocal("pe.redpick.mode1"), I18n.translateToLocal("pe.redpick.mode2"),
				I18n.translateToLocal("pe.redpick.mode3"), I18n.translateToLocal("pe.redpick.mode4")});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "pickaxe";
		this.harvestMaterials.add(Material.iron);
		this.harvestMaterials.add(Material.anvil);
		this.harvestMaterials.add(Material.rock);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		super.onCreated(stack, world, player);
		
		if (!world.isRemote)
		{
			player.addStat(AchievementHandler.RM_PICK, 1);
		}
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state)
	{
		Block b = state.getBlock();
		if ((b == ObjHandler.matterBlock && state.getValue(MatterBlock.TIER_PROP) == MatterBlock.EnumMatterType.RED_MATTER || b == ObjHandler.rmFurnaceOff || b == ObjHandler.rmFurnaceOn))
		{
			return 1200000.0F;
		}
		return super.getStrVsBlock(stack, state);
	}
}
