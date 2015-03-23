package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import moze_intel.projecte.api.IAlchBagItem;
import moze_intel.projecte.api.IAlchChestItem;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.container.inventory.AlchBagInventory;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class BlackHoleBand extends RingToggle implements IAlchBagItem, IAlchChestItem, IBauble, IPedestalItem
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
		if (stack.getItemDamage() != 1 || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		AxisAlignedBB bBox = player.boundingBox.expand(7, 7, 7);
		List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, bBox);
		
		for (EntityItem item : itemList)
		{
			Utils.gravitateEntityTowards(player.posX, player.posY + (world.isRemote ? 0 : 1.62), player.posZ, item);
			// Need to change y level on clientside due to vanilla discrepancy which is fixed in 1.8
		}
		
		List<EntityLootBall> ballList = world.getEntitiesWithinAABB(EntityLootBall.class, bBox);
		
		for (EntityLootBall ball : ballList)
		{
			Utils.gravitateEntityTowards(player.posX, player.posY + (world.isRemote ? 0 : 1.62), player.posZ, ball);
			// Need to change y level on clientside due to vanilla discrepancy which is fixed in 1.8
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
				Utils.gravitateEntityTowards(x + 0.5, y + 0.5, z + 0.5, item);
				if (!world.isRemote)
				{
					double distSq = item.getDistanceSq(x + 0.5, y + 0.5, z + 0.5);
					if (distSq < 1.21)
					{
						Utils.insertEntityItemIntoAdjacentInv(item, tile);
					}
				}
			}

			List<EntityLootBall> balls = world.getEntitiesWithinAABB(EntityLootBall.class, tile.getEffectBounds());
			for (EntityLootBall ball: balls)
			{
				Utils.gravitateEntityTowards(x + 0.5, y + 0.5, z + 0.5, ball);
				if (!world.isRemote)
				{
					double distSq = ball.getDistanceSq(x + 0.5, y + 0.5, z + 0.5);
					if (distSq < 1.21)
					{
						Utils.insertLootballIntoAdjacentInv(ball, tile);
					}
				}
			}
		}
	}



	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = new ArrayList<String>();
		list.add(EnumChatFormatting.BLUE + "Sucks in nearby item drops");
		list.add(EnumChatFormatting.BLUE + "Dumps in adjacent inventories");
		return list;
	}

	@Override
	public void updateInAlchChest(AlchChestTile chest, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			return;
		}
		double centeredX = chest.xCoord + 0.5;
		double centeredY = chest.yCoord + 0.5;
		double centeredZ = chest.zCoord + 0.5;
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(centeredX - 7, centeredY - 7, centeredZ - 7, centeredX + 7, centeredY + 7, centeredZ + 7);
		for (EntityItem item : ((List<EntityItem>) chest.getWorldObj().getEntitiesWithinAABB(EntityItem.class, aabb)))
		{
			Utils.gravitateEntityTowards(centeredX, centeredY, centeredZ, item);
			if (!item.worldObj.isRemote)
			{
				double dist = item.getDistanceSq(centeredX, centeredY, centeredZ);
				if (dist < 1.21)
				{
					Utils.insertEntityItemIntoInv(item, chest);
				}
			}
		}

		for (EntityLootBall ball: ((List<EntityLootBall>) chest.getWorldObj().getEntitiesWithinAABB(EntityLootBall.class, aabb)))
		{
			Utils.gravitateEntityTowards(centeredX, centeredY, centeredZ, ball);
			if (!ball.worldObj.isRemote)
			{
				double dist = ball.getDistanceSq(centeredX, centeredY, centeredZ);
				if (dist < 1.21)
				{
					Utils.insertLootballntoInv(ball, chest);
				}
			}
		}

	}

	@Override
	public void updateInAlchBag(EntityPlayer player, ItemStack bag, ItemStack item)
	{
		if (item.getItemDamage() == 0)
		{
			return;
		}

		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(player.posX - 7, player.posY - 7, player.posZ - 7, player.posX + 7, player.posY + 7, player.posZ + 7);
		for (EntityItem ent : ((List<EntityItem>) player.worldObj.getEntitiesWithinAABB(EntityItem.class, aabb)))
		{
			Utils.gravitateEntityTowards(player.posX, player.posY, player.posZ, ent);
		}
		for (EntityLootBall ball : ((List<EntityLootBall>) player.worldObj.getEntitiesWithinAABB(EntityLootBall.class, aabb)))
		{
			Utils.gravitateEntityTowards(player.posX, player.posY, player.posZ, ball);
		}
	}

	@Override
	public boolean onPickUp(EntityPlayer player, ItemStack bag, EntityItem item)
	{
		IInventory inv = player.openContainer instanceof AlchBagContainer ? ((AlchBagContainer) player.openContainer).inventory : new AlchBagInventory(player, bag);
		Utils.insertEntityItemIntoInv(item, inv);
		return true;
	}
}
