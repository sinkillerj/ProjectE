package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.item.IItemCharge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemCharge extends ItemPE implements IItemCharge
{
	byte numCharges;

	public ItemCharge(String unlocalName, byte numCharges)
	{
		this.numCharges = numCharges;
		this.setUnlocalizedName(unlocalName);
		this.setMaxStackSize(1);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTagCompound();
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		byte charge = getCharge(stack);
		
		//Must be beetween 0.0D - 1.0D
		return charge == 0 ? 1.0D : 1.0D - (double) charge / (double) (numCharges);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		if (!world.isRemote)
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (!stack.hasTagCompound())
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
	}
	
	@Override
	public byte getCharge(ItemStack stack)
	{
		return stack.stackTagCompound.getByte("Charge");
	}
	
	@Override
	public void changeCharge(EntityPlayer player, ItemStack stack)
	{
		byte currentCharge = getCharge(stack);

		if (player.isSneaking())
		{
			if (currentCharge > 0)
			{
				player.worldObj.playSoundAtEntity(player, "projecte:item.peuncharge", 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
				stack.stackTagCompound.setByte("Charge", (byte) (currentCharge - 1));
			}
		}
		else if (currentCharge < numCharges)
		{
			player.worldObj.playSoundAtEntity(player, "projecte:item.pecharge", 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
			stack.stackTagCompound.setByte("Charge", (byte) (currentCharge + 1));
		}
	}
}
