package moze_intel.projecte.gameObjs.items;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import java.util.List;

public class DestructionCatalyst extends ItemCharge
{
	public DestructionCatalyst() 
	{
		super("destruction_catalyst", (byte)3);
		this.setNoRepair();
	}

	// Only for Catalitic Lens
	protected DestructionCatalyst(String name, byte numCharges)
	{
		super(name, numCharges);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (world.isRemote) return ActionResult.newResult(EnumActionResult.SUCCESS, stack);

		RayTraceResult mop = this.rayTrace(world, player, false);

		if (mop != null && mop.typeOfHit.equals(Type.BLOCK))
		{
			int numRows = calculateDepthFromCharge(stack);
			boolean hasAction = false;
			
			BlockPos coords = mop.getBlockPos();
			AxisAlignedBB box = WorldHelper.getDeepBox(coords, mop.sideHit, --numRows);
			
			List<ItemStack> drops = Lists.newArrayList();

			for (BlockPos pos : WorldHelper.getPositionsFromBox(box))
			{
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				float hardness = state.getBlockHardness(world, pos);

				if (world.isAirBlock(pos) || hardness >= 50.0F || hardness == -1.0F)
				{
					continue;
				}

				if (!consumeFuel(player, stack, 8, true))
				{
					break;
				}

				if (!hasAction)
				{
					hasAction = true;
				}

				if (PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), pos))
				{
					List<ItemStack> list = WorldHelper.getBlockDrops(world, player, world.getBlockState(pos), stack, pos);
					if (list != null && list.size() > 0)
					{
						drops.addAll(list);
					}

					world.setBlockToAir(pos);

					if (world.rand.nextInt(8) == 0)
					{
						PacketHandler.sendToAllAround(new ParticlePKT(world.rand.nextBoolean() ? EnumParticleTypes.EXPLOSION_NORMAL : EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY(), pos.getZ()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY() + 1, pos.getZ(), 32));
					}
				}

				List<ItemStack> list = WorldHelper.getBlockDrops(world, player, world.getBlockState(pos), stack, pos);

				if (list != null && list.size() > 0)
				{
					drops.addAll(list);
				}

				world.setBlockToAir(pos);

				if (world.rand.nextInt(8) == 0)
				{
					PacketHandler.sendToAllAround(new ParticlePKT(world.rand.nextBoolean() ? EnumParticleTypes.EXPLOSION_NORMAL : EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY(), pos.getZ()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY() + 1, pos.getZ(), 32));
				}
			}

			PlayerHelper.swingItem(player);
			if (hasAction)
			{
				WorldHelper.createLootDrop(drops, world, mop.getBlockPos());
				world.playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}
			
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	protected int calculateDepthFromCharge(ItemStack stack)
	{
		byte charge = getCharge(stack);
		if (charge <= 0)
		{
			return 1;
		}
		if (this instanceof CataliticLens)
		{
			return 8 + (charge * 8); // Increases linearly by 8, starting at 16 for charge 1

		}
		return (int) Math.pow(2, 1 + charge); // Default DesCatalyst formula, doubles for every level, starting at 4 for charge 1
	}
}
