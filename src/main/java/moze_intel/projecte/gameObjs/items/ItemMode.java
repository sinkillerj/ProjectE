package moze_intel.projecte.gameObjs.items;

import java.util.List;

import moze_intel.projecte.api.IModeChanger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemMode extends ItemCharge implements IModeChanger
{
	private byte numModes;
	private String[] modes;
	
	public ItemMode(String unlocalName, byte numCharge, String[] modeDescrp)
	{
		super(unlocalName, numCharge);
		this.numModes = (byte) modeDescrp.length;
		this.modes = modeDescrp;
	}
	
	public byte getMode(ItemStack stack)
	{
		return stack.stackTagCompound.getByte("Mode");
	}
	
	public String getModeDescription(ItemStack stack)
	{
		return modes[stack.stackTagCompound.getByte("Mode")];
	}
	
	protected void changeMode(ItemStack stack)
	{
		byte newMode = (byte) (getMode(stack) + 1);
		stack.stackTagCompound.setByte("Mode", (byte) (newMode > numModes - 1 ? 0 : newMode));
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		changeMode(stack);
		player.addChatComponentMessage(new ChatComponentText("Switched to "+modes[getMode(stack)]+" mode"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		if (stack.hasTagCompound())
		{
			list.add("Mode: "+EnumChatFormatting.AQUA+getModeDescription(stack));
		}
	}
}

