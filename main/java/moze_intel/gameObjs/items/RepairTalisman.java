package moze_intel.gameObjs.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RepairTalisman extends ItemBase
{
	public RepairTalisman()
	{
		this.setUnlocalizedName("repair_talisman");
		this.setMaxStackSize(1);
	}
	
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
			
				if (invStack == null || invStack.getItem() instanceof IItemModeChanger) 
				{
					continue;
				}
			
				if (!invStack.getHasSubtypes() && invStack.getMaxDamage() != 0 && invStack.getItemDamage() > 0)
				{
					invStack.setItemDamage(invStack.getItemDamage() - 1);
					inv.setInventorySlotContents(i, invStack);
					
					if (!hasAction)
					{
						hasAction = true;
					}
				}
			}
			
			if (hasAction)
			{
				stack.stackTagCompound.setByte("Cooldown", (byte) 19);
				player.inventoryContainer.detectAndSendChanges();
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("repair_talisman"));
	}
}
