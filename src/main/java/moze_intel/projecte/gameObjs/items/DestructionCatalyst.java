package moze_intel.projecte.gameObjs.items;

import com.google.common.collect.Lists;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

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

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) return stack;

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);

		if (mop != null && mop.typeOfHit.equals(MovingObjectType.BLOCK))
		{
			int numRows = calculateDepthFromCharge(stack);
			boolean hasAction = false;
			
			BlockPos coords = mop.getBlockPos();
			AxisAlignedBB box = WorldHelper.getDeepBox(coords, mop.sideHit, --numRows);
			
			List<ItemStack> drops = Lists.newArrayList();
			
			for (int x = (int) box.minX; x <= box.maxX; x++)
				for (int y = (int) box.minY; y <= box.maxY; y++)
					for (int z = (int) box.minZ; z <= box.maxZ; z++)
					{
						BlockPos pos = new BlockPos(x, y, z);
						Block block = world.getBlockState(pos).getBlock();
						float hardness = block.getBlockHardness(world, pos);
						
						if (block == Blocks.air || hardness >= 50.0F || hardness == -1.0F)
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
						
						List<ItemStack> list = WorldHelper.getBlockDrops(world, player, world.getBlockState(pos), stack, pos);
						
						if (list != null && list.size() > 0)
						{
							drops.addAll(list);
						}
						
						world.setBlockToAir(pos);
						
						if (world.rand.nextInt(8) == 0)
						{
							PacketHandler.sendToAllAround(new ParticlePKT(EnumParticleTypes.SMOKE_LARGE, x, y, z), new TargetPoint(world.provider.getDimensionId(), x, y + 1, z, 32));
						}
					}

			PlayerHelper.swingItem(((EntityPlayerMP) player));
			if (hasAction)
			{
				WorldHelper.createLootDrop(drops, world, mop.getBlockPos());
				world.playSoundAtEntity(player, "projecte:item.pedestruct", 1.0F, 1.0F);
			}
		}
			
		return stack;
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
