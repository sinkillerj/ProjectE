package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.items.ItemCharge;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DarkHoe extends ItemCharge
{
	public DarkHoe() 
	{
		super("dm_hoe", (byte) 3);
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		if (!player.canPlayerEdit(x, y, z, par7, stack))
		{
			return false;
		}
		else
		{
			UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);
			if (MinecraftForge.EVENT_BUS.post(event))
			{
				return false;
			}

			if (event.getResult() == Result.ALLOW)
			{
				return true;
			}

			byte charge = this.getCharge(stack);
			boolean hasAction = false;
			boolean hasSoundPlayed = false;
			
			for (int i = x - charge; i <= x + charge; i++)
				for (int j = z - charge; j <= z + charge; j++)
				{
					Block block = world.getBlock(i, y, j);
					
					if (world.getBlock(i, y + 1, j).isAir(world, i, y + 1, j) && (block == Blocks.grass || block == Blocks.dirt))
					{
						Block block1 = Blocks.farmland;
						
						if (!hasSoundPlayed)
						{
							world.playSoundEffect((double)((float)i + 0.5F), (double)((float)y + 0.5F), (double)((float)j + 0.5F), block1.stepSound.getStepResourcePath(), (block1.stepSound.getVolume() + 1.0F) / 2.0F, block1.stepSound.getPitch() * 0.8F);
							hasSoundPlayed = true;
						}
						
						if (world.isRemote)
						{
							return true;
						}
						else
						{
							 world.setBlock(i, y, j, block1);
							 
							 if (!hasAction)
							 {
								 hasAction = true;
							 }
						}
					}
				}
			
			return hasAction;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("dm_tools", "hoe"));
	}
}
