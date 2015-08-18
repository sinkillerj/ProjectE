package moze_intel.projecte.gameObjs.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.utils.ComparatorHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class MatterFurnace extends BlockDirection
{
	private String textureName;
	private boolean isActive;
	private boolean isHighTier;
	private static boolean isUpdating;
	@SideOnly(Side.CLIENT) 
	private IIcon front;
	private Random rand = new Random();

	public MatterFurnace(boolean active, boolean isRM) 
	{
		super(Material.rock);
		this.setCreativeTab(ObjHandler.cTab);
		isActive = active;
		isHighTier = isRM;
		textureName = isHighTier ? "rm" : "dm";
		this.setBlockName("pe_" + textureName + "_furnace");
		
		if (isActive) 
		{
			this.setCreativeTab(null);
			this.setLightLevel(0.875F);
		}
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) == 0 ? 1000000F : 2000000F;
	}
	
	@Override
	public Item getItemDropped(int no, Random rand, int clue)
	{
		return isHighTier ? Item.getItemFromBlock(ObjHandler.rmFurnaceOff) : Item.getItemFromBlock(ObjHandler.dmFurnaceOff);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			if (isHighTier)
			{
				player.openGui(PECore.instance, Constants.RM_FURNACE_GUI, world, x, y, z);
			}
			else
			{
				player.openGui(PECore.instance, Constants.DM_FURNACE_GUI, world, x, y, z);
			}
		}
		
		return true;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int noclue)
	{
		if (!isUpdating)
		{
			IInventory tile = (IInventory) world.getTileEntity(x, y, z);
			if (tile == null) return;
			for (int i = 0; i < tile.getSizeInventory(); i++)
			{
				ItemStack stack = tile.getStackInSlot(i);
				
				if (stack == null) 
				{
					continue;
				}
				
				WorldHelper.spawnEntityItem(world, stack, x, y, z);
			}
			
			world.func_147453_f(x, y, z, block);
		}
		
		world.removeTileEntity(x, y, z);
	}
	
	public void updateFurnaceBlockState(boolean isActive, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity tile = world.getTileEntity(x, y, z);
		isUpdating = true;

		if (isActive)
		{
			if (isHighTier)
				world.setBlock(x, y, z, ObjHandler.rmFurnaceOn);
			else
				world.setBlock(x, y, z, ObjHandler.dmFurnaceOn);
		}
		else
		{
			if (isHighTier)
				world.setBlock(x, y, z, ObjHandler.rmFurnaceOff);
			else
				world.setBlock(x, y, z, ObjHandler.dmFurnaceOff);
		}

		isUpdating = false;
		world.setBlockMetadataWithNotify(x, y, z, meta, 2);

		if (tile != null)
		{
			tile.validate();
			world.setTileEntity(x, y, z, tile);
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entLiving, ItemStack stack)
	{
		setFacingMeta(world, x, y, z, ((EntityPlayer) entLiving));
		
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if (stack.hasTagCompound() && stack.stackTagCompound.getBoolean("ProjectEBlock") && tile instanceof TileEmc)
		{
			stack.stackTagCompound.setInteger("x", x);
			stack.stackTagCompound.setInteger("y", y);
			stack.stackTagCompound.setInteger("z", z);
			stack.stackTagCompound.setInteger("EMC", 0);
			stack.stackTagCompound.setShort("BurnTime", (short) 0);
			stack.stackTagCompound.setShort("CookTime", (short) 0);
			
			tile.readFromNBT(stack.stackTagCompound);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand)
	{
		if (isActive)
		{
			int l = world.getBlockMetadata(x, y, z);
			float f = (float) x + 0.5F;
			float f1 = (float) y + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
			float f2 = (float) z + 0.5F;
			float f3 = 0.52F;
			float f4 = rand.nextFloat() * 0.6F - 0.3F;

			if (l == 4)
			{
				world.spawnParticle("smoke", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
			}
			else if (l == 5)
			{
				world.spawnParticle("smoke", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
			}
			else if (l == 2)
			{
				world.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
			}
			else if (l == 3)
			{
				world.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon("projecte:" + textureName);
		front = register.registerIcon("projecte:matter_furnace/" + (isActive ? (textureName + "_on") : (textureName + "_off")));
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if (meta == 0 && side == 3) 
		{
			return front;
		}
		
		return side != meta ? this.blockIcon : front;
	}
	
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z)
	{
		return isHighTier ? Item.getItemFromBlock(ObjHandler.rmFurnaceOff) : Item.getItemFromBlock(ObjHandler.dmFurnaceOff);
	}

	@Override
	public boolean hasTileEntity(int meta)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta)
	{
		return isHighTier ? new RMFurnaceTile() : new DMFurnaceTile();
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int meta)
	{
		return ComparatorHelper.getForMatterFurnace(world, x, y, z);
	}
}
