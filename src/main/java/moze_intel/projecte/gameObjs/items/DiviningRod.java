package moze_intel.projecte.gameObjs.items;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DiviningRod extends ItemPE implements IModeChanger
{
	// Modes should be in the format depthx3x3
	private final String[] modes;

	public DiviningRod(Builder builder, String[] modeDesc)
	{
		super(builder);
		modes = modeDesc;
	}
	
	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		World world = ctx.getWorld();
		EntityPlayer player = ctx.getPlayer();

		if (world.isRemote)
		{
			return EnumActionResult.SUCCESS;
		}

		PlayerHelper.swingItem(player, hand);
		List<Long> emcValues = new ArrayList<>();
		long totalEmc = 0;
		int numBlocks = 0;

		byte mode = getMode(ctx.getItem());
		int depth = getDepthFromMode(mode);
		AxisAlignedBB box = WorldHelper.getDeepBox(ctx.getPos(), ctx.getFace(), depth);

		for (BlockPos digPos : WorldHelper.getPositionsFromBox(box))
		{
			IBlockState state = world.getBlockState(digPos);
			Block block = state.getBlock();

			if (world.isAirBlock(digPos))
			{
				continue;
			}

			NonNullList<ItemStack> drops = NonNullList.create();
			block.getDrops(state, drops, world, digPos, 0);

			if (drops.isEmpty())
			{
				continue;
			}

			ItemStack blockStack = drops.get(0);
			long blockEmc = EMCHelper.getEmcValue(blockStack);

			if (blockEmc == 0)
			{
				Map<ItemStack, ItemStack> map = FurnaceRecipes.instance().getSmeltingList();

				for (Entry<ItemStack, ItemStack> entry : map.entrySet())
				{
					if (entry == null || entry.getKey().isEmpty())
					{
						continue;
					}

					if (ItemHelper.areItemStacksEqualIgnoreNBT(entry.getKey(), blockStack))
					{
						long currentValue = EMCHelper.getEmcValue(entry.getValue());

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
			return EnumActionResult.FAIL;
		}

		long[] maxValues = new long[3];

		for (int i = 0; i < 3; i++)
		{
			maxValues[i] = 1;
		}

		emcValues.sort(Comparator.reverseOrder());

		int num = emcValues.size() >= 3 ? 3 : emcValues.size();

		for (int i = 0; i < num; i++)
		{
			maxValues[i] = emcValues.get(i);
		}

		player.sendMessage(new TextComponentTranslation("pe.divining.avgemc", numBlocks, (totalEmc / numBlocks)));

		if (this == ObjHandler.dRod2 || this == ObjHandler.dRod3)
		{
			player.sendMessage(new TextComponentTranslation("pe.divining.maxemc", maxValues[0]));
		}

		if (this == ObjHandler.dRod3)
		{
			player.sendMessage(new TextComponentTranslation("pe.divining.secondmax", maxValues[1]));
			player.sendMessage(new TextComponentTranslation("pe.divining.thirdmax", maxValues[2]));
		}

		return EnumActionResult.SUCCESS;
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
	public byte getMode(@Nonnull ItemStack stack)
	{
		return ItemHelper.getOrCreateCompound(stack).getByte(TAG_MODE);
	}

	@Override
	public boolean changeMode(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		if (modes.length == 1)
		{
			return false;
		}
		if (getMode(stack) == modes.length - 1)
		{
			ItemHelper.getOrCreateCompound(stack).putByte(TAG_MODE, ((byte) 0));
		}
		else
		{
			ItemHelper.getOrCreateCompound(stack).putByte(TAG_MODE, ((byte) (getMode(stack) + 1)));
		}

		player.sendMessage(new TextComponentTranslation("pe.item.mode_switch", modes[getMode(stack)]));

		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags)
	{
		list.add(new TextComponentTranslation("pe.item.mode")
				.appendText(": ")
				.appendSibling(new TextComponentString(modes[getMode(stack)])
						.setStyle(new Style().setColor(TextFormatting.AQUA))));
	}
}
