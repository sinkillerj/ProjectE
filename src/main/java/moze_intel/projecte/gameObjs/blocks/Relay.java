package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK2Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;
import moze_intel.projecte.utils.ComparatorHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public class Relay extends BlockDirection
{
	private final int tier;
	
	public Relay(int tier) 
	{
		super(Material.ROCK);
		this.setUnlocalizedName("pe_relay_MK" + Integer.toString(tier));
		this.setLightLevel(Constants.COLLECTOR_LIGHT_VALS[tier - 1]);
		this.setHardness(10.0f);
		this.tier = tier;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			switch (tier)
			{
				case 1:
					player.openGui(PECore.instance, Constants.RELAY1_GUI, world, x, y, z);
					break;
				case 2:
					player.openGui(PECore.instance, Constants.RELAY2_GUI, world, x, y, z);
					break;
				case 3:
					player.openGui(PECore.instance, Constants.RELAY3_GUI, world, x, y, z);
					break;
			}
		}
		return true;
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
		switch (tier)
		{
			case 1: return new RelayMK1Tile();
			case 2: return new RelayMK2Tile();
			case 3: return new RelayMK3Tile();
			default: return null;
		}
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
	{
		return ComparatorHelper.getForRelay(world, pos);
	}

	@Override
	public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state)
	{
		TileEntity te = world.getTileEntity(pos);
		WorldHelper.dropInventory(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), world, pos);
		super.breakBlock(world, pos, state);
	}

}
