package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DestructionCatalyst extends ItemPE implements IItemCharge {

	public DestructionCatalyst(Properties props) {
		super(props);
		addItemCapability(new ChargeItemCapabilityWrapper());
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}
		PlayerEntity player = ctx.getPlayer();
		ItemStack stack = ctx.getItem();
		int numRows = calculateDepthFromCharge(stack);
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos pos : WorldHelper.getPositionsFromBox(WorldHelper.getDeepBox(ctx.getPos(), ctx.getFace(), --numRows))) {
			if (world.isAirBlock(pos)) {
				continue;
			}
			BlockState state = world.getBlockState(pos);
			float hardness = state.getBlockHardness(world, pos);
			if (hardness == -1.0F || hardness >= 50.0F) {
				continue;
			}
			if (!consumeFuel(player, stack, 8, true)) {
				break;
			}
			hasAction = true;
			//Ensure we are immutable so that changing blocks doesn't act weird
			pos = pos.toImmutable();
			if (PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, pos)) {
				List<ItemStack> list = Block.getDrops(state, (ServerWorld) world, pos, world.getTileEntity(pos), player, stack);
				drops.addAll(list);
				world.removeBlock(pos, false);
				if (world.rand.nextInt(8) == 0) {
					((ServerWorld) world).spawnParticle(world.rand.nextBoolean() ? ParticleTypes.POOF : ParticleTypes.LARGE_SMOKE, pos.getX(), pos.getY(), pos.getZ(), 2, 0, 0, 0, 0.05);
				}
			}
		}
		if (hasAction) {
			WorldHelper.createLootDrop(drops, world, ctx.getPos());
			world.playSound(null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
		return ActionResultType.SUCCESS;
	}

	private int calculateDepthFromCharge(ItemStack stack) {
		int charge = getCharge(stack);
		if (charge <= 0) {
			return 1;
		}
		if (this instanceof CataliticLens) {
			return 8 + 8 * charge;
		}
		return (int) Math.pow(2, 1 + charge);
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return 3;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1.0D - getChargePercent(stack);
	}
}