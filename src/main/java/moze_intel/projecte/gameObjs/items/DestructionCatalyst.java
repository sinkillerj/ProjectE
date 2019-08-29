package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DestructionCatalyst extends ItemPE implements IItemCharge
{
	public DestructionCatalyst(Properties props)
	{
		super(props);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		World world = ctx.getWorld();
		PlayerEntity player = ctx.getPlayer();

		if (world.isRemote) return ActionResultType.SUCCESS;

		ItemStack stack = ctx.getItem();
		int numRows = calculateDepthFromCharge(stack);
		boolean hasAction = false;

		AxisAlignedBB box = WorldHelper.getDeepBox(ctx.getPos(), ctx.getFace(), --numRows);

		List<ItemStack> drops = new ArrayList<>();

		for (BlockPos pos : WorldHelper.getPositionsFromBox(box))
		{
			BlockState state = world.getBlockState(pos);
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

			if (PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), pos))
			{
				List<ItemStack> list = Block.getDrops(state, (ServerWorld) world, pos, world.getTileEntity(pos), player, stack);
				if (list != null && list.size() > 0
					// shulker boxes are implemented stupidly and drop whenever we set it to air, so don't dupe
					&& !(state.getBlock() instanceof ShulkerBoxBlock))
				{
					drops.addAll(list);
				}

				world.removeBlock(pos, false);

				if (world.rand.nextInt(8) == 0)
				{
					((ServerWorld) world).spawnParticle(world.rand.nextBoolean() ? ParticleTypes.POOF : ParticleTypes.LARGE_SMOKE, pos.getX(), pos.getY(), pos.getZ(), 2, 0, 0, 0, 0.05);
				}
			}
		}

		if (hasAction)
		{
			WorldHelper.createLootDrop(drops, world, ctx.getPos());
			world.playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
			
		return ActionResultType.SUCCESS;
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
