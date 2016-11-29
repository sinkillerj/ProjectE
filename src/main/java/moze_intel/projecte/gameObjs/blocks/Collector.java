package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK3Tile;
import moze_intel.projecte.utils.ComparatorHelper;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class Collector extends BlockDirection
{
	private final int tier;
	
	public Collector(int tier) 
	{
		super(Material.GLASS);
		this.setUnlocalizedName("pe_collector_MK" + tier);
		this.setLightLevel(Constants.COLLECTOR_LIGHT_VALS[tier - 1]);
		this.setHardness(0.3f);
		this.tier = tier;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if (!world.isRemote)
			switch (tier)
			{
				case 1:
					player.openGui(PECore.instance, Constants.COLLECTOR1_GUI, world, x, y, z);
					break;
				case 2:
					player.openGui(PECore.instance, Constants.COLLECTOR2_GUI, world, x, y, z);
					break;
				case 3:
					player.openGui(PECore.instance, Constants.COLLECTOR3_GUI, world, x, y, z);
					break;
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
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		switch (tier) {
			case 3:
				return new CollectorMK3Tile();
			case 2:
				return new CollectorMK2Tile();
			case 1:
				return new CollectorMK1Tile();
			default:
				return null;
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
		return ComparatorHelper.getForCollector(world, pos);
	}

	@Override
	public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state)
	{
		TileEntity ent = world.getTileEntity(pos);
		IItemHandler handler = ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		for (int i = 0; i < handler.getSlots(); i++)
		{
			if (i != CollectorMK1Tile.LOCK_SLOT && handler.getStackInSlot(i) != null)
			{
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
			}
		}
		super.breakBlock(world, pos, state);
	}
}
