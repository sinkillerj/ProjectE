package moze_intel.gameObjs.items;

import java.util.ArrayList;
import java.util.List;

import moze_intel.MozeCore;
import moze_intel.gameObjs.entity.LensProjectile;
import moze_intel.gameObjs.entity.LootBall;
import moze_intel.network.packets.ParticlePKT;
import moze_intel.network.packets.SwingItemPKT;
import moze_intel.utils.Constants;
import moze_intel.utils.CoordinateBox;
import moze_intel.utils.Coordinates;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CataliticLens extends ItemCharge implements IProjectileShooter
{
	public CataliticLens() 
	{
		super("catalitic_lens", (byte) 4);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
		if (world.isRemote) return stack;

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);

		if (mop != null && mop.typeOfHit.equals(MovingObjectType.BLOCK))
		{
			int charge = this.getCharge(stack);
			int numRows;
			boolean hasAction = false;
			
			if (charge == 0)
				numRows = 1;
			else if (charge == 1)
				numRows = 16;
			else if (charge == 2)
				numRows = 32;
			else numRows = 64;
			
			ForgeDirection direction = ForgeDirection.getOrientation(mop.sideHit);
			
			Coordinates coords = new Coordinates(mop);
			CoordinateBox box = getBoxFromDirection(direction, coords, numRows);
			
			List<ItemStack> drops = new ArrayList();
			
			for (int x = (int) box.minX; x <= box.maxX; x++)
				for (int y = (int) box.minY; y <= box.maxY; y++)
					for (int z = (int) box.minZ; z <= box.maxZ; z++)
					{
						Block block = world.getBlock(x, y, z);
						float hardness = block.getBlockHardness(world, x, y, z);
						if (block == null || block == Blocks.air || hardness >= 50.0F || hardness == -1.0F)
							continue;
						
						if (!this.consumeFuel(player, stack, 8, true))
						{
							break;
						}
						
						if (!hasAction)
							hasAction = true;
						
						ArrayList<ItemStack> list = block.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
						if (list != null && list.size() > 0)
							drops.addAll(list);
						
						world.setBlockToAir(x, y, z);
						if (world.rand.nextInt(8) == 0)
							MozeCore.pktHandler.sendToAllAround(new ParticlePKT("largesmoke", x, y, z), new TargetPoint(world.provider.dimensionId, x, y + 1, z, 32));
					}
			
			MozeCore.pktHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
			if (hasAction)
			{
				world.playSoundAtEntity(player, "projecte:destruct", 0.5F, 1.0F);
				world.spawnEntityInWorld(new LootBall(world, drops, player.posX, player.posY, player.posZ));
			}
		}
			
        return stack;
    }
	
	public CoordinateBox getBoxFromDirection(ForgeDirection direction, Coordinates coords, int charge)
	{
		charge--;
		
		if (direction.offsetX != 0)
		{
			if (direction.offsetX > 0)
				return new CoordinateBox(coords.x - charge, coords.y - 1, coords.z - 1, coords.x, coords.y + 1, coords.z + 1);
			else return new CoordinateBox(coords.x, coords.y - 1, coords.z - 1, coords.x + charge, coords.y + 1, coords.z + 1);
		}
		else if (direction.offsetY != 0)
		{
			if (direction.offsetY > 0)
				return new CoordinateBox(coords.x - 1, coords.y - charge, coords.z - 1, coords.x + 1, coords.y, coords.z + 1);
			else return new CoordinateBox(coords.x - 1, coords.y, coords.z - 1, coords.x + 1, coords.y + charge, coords.z + 1);
		}
		else
		{
			if (direction.offsetZ > 0)
				return new CoordinateBox(coords.x - 1, coords.y - 1, coords.z - charge, coords.x + 1, coords.y + 1, coords.z);
			else return new CoordinateBox(coords.x - 1, coords.y - 1, coords.z, coords.x + 1, coords.y + 1, coords.z + charge);
		}
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack)
	{
		World world = player.worldObj;
		int requiredEmc = Constants.EXPLOSIVE_LENS_COST[this.getCharge(stack)];
		
		if (!this.consumeFuel(player, stack, requiredEmc, true))
		{
			return false;
		}
		
		world.playSoundAtEntity(player, "projecte:wall", 1.0F, 1.0F);
		world.spawnEntityInWorld(new LensProjectile(world, player, this.getCharge(stack)));
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("catalitic_lens"));
	}
}
