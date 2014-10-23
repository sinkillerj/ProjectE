package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.MozeCore;
import moze_intel.projecte.gameObjs.container.inventory.MercurialEyeInventory;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.CoordinateBox;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MercurialEye extends ItemMode implements IExtraFunction
{
	public MercurialEye() 
	{
		super("mercurial_eye", (byte) 4, new String[] {"Extension", "Transmutation"});
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			return stack;
		}
		
		MercurialEyeInventory inv = new MercurialEyeInventory(stack);
		ItemStack klein = inv.getKleinStack();
		ItemStack target = inv.getTargetStack();
		
		if (target == null)
		{
			return stack;
		}
		
		Block targetBlock = Block.getBlockFromItem(target.getItem());
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
		
		if (mop == null || !mop.typeOfHit.equals(MovingObjectType.BLOCK))
		{
			return stack;
		}
		
		byte mode = this.getMode(stack);
		int offset = 0;
		
		switch(this.getCharge(stack))
		{
			case 0:
				offset = 0;
				break;
			case 1:
				offset = 1;
				break;
			case 2:
				offset = 4;
				break;
			case 3:
				offset = 8;
				break;
		}
		
		
		CoordinateBox box = getAxisRelativePanel(new Coordinates(mop), offset, getRelativeOrientation(player));
		
		int emcCost = Utils.getEmcValue(target);
		
		for (int x = (int) box.minX; x <= box.maxX; x++)
			for (int y = (int) box.minY; y <= box.maxY; y++)
				for (int z = (int) box.minZ; z <= box.maxZ; z++)
				{
					Block block = world.getBlock(x, y, z);
					
					if (mode == 0)
					{
						if (ItemPE.getEmc(klein) >= emcCost && block == Blocks.air)
						{
							world.setBlock(x, y, z, targetBlock);
							ItemPE.removeEmc(stack, emcCost);
						}
					}
					else
					{
						if (block == Blocks.air)
						{
							continue;
						}
						
						int emcDifference = Utils.getEmcValue(new ItemStack(block, 1, world.getBlockMetadata(x, y, z))) - emcCost;
						
						if (emcDifference <= 0)
						{
							world.setBlock(x, y, z, targetBlock);
						}
						else if (ItemPE.getEmc(klein) >= emcDifference)
						{
							world.setBlock(x, y, z, targetBlock);
							ItemPE.removeEmc(klein, emcDifference);
						}
					}
				}
			
		
		
		return stack;
	}
	
	private CoordinateBox getAxisRelativePanel(Coordinates coords, int offset, String orientation)
	{
		if (orientation.equals("DOWN") || orientation.equals("UP"))
		{
			return new CoordinateBox(coords.x - offset, coords.y, coords.z - offset, coords.x + offset, coords.y, coords.z + offset);
		}
		else if (orientation.equals("NORTH") || orientation.equals("SOUTH"))
		{
			return new CoordinateBox(coords.x - offset, coords.y - offset, coords.z, coords.x + offset, coords.y + offset, coords.z);
		}
		else
		{
			return new CoordinateBox(coords.x, coords.y - offset, coords.z - offset, coords.x, coords.y + offset, coords.z + offset);
		}
	}
	
	private String getRelativeOrientation(Entity ent)
	{
		int dir = MathHelper.floor_double(ent.rotationPitch * 4.0F / 360.0F + 0.5D);
		
		if (dir == 1)
		{
			return "DOWN";
		}
		
		if (dir == -1)
		{
			return "UP";
		}
		
        return Direction.directions[MathHelper.floor_double(ent.rotationYaw * 4.0F / 360.0F + 0.5D) & 3];
	}
	
	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player) 
	{
		player.openGui(MozeCore.instance, Constants.MERCURIAL_GUI, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) 
	{
		return 1; 
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        this.itemIcon = register.registerIcon(this.getTexture("mercurial_eye"));
    }
}
