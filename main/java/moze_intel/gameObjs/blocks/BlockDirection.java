package moze_intel.gameObjs.blocks;

import java.util.Random;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.tiles.TileEmcDirection;
import moze_intel.gameObjs.tiles.TileEntityDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockDirection extends BlockContainer
{
	private Random rand = new Random();
	
	public BlockDirection(Material material) 
	{
		super(material);
		this.setCreativeTab(ObjHandler.cTab);
	}
	
	@Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
		TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityDirection || tile instanceof TileEmcDirection)
        {
            int direction = 0;
            int facing = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

            if (facing == 0)
                direction = ForgeDirection.NORTH.ordinal();
            else if (facing == 1)
                direction = ForgeDirection.EAST.ordinal();
            else if (facing == 2)
                direction = ForgeDirection.SOUTH.ordinal();
            else if (facing == 3)
                direction = ForgeDirection.WEST.ordinal();
            
            if (tile instanceof TileEntityDirection)
            	((TileEntityDirection) tile).setOrientation(direction);
            else ((TileEmcDirection) tile).setOrientation(direction);
        }
    }
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int noclue)
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
            
            for (float f2 = rand.nextFloat() * 0.8F + 0.1F; stack.stackSize > 0; world.spawnEntityInWorld(entityitem))
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
		super.breakBlock(world, x, y, z, block, noclue);
	}
}
