package moze_intel.projecte.gameObjs.items;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.utils.Comparators;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DiviningRodLow extends ItemPE implements IModeChanger
{
	// Modes should be in the format depthx3x3
	protected String[] modes;

	public DiviningRodLow()
	{
		this.setUnlocalizedName("divining_rod_1");
		modes = new String[] {"3x3x3"};
	}

	// Only for subclasses
	protected DiviningRodLow(String[] modeDesc)
	{
		modes = modeDesc;
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
		if (world.isRemote)
		{
			return stack;
		}

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);

		if (mop != null && mop.typeOfHit.equals(MovingObjectType.BLOCK))
		{
			PlayerHelper.swingItem(player);
			List<Integer> emcValues = Lists.newArrayList();
			long totalEmc = 0;
			int numBlocks = 0;

			byte mode = getMode(stack);
			int depth = getDepthFromMode(mode);
			AxisAlignedBB box = WorldHelper.getDeepBox(new Coordinates(mop), ForgeDirection.getOrientation(mop.sideHit), depth);

			for (int i = (int) box.minX; i <= box.maxX; i++)
				for (int j = (int) box.minY; j <= box.maxY; j++)
					for (int k = (int) box.minZ; k <= box.maxZ; k++)
					{
						Block block = world.getBlock(i, j, k);

						if (block == Blocks.air)
						{
							continue;
						}

						List<ItemStack> drops = block.getDrops(world, i, j, k, world.getBlockMetadata(i, j, k), 0);

						if (drops.size() == 0)
						{
							continue;
						}

						ItemStack blockStack = drops.get(0);
						int blockEmc = EMCHelper.getEmcValue(blockStack);

						if (blockEmc == 0)
						{
							Map<ItemStack, ItemStack> map = FurnaceRecipes.smelting().getSmeltingList();

							for (Entry<ItemStack, ItemStack> entry : map.entrySet())
							{
								if (entry == null || entry.getKey() == null)
								{
									continue;
								}

								if (ItemHelper.areItemStacksEqualIgnoreNBT(entry.getKey(), blockStack))
								{
									int currentValue = EMCHelper.getEmcValue(entry.getValue());

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


			if (numBlocks == 0)
			{
				return stack;
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

			player.addChatComponentMessage(new ChatComponentTranslation("pe.divining.avgemc", numBlocks, (totalEmc / numBlocks)));

			if (this instanceof DiviningRodMedium)
			{
				player.addChatComponentMessage(new ChatComponentTranslation("pe.divining.maxemc", maxValues[0]));
			}
			if (this instanceof DiviningRodHigh)
			{
				player.addChatComponentMessage(new ChatComponentTranslation("pe.divining.secondmax", maxValues[1]));
				player.addChatComponentMessage(new ChatComponentTranslation("pe.divining.thirdmax", maxValues[2]));
			}
		}

		return stack;

	}

	/**
	 * Gets the first number in the mode description.
	 */
	private int getDepthFromMode(byte mode)
	{
		String modeDesc = modes[mode];
		// Subtract one because of how the box method works
		return Integer.parseInt(modeDesc.substring(0, modeDesc.indexOf('x'))) - 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("divining1"));
	}

	@Override
	public byte getMode(ItemStack stack)
	{
		return stack.stackTagCompound.getByte("Mode");
	}

	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		if (modes.length == 1)
		{
			return;
		}
		if (getMode(stack) == modes.length - 1)
		{
			stack.stackTagCompound.setByte("Mode", ((byte) 0));
		}
		else
		{
			stack.stackTagCompound.setByte("Mode", ((byte) (getMode(stack) + 1)));
		}

		player.addChatComponentMessage(new ChatComponentTranslation("pe.item.mode_switch", modes[getMode(stack)]));
	}
}
