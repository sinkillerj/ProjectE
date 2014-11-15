package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IModeChanger;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RepairTalisman extends ItemPE implements IBauble
{
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
		byte coolDown = stack.stackTagCompound.getByte("Cooldown");
		
		if (coolDown > 0)
		{
			stack.stackTagCompound.setByte("Cooldown", (byte) (coolDown - 1));
		}
		else
		{
			IInventory inv = player.inventory;
			boolean hasAction = false;
			
			for (int i = 0; i < 36; i++)
			{
				ItemStack invStack = inv.getStackInSlot(i);
			
				if (invStack == null || invStack.getItem() instanceof IModeChanger) 
				{
					continue;
				}
			
				if (!invStack.getHasSubtypes() && invStack.getMaxDamage() != 0 && invStack.getItemDamage() > 0)
				{
					invStack.setItemDamage(invStack.getItemDamage() - 1);
					//inv.setInventorySlotContents(i, invStack);
					
					if (!hasAction)
					{
						hasAction = true;
					}
				}
			}
			
			for (int i = 0; i < 4; i++)
			{
				ItemStack invStack = player.inventory.armorItemInSlot(i);
			
				if (invStack == null || invStack.getItem() instanceof IModeChanger)
				{
					continue;
				}
			
				if (!invStack.getHasSubtypes() && invStack.getMaxDamage() != 0 && invStack.getItemDamage() > 0)
				{
					invStack.setItemDamage(invStack.getItemDamage() - 1);
					//inv.setInventorySlotContents(i, invStack);
					
					if (!hasAction)
					{
						hasAction = true;
					}
				}
			}
			
			if (hasAction)
			{
				stack.stackTagCompound.setByte("Cooldown", (byte) 19);
				//player.inventoryContainer.detectAndSendChanges();
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("repair_talisman"));
	}

	@Override
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.BELT;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) 
	{
		this.onUpdate(stack, player.worldObj, player, 0, false);
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}
}
