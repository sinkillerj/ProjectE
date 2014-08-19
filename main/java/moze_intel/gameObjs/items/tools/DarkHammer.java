package moze_intel.gameObjs.items.tools;

import java.util.ArrayList;
import java.util.List;

import moze_intel.MozeCore;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.entity.LootBall;
import moze_intel.gameObjs.items.ItemCharge;
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

public class DarkHammer extends ItemCharge
{
	public DarkHammer() 
	{
		super("dm_hammer", (byte) 3);
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase eLiving)
    {
		if (world.isRemote || !(eLiving instanceof EntityPlayer) || this.getCharge(stack) == 0 || !canHarvestBlock(block, stack))
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
						ArrayList<ItemStack> blockDrops = Utils.getBlockDrops(world, player, b, stack, x, y, z);
						
						if (blockDrops.isEmpty())
						{
							continue;
						}
						
						drops.addAll(blockDrops);
						world.setBlockToAir(i, j, k);
					}
				}
		
		if (!drops.isEmpty())
		{
			world.spawnEntityInWorld(new LootBall(world, drops, player.posX, player.posY, player.posZ));
			MozeCore.pktHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
		}
		
		return true;
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
		if (block.equals(Blocks.bedrock))
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
		if (block == ObjHandler.matterBlock)
		{
			return 1200000.0F;
		}
		
		String harvest = block.getHarvestTool(metadata);
		
		if(harvest == null || harvest.equals("pickaxe") || harvest.equals("chisel"))
		{
			return 14.0f + (12.0F * this.getCharge(stack));
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
		this.itemIcon = register.registerIcon(this.getTexture("dm_tools", "hammer"));
	}
}
