package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.IExtraFunction;
import moze_intel.projecte.api.IProjectileShooter;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.BlockDirection;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PhilosophersStone extends ItemMode implements IProjectileShooter, IExtraFunction
{
	public PhilosophersStone()
	{
		super("philosophers_stone", (byte)4, new String[] {
				StatCollector.translateToLocal("pe.philstone.mode1"),
				StatCollector.translateToLocal("pe.philstone.mode2"),
				StatCollector.translateToLocal("pe.philstone.mode3")});
		this.setContainerItem(this);
		this.setNoRepair();
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing sideHit, float px, float py, float pz)
	{
		if (world.isRemote)
		{
			return false;
		}

		IBlockState state = world.getBlockState(pos);

		if (!world.isAirBlock(pos))
		{
			TileEntity tile = world.getTileEntity(pos);
			
			if (player.isSneaking())
			{
				if (tile instanceof TileEmc)
				{
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setBoolean("ProjectEBlock", true);
					tile.writeToNBT(nbt);
					
					ItemStack s;
					if (state.getBlock() == ObjHandler.dmFurnaceOn)
					{
						s = new ItemStack(ObjHandler.dmFurnaceOff);
					}
					else if (state.getBlock() == ObjHandler.rmFurnaceOn)
					{
						s = new ItemStack(ObjHandler.rmFurnaceOff);
					}
					else if (state.getBlock() instanceof BlockDirection)
					{
						s = ItemHelper.stateToStack(state.withProperty(BlockDirection.FACING, EnumFacing.SOUTH), 1);
					}
					else
					{
						s = ItemHelper.stateToStack(state, 1);
					}
					
					s.setTagCompound(nbt);
					
					world.removeTileEntity(pos);
					world.setBlockToAir(pos);
					WorldHelper.spawnEntityItem(world, s, pos.getX(), pos.getY(), pos.getZ());
				}
			}
		}

		IBlockState result = WorldTransmutations.getWorldTransmutation(world, pos, player.isSneaking());

		if (result != null)
		{
			int mode = this.getMode(stack);
			int charge = this.getCharge(stack);
			
			if (mode == 0)
			{
				doWorldTransmutation(world, state, result, pos, 0, 0, charge);
			}
			else if (mode == 1)
			{
				transmutePanel(sideHit, charge, state, result, pos, world);
			}
			else 
			{
				transmuteLine(sideHit, charge, state, result, pos, world, player);
			}

			world.playSoundAtEntity(player, "projecte:item.petransmute", 1.0F, 1.0F);

			PlayerHelper.swingItem(player);
		}
		
		return true;
	}
	
	private void transmutePanel(EnumFacing direction, int charge, IBlockState pointed, IBlockState result, BlockPos coords, World world)
	{
		int side;
		
		if (direction.getAxis() == EnumFacing.Axis.Y)
		{
			side = 0;
		}
		else if (direction.getAxis() == EnumFacing.Axis.X)
		{
			side = 1;
		}
		else
		{
			side = 2;
		}
		
		doWorldTransmutation(world, pointed, result, coords, 1, side, charge);
	}
	
	private void transmuteLine(EnumFacing direction, int charge, IBlockState pointed, IBlockState result, BlockPos coords, World world, EntityPlayer player)
	{
		int side;
		
		if (direction.getAxis() == EnumFacing.Axis.X)
		{
			side = 0;
		}
		else if (direction.getAxis() == EnumFacing.Axis.Z)
		{
			side = 1;
		}
		else
		{
			EnumFacing dir = player.getHorizontalFacing();
			
			if (dir == EnumFacing.NORTH || dir == EnumFacing.SOUTH)
			{
				side = 0;
			}
			else
			{
				side = 1;
			}
		}
		
		doWorldTransmutation(world, pointed, result, coords, 2, side, charge);
	}
	
	/**
	 * type 0 = cube, type 1 = panel, type 2 = line
	 */
	private void doWorldTransmutation(World world, IBlockState pointed, IBlockState result, BlockPos pos, int type, int side, int charge)
	{
		if (type == 0)
		{
			for (int i = pos.getX() - charge; i <= pos.getX() + charge; i++)
				for (int j = pos.getY() - charge; j <= pos.getY() + charge; j++)
					for (int k = pos.getZ() - charge; k <= pos.getZ() + charge; k++)
					{
						changeBlock(world, pointed, result, new BlockPos(i, j, k));
					}
		}
		else if (type == 1)
		{
			if (side == 0)
			{
				for (int i = pos.getX() - charge; i <= pos.getX() + charge; i++)
					for (int j = pos.getZ() - charge; j <= pos.getZ() + charge; j++)
					{
						changeBlock(world, pointed, result, new BlockPos(i, pos.getY(), j));
					}
			}
			else if (side == 1)
			{
				for (int i = pos.getY() - charge; i <= pos.getY() + charge; i++)
					for (int j = pos.getZ() - charge; j <= pos.getZ() + charge; j++)
					{
						changeBlock(world, pointed, result, new BlockPos(pos.getX(), i, j));
					}
			}
			else
			{
				for (int i = pos.getX() - charge; i <= pos.getX() + charge; i++)
					for (int j = pos.getY() - charge; j <= pos.getY() + charge; j++)
					{
						changeBlock(world, pointed, result, new BlockPos(i, j, pos.getZ()));
					}
			}
		}
		else
		{
			if (side == 0)
			{
				for (int i = pos.getZ() - charge; i <= pos.getZ() + charge; i++)
				{
					changeBlock(world, pointed, result, new BlockPos(pos.getX(), pos.getY(), i));
				}
			}
			else 
			{
				for (int i = pos.getX() - charge; i <= pos.getX() + charge; i++)
				{
					changeBlock(world, pointed, result, new BlockPos(i, pos.getY(), pos.getZ()));
				}
			}
		}
	}
	
	private void changeBlock(World world, IBlockState pointed, IBlockState result, BlockPos pos)
	{
		if (world.getBlockState(pos) == pointed)
		{
			world.setBlockState(pos, result, 3);

			if (world.rand.nextInt(8) == 0)
			{
				PacketHandler.sendToAllAround(new ParticlePKT(EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY() + 1, pos.getZ()), new TargetPoint(world.provider.getDimensionId(), pos.getX(), pos.getY() + 1, pos.getZ(), 32));
			}
		}
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
	public boolean shootProjectile(EntityPlayer player, ItemStack stack) 
	{
		World world = player.worldObj;
		world.playSoundAtEntity(player, "projecte:item.petransmute", 1.0F, 1.0F);
		world.spawnEntityInWorld(new EntityMobRandomizer(world, player));
		return true;
	}
	
	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player) 
	{
		if (!player.worldObj.isRemote)
		{
			player.openGui(PECore.instance, Constants.PHILOS_STONE_GUI, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		list.add(String.format(StatCollector.translateToLocal("pe.philstone.tooltip1"), ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION)));
		list.add(StatCollector.translateToLocal("pe.philstone.tooltip2"));
		list.add(StatCollector.translateToLocal("pe.philstone.tooltip3"));
		list.add(StatCollector.translateToLocal("pe.philstone.tooltip4"));
	}
}
