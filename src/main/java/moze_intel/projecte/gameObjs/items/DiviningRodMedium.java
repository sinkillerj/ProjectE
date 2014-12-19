package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IModeChanger;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SwingItemPKT;
import moze_intel.projecte.utils.CoordinateBox;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class DiviningRodMedium extends ItemPE implements IModeChanger
{
	protected String[] modes;
	
	public DiviningRodMedium()
	{
		this.setUnlocalizedName("divining_rod_2");
		modes = new String[] {"3x3x3", "16x3x3"};
	}
	
	public DiviningRodMedium(String[] modes)
	{
		this.modes = modes;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) return stack;
		
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
		int fuelCost = getMode(stack) == 1 ? 32 : 16;
		
		if (mop != null && mop.typeOfHit.equals(MovingObjectType.BLOCK))
		{
			PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
			long totalEmc = 0;
			int max = 0;
			int numBlocks = 0;
			
			int range = getMode(stack) == 1 ? 16 : 3; 
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
						
						if (drops.isEmpty())
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
										blockEmc = currentValue;
										totalEmc += currentValue;
									}	
								}
							}
						}
						else
						{
							totalEmc += blockEmc;
						}
						
						if (blockEmc > max)
						{
							max = blockEmc;
						}
						
						numBlocks++;	
					}
			
			player.addChatComponentMessage(new ChatComponentText(String.format("Average EMC for %d blocks: %,d", numBlocks, (totalEmc / numBlocks))));
			player.addChatComponentMessage(new ChatComponentText(String.format("Max EMC: %,d", max)));
		}
		
		return stack;
	}
	
	public CoordinateBox getBoxFromDirection(ForgeDirection direction, Coordinates coords, int range)
	{
		range--;
		
		if (direction.offsetX != 0)
		{
			if (direction.offsetX > 0)
				return new CoordinateBox(coords.x - range, coords.y - 1, coords.z - 1, coords.x, coords.y + 1, coords.z + 1);
			else return new CoordinateBox(coords.x, coords.y - 1, coords.z - 1, coords.x + range, coords.y + 1, coords.z + 1);
		}
		else if (direction.offsetY != 0)
		{
			if (direction.offsetY > 0)
				return new CoordinateBox(coords.x - 1, coords.y - range, coords.z - 1, coords.x + 1, coords.y, coords.z + 1);
			else return new CoordinateBox(coords.x - 1, coords.y, coords.z - 1, coords.x + 1, coords.y + range, coords.z + 1);
		}
		else
		{
			if (direction.offsetZ > 0)
				return new CoordinateBox(coords.x - 1, coords.y - 1, coords.z - range, coords.x + 1, coords.y + 1, coords.z);
			else return new CoordinateBox(coords.x - 1, coords.y - 1, coords.z, coords.x + 1, coords.y + 1, coords.z + range);
		}
	}
	
	public byte getMode(ItemStack stack)
	{
		return stack.stackTagCompound.getByte("Mode");
	}
	
	public void changeMode(ItemStack stack)
	{
		if (getMode(stack) == 1)
		{
			stack.stackTagCompound.setByte("Mode", (byte) 0);
		}
		else 
		{
			stack.stackTagCompound.setByte("Mode", (byte) 1);
		}
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		changeMode(stack);
		player.addChatComponentMessage(new ChatComponentText("Changed mode to: "+modes[getMode(stack)]));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		if (stack.hasTagCompound())
		{
			list.add("Mode: "+EnumChatFormatting.AQUA+modes[getMode(stack)]);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("divining2"));
	}
}
