package moze_intel.gameObjs.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemMode extends ItemCharge
{
	private byte numModes;
	private String[] modes;
	
	public ItemMode(String unlocalName, byte numCharge, String[] modeDescrp)
	{
		super(unlocalName, numCharge);
		this.numModes = (byte) modeDescrp.length;
		this.modes = modeDescrp;
	}
	
	public byte GetMode(ItemStack stack)
	{
		return stack.stackTagCompound.getByte("Mode");
	}
	
	public String GetModeDescription(ItemStack stack)
	{
		return modes[stack.stackTagCompound.getByte("Mode")];
	}
	
	protected void ChangeMode(ItemStack stack)
	{
		byte newMode = (byte) (GetMode(stack) + 1);
		stack.stackTagCompound.setByte("Mode", (byte) (newMode > numModes - 1 ? 0 : newMode));
	}
	
	public void changeMode(ItemStack stack, EntityPlayer player)
	{
		ChangeMode(stack);
		player.addChatComponentMessage(new ChatComponentText("Switched to "+modes[GetMode(stack)]+" mode"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		if (stack.hasTagCompound())
		{
			list.add("Mode: "+EnumChatFormatting.AQUA+GetModeDescription(stack));
		}
	}
}

