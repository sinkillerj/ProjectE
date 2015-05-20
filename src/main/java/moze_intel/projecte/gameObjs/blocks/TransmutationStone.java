package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class TransmutationStone extends Block implements ITileEntityProvider
{
	public TransmutationStone() 
	{
		super(Material.rock);
		this.setCreativeTab(ObjHandler.cTab);
		this.setUnlocalizedName("pe_transmutation_stone");
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
		this.setHardness(10.0f);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random random, int par2)
	{
		return Item.getItemFromBlock(ObjHandler.transmuteStone);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) 
	{
		return new TransmuteTile();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TransmuteTile tile = (TransmuteTile) world.getTileEntity(x, y, z);
			
		if (!tile.isUsed())
		{
			tile.setPlayer(player);
			
			if (!world.isRemote)
			{
				player.openGui(PECore.MODID, Constants.TRANSMUTE_STONE_GUI, world, x, y, z);
			}
		}
		else
		{
			if (!world.isRemote)
			{
				player.addChatComponentMessage(new ChatComponentTranslation("pe.transmutation.already_using"));
			}
		}
		return true;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean isFullCube()
	{
		return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entLiving, ItemStack stack)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("ProjectEBlock") && tile instanceof TileEmc)
		{
			stack.getTagCompound().setInteger("x", x);
			stack.getTagCompound().setInteger("y", y);
			stack.getTagCompound().setInteger("z", z);
			
			tile.readFromNBT(stack.stackTagCompound);
		}
	}
}
