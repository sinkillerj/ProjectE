package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public class MatterFurnace extends BlockDirection
{
	private final boolean isActive;
	private final boolean isHighTier;
	private static boolean isUpdating;

	public MatterFurnace(boolean active, boolean isRM) 
	{
		super(Material.ROCK);
		this.setCreativeTab(ObjHandler.cTab);
		isActive = active;
		isHighTier = isRM;
		this.setTranslationKey("pe_" + (isHighTier ? "rm" : "dm") + "_furnace");
		this.setHardness(1000000F);
		
		if (isActive) 
		{
			this.setCreativeTab(null);
			this.setLightLevel(0.875F);
		}
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos)
	{
		return isHighTier ? 2000000F : 1000000F;
	}
	
	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return isHighTier ? Item.getItemFromBlock(ObjHandler.rmFurnaceOff) : Item.getItemFromBlock(ObjHandler.dmFurnaceOff);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
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
	public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state)
	{
		// isUpdating is true if this breakBlock is being called as a result of updateFurnaceBlockState
		// It prevents items from dropping out of the furnace when switching on/off state
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
				world.setBlockState(pos, ObjHandler.rmFurnaceOn.getDefaultState().withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3);
			}
			else
			{
				world.setBlockState(pos, ObjHandler.dmFurnaceOn.getDefaultState().withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3);
			}
		}
		else
		{
			if (isHighTier)
			{
				world.setBlockState(pos, ObjHandler.rmFurnaceOff.getDefaultState().withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3);
			}
			else
			{
				world.setBlockState(pos, ObjHandler.dmFurnaceOff.getDefaultState().withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3);
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
		world.setBlockState(pos, state.withProperty(PEStateProps.FACING, entLiving.getHorizontalFacing().getOpposite()));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		if (isActive)
		{
			EnumFacing facing = state.getValue(PEStateProps.FACING);
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
					break;
				case NORTH:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
					world.spawnParticle(EnumParticleTypes.FLAME, (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
					break;
				case SOUTH:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
					world.spawnParticle(EnumParticleTypes.FLAME, (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
					break;
			}
		}
	}
	
	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player)
	{
		return isHighTier ? new ItemStack(Item.getItemFromBlock(ObjHandler.rmFurnaceOff)) : new ItemStack(Item.getItemFromBlock(ObjHandler.dmFurnaceOff));
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state)
	{
		return isHighTier ? new RMFurnaceTile() : new DMFurnaceTile();
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
		{
			IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			return ItemHandlerHelper.calcRedstoneFromInventory(inv);
		}
		return 0;
	}
}
