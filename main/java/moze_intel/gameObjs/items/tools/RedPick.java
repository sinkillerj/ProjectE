package moze_intel.gameObjs.items.tools;

import java.util.ArrayList;
import java.util.List;

import moze_intel.MozeCore;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.entity.LootBall;
import moze_intel.gameObjs.items.ItemMode;
import moze_intel.network.packets.SwingItemPKT;
import moze_intel.utils.CoordinateBox;
import moze_intel.utils.Coordinates;
import moze_intel.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RedPick extends ItemMode
{
	public RedPick()
	{
		super("rm_pick", (byte) 4, new String[] {"Standard", "3x Tallshot", "3x Wideshot", "3x Longshot"});
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase eLiving)
    {
		if (world.isRemote || !(eLiving instanceof EntityPlayer))
		{
			return false;
		}
		
		EntityPlayer player = (EntityPlayer) eLiving;
		byte mode = this.getMode(stack);
		
		if (mode == 0)
		{
			return false;
		}
			
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
		CoordinateBox box = null;
		
		if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK)
		{
			return false;
		}
		
		ForgeDirection direction = ForgeDirection.getOrientation(mop.sideHit);
		
		if (mode == 1)
		{
			box = new CoordinateBox(x, y - 1, z, x, y + 1, z);
		}
		else if (mode == 2)
		{
			if (direction.offsetX != 0)
			{
				box = new CoordinateBox(x, y, z - 1, x, y, z + 1);
			}
			else if (direction.offsetZ != 0)
			{
				box = new CoordinateBox(x - 1, y, z, x + 1, y, z);
			}
			else
			{
				int dir = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
				
				if (dir == 0 || dir == 2)
				{
					box = new CoordinateBox(x, y, z - 1, x, y, z + 1);
				}
				else
				{
					box = new CoordinateBox(x - 1, y, z, x + 1, y, z);
				}
			}
		}
		else
		{
			if (direction.offsetX == 1)
			{
				box = new CoordinateBox(x - 2, y, z, x, y, z);
			}
			else if (direction.offsetX == - 1)
			{
				box = new CoordinateBox(x, y, z, x + 2, y, z);
			}
			else if (direction.offsetZ == 1)
			{
				box = new CoordinateBox(x, y, z - 2, x, y, z);
			}
			else if (direction.offsetZ == -1)
			{
				box = new CoordinateBox(x, y, z, x, y, z + 2);
			}
			else if (direction.offsetY == 1)
			{
				box = new CoordinateBox(x, y - 2, z, x, y, z);
			}
			else 
			{
				box = new CoordinateBox(x, y, z, x, y + 2, z);
			}
		}
		
		List<ItemStack> drops = new ArrayList<ItemStack>();
		
		for (int i = (int) box.minX; i <= box.maxX; i++)
			for (int j = (int) box.minY; j <= box.maxY; j++)
				for (int k = (int) box.minZ; k <= box.maxZ; k++)
				{
					Block b = world.getBlock(i, j, k);
					
					if (b != Blocks.air && canHarvestBlock(b, stack))
					{
						drops.addAll(Utils.getBlockDrops(world, player, b, stack, i, j, k));
						world.setBlockToAir(i, j, k);
					}
				}
			
		world.spawnEntityInWorld(new LootBall(world, drops, x, y, z));
        return false;
    }
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			int offset = this.getCharge(stack) + 3;
			CoordinateBox box = new CoordinateBox(player.posX - offset, player.posY - offset, player.posZ - offset, player.posX + offset, player.posY + offset, player.posZ + offset);
			List<ItemStack> drops = new ArrayList();
			
			for (int x = (int) box.minX; x <= box.maxX; x++)
				for (int y = (int) box.minY; y <= box.maxY; y++)
					for (int z = (int) box.minZ; z <= box.maxZ; z++)
					{
						Block block = world.getBlock(x, y, z);
						
						if (Utils.isOre(block))
						{
							Utils.harvestVein(world, player, stack, new Coordinates(x, y, z), block, drops, 0);
						}
					}
			
			if (!drops.isEmpty())
			{
				world.spawnEntityInWorld(new LootBall(world, drops, player.posX, player.posY, player.posZ));
				MozeCore.pktHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
			}
		}
		
		return stack;
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		if (block.getBlockHardness(null, 0, 0, 0) == -1)
		{
			return false;
		}
		
		String harvest = block.getHarvestTool(0);
		
		if (harvest == null || harvest.equals("pickaxe") || harvest.equals("chisel"))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if (block == ObjHandler.matterBlock || block == ObjHandler.dmFurnaceOff || block == ObjHandler.dmFurnaceOn || block == ObjHandler.rmFurnaceOff || block == ObjHandler.rmFurnaceOn)
		{
			return 1200000.0F;
		}
		
		String harvest = block.getHarvestTool(metadata);
		
		if(harvest == null || harvest.equals("pickaxe") || harvest.equals("chisel"))
		{
			return 16.0f + (14.0F * this.getCharge(stack));
		}
		
		return 1.0F;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
		return true;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("rm_tools", "pick"));
	}
}
