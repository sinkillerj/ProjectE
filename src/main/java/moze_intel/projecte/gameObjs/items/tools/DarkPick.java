package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.AchievementHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class DarkPick extends PEToolBase
{
	public DarkPick()
	{
		super("dm_pick", (byte)2, new String[] {
				StatCollector.translateToLocal("pe.darkpick.mode1"), StatCollector.translateToLocal("pe.darkpick.mode2"),
				StatCollector.translateToLocal("pe.darkpick.mode3"), StatCollector.translateToLocal("pe.darkpick.mode4")});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.pePrimaryToolClass = "pickaxe";
		this.harvestMaterials.add(Material.iron);
		this.harvestMaterials.add(Material.anvil);
		this.harvestMaterials.add(Material.rock);
	}

	// Only for RedPick
	protected DarkPick(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase eLiving)
	{
		digBasedOnMode(stack, world, block, x, y, z, eLiving);
		return true;
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if ((block == ObjHandler.matterBlock && metadata == 0) || block == ObjHandler.dmFurnaceOff || block == ObjHandler.dmFurnaceOn)
		{
			return 1200000.0F;
		}
		
		return super.getDigSpeed(stack, block, metadata);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		super.onCreated(stack, world, player);
		
		if (!world.isRemote)
		{
			player.addStat(AchievementHandler.DM_PICK, 1);
		}
	}
}
