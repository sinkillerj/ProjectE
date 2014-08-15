package moze_intel.gameObjs.blocks;

import java.util.Random;

import moze_intel.MozeCore;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.tiles.DMFurnaceTile;
import moze_intel.gameObjs.tiles.RMFurnaceTile;
import moze_intel.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MatterFurnace extends BlockContainer implements ITileEntityProvider
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
		this.setBlockName(textureName+"_furnace");
		if (isActive) this.setLightLevel(0.875F);
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0) 
		{
			return 1000000.0F;
		}
		else
		{
			return 2000000.0F;
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			if (isHighTier)
				player.openGui(MozeCore.instance, Constants.RM_FURNACE_GUI, world, x, y, z);
			else player.openGui(MozeCore.instance, Constants.DM_FURNACE_GUI, world, x, y, z);
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
				if (stack == null) continue;
				
				float f = rand.nextFloat() * 0.8F + 0.1F;
	            float f1 = rand.nextFloat() * 0.8F + 0.1F;
	            EntityItem entityitem;
	            
	            for (float f2 = rand .nextFloat() * 0.8F + 0.1F; stack.stackSize > 0; world.spawnEntityInWorld(entityitem))
	            {
	                int j1 = rand.nextInt(21) + 10;

	                if (j1 > stack.stackSize)
	                    j1 = stack.stackSize;

	                stack.stackSize -= j1;
	                entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(stack.getItem(), j1, stack.getItemDamage()));
	                float f3 = 0.05F;
	                entityitem.motionX = (double)((float)rand.nextGaussian() * f3);
	                entityitem.motionY = (double)((float)rand.nextGaussian() * f3 + 0.2F);
	                entityitem.motionZ = (double)((float)rand.nextGaussian() * f3);

	                if (stack.hasTagCompound())
	                    entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
	            }			
			}
			world.func_147453_f(x, y, z, block);
		}
		super.breakBlock(world, x, y, z, block, noclue);
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
        	else world.setBlock(x, y, z, ObjHandler.dmFurnaceOn);
        }
        else
        {
        	if (isHighTier)
        		world.setBlock(x, y, z, ObjHandler.rmFurnaceOff);
        	else world.setBlock(x, y, z, ObjHandler.dmFurnaceOff);
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
        int l = MathHelper.floor_double((double)(entLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);

        if (l == 1)
            world.setBlockMetadataWithNotify(x, y, z, 5, 2);

        if (l == 2)
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);

        if (l == 3)
            world.setBlockMetadataWithNotify(x, y, z, 4, 2);
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
		this.blockIcon = register.registerIcon("projecte:"+textureName);
		front = register.registerIcon("projecte:matter_furnace/"+(isActive ? (textureName+"_on") : (textureName + "_off")));
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
		if (meta == 0 && side == 3) return front;
		return side != meta ? this.blockIcon : front;
    }
	
	@SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z)
	{
		return isHighTier ? isActive ? Item.getItemFromBlock(ObjHandler.rmFurnaceOn) : Item.getItemFromBlock(ObjHandler.rmFurnaceOff) : Item.getItemFromBlock(ObjHandler.dmFurnaceOff);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) 
	{
		if (isHighTier) return new RMFurnaceTile();
		return new DMFurnaceTile();
	}
}
