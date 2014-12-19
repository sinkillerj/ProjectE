package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SwingItemPKT;
import moze_intel.projecte.utils.Comparators;
import moze_intel.projecte.utils.CoordinateBox;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class DiviningRodHigh extends DiviningRodMedium
{
	public DiviningRodHigh()
	{
		super(new String[] {"3x3x3", "16x3x3", "64x3x3"});
		this.setUnlocalizedName("divining_rod_3");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) 
		{
			return stack;
		}
		
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
		
		if (mop != null && mop.typeOfHit.equals(MovingObjectType.BLOCK))
		{
			PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
			List<Integer> emcValues = new ArrayList<Integer>();
			long totalEmc = 0;
			int numBlocks = 0;
			
			byte mode = getMode(stack);
			int range = mode == 2 ? 64 : mode == 1 ? 16 : 3; 
			CoordinateBox box = getBoxFromDirection(ForgeDirection.getOrientation(mop.sideHit), new Coordinates(mop), range);
			
			for (int i = (int) box.minX; i <= box.maxX; i++)
				for (int j = (int) box.minY; j <= box.maxY; j++)
					for (int k = (int) box.minZ; k <= box.maxZ; k++)
					{
						Block block = world.getBlock(i, j, k);
						
						if (block == Blocks.air)
						{
							continue;
						}
						
						ArrayList<ItemStack> drops = block.getDrops(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
						
						if (drops.size() == 0)
						{
							continue;
						}
						
						int blockEmc = Utils.getEmcValue(drops.get(0));
						
						if (blockEmc == 0)
						{
							HashMap<ItemStack, ItemStack> map = (HashMap) FurnaceRecipes.smelting().getSmeltingList();
							
							for (Entry<ItemStack, ItemStack> entry : map.entrySet())
							{
								if (entry == null || entry.getKey() == null)
								{
									continue;
								}
								
								if (entry.getKey().getItem() == drops.get(0).getItem())
								{
									int currentValue = Utils.getEmcValue(entry.getValue());
									
									if (currentValue != 0)
									{
										if (!emcValues.contains(currentValue))
										{
											emcValues.add(currentValue);
										}
										
										totalEmc += currentValue;
									}	
								}
							}
						}
						else
						{
							if (!emcValues.contains(blockEmc))
							{
								emcValues.add(blockEmc);
							}
							
							totalEmc += blockEmc;
						}
						
						numBlocks++;
					}
			
			
			int[] maxValues = new int[3];
			
			for (int i = 0; i < 3; i++)
			{
				maxValues[i] = 1;
			}
			
			Collections.sort(emcValues, Comparators.INT_DESCENDING);
			
			int num = emcValues.size() >= 3 ? 3 : emcValues.size();
			
			for (int i = 0; i < num; i++)
			{
				maxValues[i] = emcValues.get(i);
			}
			
			player.addChatComponentMessage(new ChatComponentText(String.format("Average EMC for %d blocks: %,d", numBlocks, (totalEmc / numBlocks))));
			player.addChatComponentMessage(new ChatComponentText(String.format("Max EMC: %,d", maxValues[0])));
			player.addChatComponentMessage(new ChatComponentText(String.format("Second Max EMC: %,d", maxValues[1])));
			player.addChatComponentMessage(new ChatComponentText(String.format("Third Max EMC: %,d", maxValues[2])));
		}
		
		return stack;
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		byte mode = this.getMode(stack);
		
		if (mode == 2)
		{
			stack.stackTagCompound.setByte("Mode", (byte) 0);
		}
		else
		{
			stack.stackTagCompound.setByte("Mode", (byte) (mode + 1));
		}
		
		player.addChatComponentMessage(new ChatComponentText("Changed mode to: " + modes[getMode(stack)]));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("divining3"));
	}
}
