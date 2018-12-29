package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.api.item.IModeChanger;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemMode extends ItemPE implements IModeChanger, IItemCharge
{
	private final byte numModes;
	private final int numCharge;
	private final String[] modes;
	
	public ItemMode(Builder builder, String unlocalName, int numCharge, String[] modeDescrp)
	{
		super(builder);
		this.numCharge = numCharge;
		this.numModes = (byte) modeDescrp.length;
		this.modes = modeDescrp;
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack)
	{
		return stack.hasTag() ? stack.getTag().getByte(TAG_MODE) : 0;
	}
	
	private String getUnlocalizedMode(ItemStack stack)
	{
		return modes[stack.getTag().getByte(TAG_MODE)];
	}
	
	protected void changeMode(ItemStack stack)
	{
		byte newMode = (byte) (getMode(stack) + 1);
		stack.getTag().putByte(TAG_MODE, (newMode > numModes - 1 ? 0 : newMode));
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
		player.sendMessage(new TextComponentTranslation("pe.item.mode_switch", modeName));
		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag advanced)
	{
		if (stack.hasTag() && this.numModes > 0)
		{
			ITextComponent root = new TextComponentTranslation("pe.item.mode");
			ITextComponent mode = new TextComponentTranslation(getUnlocalizedMode(stack)).setStyle(new Style().setColor(TextFormatting.AQUA));
			list.add(root.appendText(": ").appendSibling(mode));
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1.0D - (double) getCharge(stack) / numCharge;
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack)
	{
		return numCharge;
	}
}

