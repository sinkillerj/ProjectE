package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IAlchBagItem;
import moze_intel.projecte.api.IAlchChestItem;
import moze_intel.projecte.api.IModeChanger;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.gameObjs.container.inventory.AlchBagInventory;
import moze_intel.projecte.gameObjs.items.rings.RingToggle;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.PlayerTimers;
import moze_intel.projecte.playerData.IOHandler;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class RepairTalisman extends ItemPE implements IAlchBagItem, IAlchChestItem, IBauble, IPedestalItem
{
	private int repairCooldown;

	public RepairTalisman()
	{
		this.setUnlocalizedName("repair_talisman");
		this.setMaxStackSize(1);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (!stack.hasTagCompound())
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
		
		if (world.isRemote || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;

		PlayerTimers.activateRepair(player);

		if (PlayerTimers.canRepair(player))
		{
			repairAllItems(player);
		}
	}

	private void repairAllItems(EntityPlayer player)
	{
		if (player.isSwingInProgress)
		{
			return;
		}

		repairAllInInventory(player.inventory);
	}

	/**
	 * Repair everything in this IInventory
	 * @param inv The inventory
	 * @return Whether the talisman repaired anything
	 */
	private boolean repairAllInInventory(IInventory inv)
	{
		boolean hadRepairs = false;
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack invStack = inv.getStackInSlot(i);

			if (invStack == null || invStack.getItem() instanceof IModeChanger || !invStack.getItem().isRepairable())
			{
				continue;
			}

			if (!invStack.getHasSubtypes() && invStack.getMaxDamage() != 0 && invStack.getItemDamage() > 0)
			{
				invStack.setItemDamage(invStack.getItemDamage() - 1);
				hadRepairs = true;
				inv.markDirty();
			}
		}
		return hadRepairs;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("repair_talisman"));
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.BELT;
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
		if (!world.isRemote)
		{
			if (repairCooldown == 0)
			{
				DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(x, y, z));
				List<EntityPlayerMP> list = world.getEntitiesWithinAABB(EntityPlayerMP.class, tile.getEffectBounds());
				for (EntityPlayerMP player : list)
				{
					repairAllItems(player);
				}
				repairCooldown = 20;
			}
			else
			{
				repairCooldown--;
			}
		}
	}

	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = new ArrayList<String>();
		list.add(EnumChatFormatting.BLUE + "Repairs nearby player items");
		list.add(EnumChatFormatting.BLUE + "1 durability/s");
		return list;
	}

	@Override
	public void updateInAlchChest(AlchChestTile chest, ItemStack stack)
	{
		if (!chest.getWorldObj().isRemote)
		{
			byte coolDown = stack.stackTagCompound.getByte("Cooldown");
			if (coolDown > 0)
			{
				stack.stackTagCompound.setByte("Cooldown", (byte) (coolDown - 1));
			}
			else
			{
				boolean hadAction = repairAllInInventory(chest);
				if (hadAction)
				{
					stack.stackTagCompound.setByte("Cooldown", (byte) 19);
				}
			}
		}
	}

	@Override
	public void updateInAlchBag(EntityPlayer player, ItemStack bag, ItemStack item)
	{
		if (!player.worldObj.isRemote)
		{
			byte coolDown = item.stackTagCompound.getByte("Cooldown");
			if (coolDown > 0)
			{
				item.stackTagCompound.setByte("Cooldown", (byte) (coolDown - 1));
				IOHandler.markDirty();
			}
			else
			{
				boolean hadAction = repairAllInInventory(new AlchBagInventory(player, bag));
				if (hadAction)
				{
					item.stackTagCompound.setByte("Cooldown", (byte) 19);
					IOHandler.markDirty();
				}
			}

		}
	}

	@Override
	public boolean onPickUp(EntityPlayer player, EntityItem item)
	{
		return false;
	}
}
