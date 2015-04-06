package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.Optional;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class BlackHoleBand extends RingToggle implements IBauble, IPedestalItem
{
	public BlackHoleBand()
	{
		super("black_hole");
		this.setNoRepair();
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			changeMode(player, stack);
		}
		
		return stack;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (world.isRemote || stack.getItemDamage() != 1 || !(entity instanceof EntityPlayer)) 
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		AxisAlignedBB bBox = player.boundingBox.expand(7, 7, 7);
		List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, bBox);
		
		for (EntityItem item : itemList)
		{
			if (ItemHelper.hasSpace(player.inventory.mainInventory, item.getEntityItem()))
			{
				item.delayBeforeCanPickup = 0;
				double d1 = (player.posX - item.posX);
				double d2 = (player.posY + (double)player.getEyeHeight() - item.posY);
				double d3 = (player.posZ - item.posZ);
				double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

				item.motionX += d1 / d4 * 0.1D;
				item.motionY += d2 / d4 * 0.1D;
				item.motionZ += d3 / d4 * 0.1D;
				
				item.moveEntity(item.motionX, item.motionY, item.motionZ);
			}
		}
		
		List<EntityLootBall> ballList = world.getEntitiesWithinAABB(EntityLootBall.class, bBox);
		
		for (EntityLootBall ball : ballList)
		{
			double d1 = (player.posX - ball.posX);
			double d2 = (player.posY + (double)player.getEyeHeight() - ball.posY);
			double d3 = (player.posZ - ball.posZ);
			double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

			ball.motionX += d1 / d4 * 0.1D;
			ball.motionY += d2 / d4 * 0.1D;
			ball.motionZ += d3 / d4 * 0.1D;
			
			ball.moveEntity(ball.motionX, ball.motionY, ball.motionZ);
		}
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.RING;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) 
	{
		this.onUpdate(stack, player.worldObj, player, 0, false);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	public void updateInPedestal(World world, int x, int y, int z)
	{
		DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(x, y, z));
		if (tile != null)
		{
			List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, tile.getEffectBounds());
			for (EntityItem item : list)
			{
				// Adapted from openBlocks and vanilla
				double dX = (x + 0.5 - item.posX);
				double dY = (y + 0.5 - item.posY);
				double dZ = (z + 0.5 - item.posZ);
				double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

				if (dist < 1.1 && !world.isRemote)
				{
					suckDumpItem(item, tile);
				}

				double vel = 1.0 - dist / 15.0;
				if (vel > 0.0D)
				{
					vel *= vel;
					item.motionX += dX / dist * vel * 0.05;
					item.motionY += dY / dist * vel * 0.2;
					item.motionZ += dZ / dist * vel * 0.05;
					item.moveEntity(item.motionX, item.motionY, item.motionZ);
				}
			}
		}
	}

	private void suckDumpItem(EntityItem item, DMPedestalTile tile)
	{
		List<TileEntity> list = WorldHelper.getAdjacentTileEntities(tile.getWorldObj(), tile);
		for (TileEntity tileEntity : list)
		{
			if (tileEntity instanceof IInventory)
			{
				IInventory inv = ((IInventory) tileEntity);
				ItemStack result = ItemHelper.pushStackInInv(inv, item.getEntityItem());
				if (result != null)
				{
					item.setEntityItemStack(result);
				}
				else
				{
					item.setDead();
				}
			}
		}
	}

	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = Lists.newArrayList();
		list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("pe.bhb.pedestal1"));
		list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("pe.bhb.pedestal2"));
		return list;
	}

}
