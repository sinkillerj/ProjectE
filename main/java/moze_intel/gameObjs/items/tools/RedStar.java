package moze_intel.gameObjs.items.tools;

import java.util.ArrayList;
import java.util.List;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.entity.LootBall;
import moze_intel.gameObjs.items.ItemCharge;
import moze_intel.network.PacketHandler;
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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RedStar extends ItemCharge 
{
	public RedStar() 
	{
		super("rm_morning_star", (byte) 4);
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase eLiving)
    {
		if (world.isRemote || !(eLiving instanceof EntityPlayer) || !canHarvestBlock(block, stack) || this.getCharge(stack) == 0)
		{
			return false;
		}
		
		EntityPlayer player = (EntityPlayer) eLiving;

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);

		if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK)
		{
			return false;
		}
		
		CoordinateBox box = getRelativeBox(new Coordinates(x, y, z), ForgeDirection.getOrientation(mop.sideHit), this.getCharge(stack));
		List<ItemStack> drops = new ArrayList();
		
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
		
		if (!drops.isEmpty())
		{
			world.spawnEntityInWorld(new LootBall(world, drops, player.posX, player.posY, player.posZ));
			PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
		}
		
		return true;
    }
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
			
			if (mop == null || !mop.typeOfHit.equals(MovingObjectType.BLOCK))
			{
				int offset = this.getCharge(stack) + 3;
				CoordinateBox box = new CoordinateBox(player.posX - offset, player.posY - offset, player.posZ - offset, player.posX + offset, player.posY + offset, player.posZ + offset);
				List<ItemStack> drops = new ArrayList();
				
				for (int x = (int) box.minX; x <= box.maxX; x++)
					for (int y = (int) box.minY; y <= box.maxY; y++)
						for (int z = (int) box.minZ; z <= box.maxZ; z++)
						{
							Block block = world.getBlock(x, y, z);
							
							if (Utils.isOre(block) && canHarvestBlock(block, stack))
							{
								Utils.harvestVein(world, player, stack, new Coordinates(x, y, z), block, drops, 0);
							}
						}
				
				if (!drops.isEmpty())
				{
					world.spawnEntityInWorld(new LootBall(world, drops, player.posX, player.posY, player.posZ));
					PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
				}
				
				return stack;
			}
			
			Block block = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
			List<ItemStack> drops = new ArrayList();
			
			if (block.getBlockHardness(null, 0, 0, 0) == -1)
			{
				return stack;
			}
			if (Utils.isOre(block) || block.equals(Blocks.gravel))
			{
				Utils.harvestVein(world, player, stack, new Coordinates(mop), block, drops, 0);
			}
			else if (block.getHarvestTool(0) == null || block.getHarvestTool(0).equals("shovel"))
			{
				CoordinateBox box = getRelativeBox(new Coordinates(mop), ForgeDirection.getOrientation(mop.sideHit), this.getCharge(stack) + 1);
				byte charge = this.getCharge(stack);

				for (int x = (int) box.minX; x <= box.maxX; x++)
					for (int y = (int) box.minY; y <= box.maxY; y++)
						for (int z = (int) box.minZ; z <= box.maxZ; z++)
						{
							Block b = world.getBlock(x, y, z);
							
							if (b != Blocks.air && canHarvestBlock(b, stack))
							{
								drops.addAll(Utils.getBlockDrops(world, player, b, stack, x, y, z));
								world.setBlockToAir(x, y, z);
							}
						}
			}
			
			if (!drops.isEmpty())
			{
				world.spawnEntityInWorld(new LootBall(world, drops, player.posX, player.posY, player.posZ));
				PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
			}
		}
		
		return stack;
	}
	
	private CoordinateBox getRelativeBox(Coordinates coords, ForgeDirection direction, int charge)
	{
		if (direction.offsetX != 0)
		{
			return new CoordinateBox(coords.x, coords.y - charge, coords.z - charge, coords.x, coords.y + charge, coords.z + charge);
		}
		else if (direction.offsetY != 0)
		{
			return new CoordinateBox(coords.x - charge, coords.y, coords.z - charge, coords.x + charge, coords.y, coords.z + charge);
		}
		else
		{
			return new CoordinateBox(coords.x - charge, coords.y - charge, coords.z, coords.x + charge, coords.y + charge, coords.z);
		}
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		if (block == Blocks.bedrock)
		{
			return false;
		}
		
		String harvest = block.getHarvestTool(0);
		
		if (harvest == null || harvest.equals("shovel") || harvest.equals("pickaxe") || harvest.equals("chisel"))
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
		
		return 48.0f;
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
		this.itemIcon = register.registerIcon(this.getTexture("rm_tools", "morning_star"));
	}
}
