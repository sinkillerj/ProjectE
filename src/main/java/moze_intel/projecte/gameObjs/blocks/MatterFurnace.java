package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class MatterFurnace extends BlockDirection implements ITileEntityProvider
{
	private boolean isActive;
	private boolean isHighTier;
	private static boolean isUpdating;

	public MatterFurnace(boolean active, boolean isRM) 
	{
		super(Material.rock);
		this.setCreativeTab(ObjHandler.cTab);
		isActive = active;
		isHighTier = isRM;
		this.setUnlocalizedName("pe_" + (isHighTier ? "rm" : "dm") + "_furnace");
		
		if (isActive) 
		{
			this.setCreativeTab(null);
			this.setLightLevel(0.875F);
		}
	}
	
	@Override
	public float getBlockHardness(World world, BlockPos pos)
	{
		return isHighTier ? 2000000F : 1000000F;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return isHighTier ? Item.getItemFromBlock(ObjHandler.rmFurnaceOff) : Item.getItemFromBlock(ObjHandler.dmFurnaceOff);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			if (isHighTier)
			{
				player.openGui(PECore.instance, Constants.RM_FURNACE_GUI, world, pos.getX(), pos.getY(), pos.getZ());
			}
			else
			{
				player.openGui(PECore.instance, Constants.DM_FURNACE_GUI, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		
		return true;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if (!isUpdating)
		{
			super.breakBlock(world, pos, state);
		}
	}
	
	public void updateFurnaceBlockState(boolean isActive, World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		TileEntity tile = world.getTileEntity(pos);
		isUpdating = true;

		if (isActive)
		{
			if (isHighTier)
			{
				world.setBlockState(pos, ObjHandler.rmFurnaceOn.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
			}
			else
			{
				world.setBlockState(pos, ObjHandler.dmFurnaceOn.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
			}
		}
		else
		{
			if (isHighTier)
			{
				world.setBlockState(pos, ObjHandler.rmFurnaceOff.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
			}
			else
			{
				world.setBlockState(pos, ObjHandler.dmFurnaceOff.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
			}
		}

		isUpdating = false;

		if (tile != null)
		{
			tile.validate();
			world.setTileEntity(pos, tile);
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entLiving, ItemStack stack)
	{
		setFacingMeta(world, pos, ((EntityPlayer) entLiving));
		
		TileEntity tile = world.getTileEntity(pos);
		
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("ProjectEBlock") && tile instanceof TileEmc)
		{
			stack.getTagCompound().setInteger("x", pos.getX());
			stack.getTagCompound().setInteger("y", pos.getY());
			stack.getTagCompound().setInteger("z", pos.getZ());
			stack.getTagCompound().setInteger("EMC", 0);
			stack.getTagCompound().setShort("BurnTime", (short) 0);
			stack.getTagCompound().setShort("CookTime", (short) 0);
			
			tile.readFromNBT(stack.getTagCompound());
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if (isActive)
		{
			EnumFacing facing = ((EnumFacing) state.getValue(FACING));
			float f = (float) pos.getX() + 0.5F;
			float f1 = (float) pos.getY() + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
			float f2 = (float) pos.getZ() + 0.5F;
			float f3 = 0.52F;
			float f4 = rand.nextFloat() * 0.6F - 0.3F;

			switch (facing)
			{
				case WEST:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
					world.spawnParticle(EnumParticleTypes.FLAME, (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
					break;
				case EAST:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
					world.spawnParticle(EnumParticleTypes.FLAME, (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
				case NORTH:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
					world.spawnParticle(EnumParticleTypes.FLAME, (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
				case SOUTH:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
					world.spawnParticle(EnumParticleTypes.FLAME, (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, BlockPos pos)
	{
		return isHighTier ? Item.getItemFromBlock(ObjHandler.rmFurnaceOff) : Item.getItemFromBlock(ObjHandler.dmFurnaceOff);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) 
	{
		return isHighTier ? new RMFurnaceTile() : new DMFurnaceTile();
	}
}
