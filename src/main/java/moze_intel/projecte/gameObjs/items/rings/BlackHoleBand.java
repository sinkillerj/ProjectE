package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
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
		if (stack.getItemDamage() != 1 || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		AxisAlignedBB bBox = player.boundingBox.expand(7, 7, 7);
		List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, bBox);
		
		for (EntityItem item : itemList)
		{
			if (Utils.hasSpace(player.inventory.mainInventory, item.getEntityItem())) 
			{
				item.delayBeforeCanPickup = 0;
				Utils.gravitateEntityTowards(player.posX, player.posY, player.posZ, item);
			}
		}
		
		List<EntityLootBall> ballList = world.getEntitiesWithinAABB(EntityLootBall.class, bBox);
		
		for (EntityLootBall ball : ballList)
		{
			Utils.gravitateEntityTowards(player.posX, player.posY, player.posZ, ball);
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
				if (item.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < 1.21 && !world.isRemote)
				{
					suckDumpItem(item, tile);
				}
			}
		}
	}

	private void suckDumpItem(EntityItem item, DMPedestalTile tile)
	{
		List<TileEntity> list = Utils.getAdjacentTileEntities(tile.getWorldObj(), tile);
		for (TileEntity tileEntity : list)
		{
			if (tileEntity instanceof IInventory)
			{
				IInventory inv = ((IInventory) tileEntity);
				ItemStack result = Utils.pushStackInInv(inv, item.getEntityItem());
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
		List<String> list = new ArrayList<String>();
		list.add(EnumChatFormatting.BLUE + "Sucks in nearby item drops");
		list.add(EnumChatFormatting.BLUE + "Dumps in adjacent inventories");
		return list;
	}

}
