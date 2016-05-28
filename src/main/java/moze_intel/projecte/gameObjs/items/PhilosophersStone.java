package moze_intel.projecte.gameObjs.items;

import com.google.common.collect.Sets;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class PhilosophersStone extends ItemMode implements IProjectileShooter, IExtraFunction
{
	public PhilosophersStone()
	{
		super("philosophers_stone", (byte)4, new String[] {
				"pe.philstone.mode1",
				"pe.philstone.mode2",
				"pe.philstone.mode3"});
		this.setContainerItem(this);
		this.setNoRepair();
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing sideHit, float px, float py, float pz)
	{
		if (world.isRemote)
		{
			return EnumActionResult.SUCCESS;
		}

		IBlockState result = WorldTransmutations.getWorldTransmutation(world, pos, player.isSneaking());

		if (result != null)
		{
			int mode = this.getMode(stack);
			int charge = this.getCharge(stack);

			for (BlockPos currentPos : getAffectedPositions(world, pos, player, sideHit, mode, charge))
			{
				PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), currentPos, result);
				if (world.rand.nextInt(8) == 0)
				{
					PacketHandler.sendToAllAround(new ParticlePKT(EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY() + 1, pos.getZ()), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY() + 1, pos.getZ(), 32));
				}
			}

			world.playSound(null, player.posX, player.posY, player.posZ, PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1, 1);

			PlayerHelper.swingItem(player);
		}
		
		return EnumActionResult.SUCCESS;
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		super.onCreated(stack, world, player);
		
		if (!world.isRemote)
		{
			player.addStat(AchievementHandler.PHIL_STONE, 1);
		}
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack, EnumHand hand)
	{
		World world = player.worldObj;
		world.playSound(null, player.posX, player.posY, player.posZ, PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1, 1);
		EntityMobRandomizer ent = new EntityMobRandomizer(world, player);
		ent.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
		world.spawnEntityInWorld(ent);
		return true;
	}
	
	@Override
	public boolean doExtraFunction(ItemStack stack, EntityPlayer player, EnumHand hand)
	{
		if (!player.worldObj.isRemote)
		{
			player.openGui(PECore.instance, Constants.PHILOS_STONE_GUI, player.worldObj, hand == EnumHand.MAIN_HAND ? 0 : 1, -1, -1);
		}

		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
	{
		list.add(I18n.format("pe.philstone.tooltip1", ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION)));
	}

	public static Set<BlockPos> getAffectedPositions(World world, BlockPos pos, EntityPlayer player, EnumFacing sideHit, int mode, int charge)
	{
		Set<BlockPos> ret = Sets.newHashSet();
		IBlockState targeted = world.getBlockState(pos);
		Iterable<BlockPos> iterable = null;

		switch (mode)
		{
			case 0: // Cube
				iterable = WorldHelper.getPositionsFromCorners(pos.add(-charge, -charge, -charge), pos.add(charge, charge, charge));
				break;
			case 1: // Panel
				if (sideHit == EnumFacing.UP || sideHit == EnumFacing.DOWN)
				{
					iterable = WorldHelper.getPositionsFromCorners(pos.add(-charge, 0, -charge), pos.add(charge, 0, charge));
				}
				else if (sideHit == EnumFacing.EAST || sideHit == EnumFacing.WEST)
				{
					iterable = WorldHelper.getPositionsFromCorners(pos.add(0, -charge, -charge), pos.add(0, charge, charge));
				}
				else if (sideHit == EnumFacing.SOUTH || sideHit == EnumFacing.NORTH)
				{
					iterable = WorldHelper.getPositionsFromCorners(pos.add(-charge, -charge, 0), pos.add(charge, charge, 0));
				}
				break;
			case 2: // Line
				EnumFacing playerFacing = player.getHorizontalFacing();

				if (playerFacing.getAxis() == EnumFacing.Axis.Z)
				{
					iterable = WorldHelper.getPositionsFromCorners(pos.add(0, 0, -charge), pos.add(0, 0, charge));
				}
				else if (playerFacing.getAxis() == EnumFacing.Axis.X)
				{
					iterable = WorldHelper.getPositionsFromCorners(pos.add(-charge, 0, 0), pos.add(charge, 0, 0));
				}
				break;
		}

		if (iterable != null) {
			for (BlockPos currentPos : iterable)
            {
                if (world.getBlockState(currentPos) == targeted)
                {
                    ret.add(currentPos);
                }
            }
		}

		return ret;
	}
}
