package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;

public class DiviningRod extends ItemPE implements IModeChanger
{
	// Modes should be in the format depthx3x3
	private final String[] modes;

	public DiviningRod(Properties props, String[] modeDesc)
	{
		super(props);
		modes = modeDesc;
	}
	
	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		World world = ctx.getWorld();
		PlayerEntity player = ctx.getPlayer();

		if (world.isRemote)
		{
			return ActionResultType.SUCCESS;
		}

		List<Long> emcValues = new ArrayList<>();
		long totalEmc = 0;
		int numBlocks = 0;

		byte mode = getMode(ctx.getItem());
		int depth = getDepthFromMode(mode);
		AxisAlignedBB box = WorldHelper.getDeepBox(ctx.getPos(), ctx.getFace(), depth);

		for (BlockPos digPos : WorldHelper.getPositionsFromBox(box))
		{
			BlockState state = world.getBlockState(digPos);

			if (world.isAirBlock(digPos))
			{
				continue;
			}

			List<ItemStack> drops = Block.getDrops(state, (ServerWorld) world, digPos,
					world.getTileEntity(digPos), player, ctx.getItem());

			if (drops.isEmpty())
			{
				continue;
			}

			ItemStack blockStack = drops.get(0);
			long blockEmc = EMCHelper.getEmcValue(blockStack);

			if (blockEmc == 0)
			{
				PrimitiveIterator.OfLong iter = world.getRecipeManager().getRecipes().stream()
						.filter(r -> r instanceof FurnaceRecipe && r.getIngredients().get(0).test(blockStack))
						.mapToLong(r -> EMCHelper.getEmcValue(r.getRecipeOutput()))
						.iterator();

				while (iter.hasNext())
				{
					long currentValue = iter.nextLong();
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
			return ActionResultType.FAIL;
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

		player.sendMessage(new TranslationTextComponent("pe.divining.avgemc", numBlocks, (totalEmc / numBlocks)));

		if (this == ObjHandler.dRod2 || this == ObjHandler.dRod3)
		{
			player.sendMessage(new TranslationTextComponent("pe.divining.maxemc", maxValues[0]));
		}

		if (this == ObjHandler.dRod3)
		{
			player.sendMessage(new TranslationTextComponent("pe.divining.secondmax", maxValues[1]));
			player.sendMessage(new TranslationTextComponent("pe.divining.thirdmax", maxValues[2]));
		}

		return ActionResultType.SUCCESS;
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
        return stack.getOrCreateTag().getByte(TAG_MODE);
	}

	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand)
	{
		if (modes.length == 1)
		{
			return false;
		}
		if (getMode(stack) == modes.length - 1)
		{
            stack.getOrCreateTag().putByte(TAG_MODE, ((byte) 0));
		}
		else
		{
            stack.getOrCreateTag().putByte(TAG_MODE, ((byte) (getMode(stack) + 1)));
		}

		player.sendMessage(new TranslationTextComponent("pe.item.mode_switch", modes[getMode(stack)]));

		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags)
	{
		list.add(new TranslationTextComponent("pe.item.mode")
				.appendText(": ")
				.appendSibling(new StringTextComponent(modes[getMode(stack)])
						.setStyle(new Style().setColor(TextFormatting.AQUA))));
	}
}
