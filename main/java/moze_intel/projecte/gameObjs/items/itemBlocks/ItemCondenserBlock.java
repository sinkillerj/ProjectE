package moze_intel.projecte.gameObjs.items.itemBlocks;

import moze_intel.projecte.utils.AchievementHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCondenserBlock extends ItemBlock
{
	public ItemCondenserBlock(Block block) 
	{
		super(block);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		if (world != null)
		{
			player.addStat(AchievementHandler.CONDENSER, 1);
		}
	}
}
