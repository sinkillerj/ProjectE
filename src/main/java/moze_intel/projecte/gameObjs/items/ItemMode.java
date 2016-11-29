package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.item.IModeChanger;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class ItemMode extends ItemCharge implements IModeChanger
{
	private final byte numModes;
	private final String[] modes;
	
	public ItemMode(String unlocalName, byte numCharge, String[] modeDescrp)
	{
		super(unlocalName, numCharge);
		this.numModes = (byte) modeDescrp.length;
		this.modes = modeDescrp;
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack)
	{
		return stack.hasTagCompound() ? stack.getTagCompound().getByte("Mode") : 0;
	}
	
	private String getUnlocalizedMode(ItemStack stack)
	{
		return modes[stack.getTagCompound().getByte("Mode")];
	}
	
	protected void changeMode(ItemStack stack)
	{
		byte newMode = (byte) (getMode(stack) + 1);
		stack.getTagCompound().setByte("Mode", (newMode > numModes - 1 ? 0 : newMode));
	}
	
	@Override
	public boolean changeMode(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		if (numModes == 0)
		{
			return false;
		}
		changeMode(stack);

		TextComponentTranslation modeName = new TextComponentTranslation(modes[getMode(stack)]);
		player.addChatComponentMessage(new TextComponentTranslation("pe.item.mode_switch", modeName));
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
	{
		if (stack.hasTagCompound() && this.numModes > 0)
		{
			list.add(I18n.format("pe.item.mode") + ": " + TextFormatting.AQUA + I18n.format(getUnlocalizedMode(stack)));
		}
	}
}

