package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.utils.AchievementHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class RedPick extends DarkPick
{
	public RedPick()
	{
		super("rm_pick", (byte)3, new String[] {
				StatCollector.translateToLocal("pe.redpick.mode1"), StatCollector.translateToLocal("pe.redpick.mode2"),
				StatCollector.translateToLocal("pe.redpick.mode3"), StatCollector.translateToLocal("pe.redpick.mode4")});
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
}
