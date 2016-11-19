package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IItemCharge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemCharge extends ItemPE implements IItemCharge
{
	final byte numCharges;

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
		
		return charge == 0 ? 1.0D : 1.0D - (double) charge / (double) (numCharges);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		if (!world.isRemote)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}
	}
	
	@Override
	public byte getCharge(@Nonnull ItemStack stack)
	{
		return stack.hasTagCompound() ? stack.getTagCompound().getByte("Charge") : 0;
	}
	
	@Override
	public boolean changeCharge(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		byte currentCharge = getCharge(stack);

		if (player.isSneaking())
		{
			if (currentCharge > 0)
			{
				player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.UNCHARGE, SoundCategory.PLAYERS, 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
				stack.getTagCompound().setByte("Charge", (byte) (currentCharge - 1));
				return true;
			}
		}
		else if (currentCharge < numCharges)
		{
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
			stack.getTagCompound().setByte("Charge", (byte) (currentCharge + 1));
			return true;
		}

		return false;
	}
}
