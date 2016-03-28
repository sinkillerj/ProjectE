package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.item.IModeChanger;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

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
		return stack.getTagCompound().getByte("Mode");
	}
	
	public String getModeDescription(ItemStack stack)
	{
		return modes[stack.getTagCompound().getByte("Mode")];
	}
	
	protected void changeMode(ItemStack stack)
	{
		byte newMode = (byte) (getMode(stack) + 1);
		stack.getTagCompound().setByte("Mode", (newMode > numModes - 1 ? 0 : newMode));
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		if (numModes == 0)
		{
			return;
		}
		changeMode(stack);
		player.addChatComponentMessage(new TextComponentTranslation("pe.item.mode_switch", modes[getMode(stack)]));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		if (stack.hasTagCompound() && this.numModes > 0)
		{
			list.add(I18n.translateToLocal("pe.item.mode") + ": " + TextFormatting.AQUA + getModeDescription(stack));
		}
	}
}

