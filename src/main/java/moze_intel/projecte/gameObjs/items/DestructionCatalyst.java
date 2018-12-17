package moze_intel.projecte.gameObjs.items;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DestructionCatalyst extends ItemPE implements IItemCharge
{
	public DestructionCatalyst() 
	{
		this.setTranslationKey("destruction_catalyst");
		this.setMaxStackSize(1);
		this.setNoRepair();
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos coords, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote) return EnumActionResult.SUCCESS;

		ItemStack stack = player.getHeldItem(hand);
		int numRows = calculateDepthFromCharge(stack);
		boolean hasAction = false;

		AxisAlignedBB box = WorldHelper.getDeepBox(coords, facing, --numRows);

		List<ItemStack> drops = new ArrayList<>();

		for (BlockPos pos : WorldHelper.getPositionsFromBox(box))
		{
			IBlockState state = world.getBlockState(pos);
			float hardness = state.getBlockHardness(world, pos);

			if (world.isAirBlock(pos) || hardness >= 50.0F || hardness == -1.0F)
			{
				continue;
			}

			if (!consumeFuel(player, stack, 8, true))
			{
				break;
			}

			hasAction = true;

			if (PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), pos))
			{
				List<ItemStack> list = WorldHelper.getBlockDrops(world, player, state, stack, pos);
				if (list != null && list.size() > 0
					// shulker boxes are implemented stupidly and drop whenever we set it to air, so don't dupe
					&& !(state.getBlock() instanceof BlockShulkerBox))
				{
					drops.addAll(list);
				}

				world.setBlockToAir(pos);

				if (world.rand.nextInt(8) == 0)
				{
					((WorldServer) world).spawnParticle(world.rand.nextBoolean() ? EnumParticleTypes.EXPLOSION_NORMAL : EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY(), pos.getZ(), 2, 0, 0, 0, 0.05);
				}
			}
		}

		PlayerHelper.swingItem(player, hand);
		if (hasAction)
		{
			WorldHelper.createLootDrop(drops, world, coords);
			world.playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
			
		return EnumActionResult.SUCCESS;
	}

	private int calculateDepthFromCharge(ItemStack stack)
	{
		int charge = getCharge(stack);
		if (charge <= 0)
		{
			return 1;
		}
		if (this instanceof CataliticLens)
		{
			return 8 + (charge * 8);

		}
		return (int) Math.pow(2, 1 + charge);
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack)
	{
		return 3;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1.0D - (double) getCharge(stack) / getNumCharges(stack);
	}
}
