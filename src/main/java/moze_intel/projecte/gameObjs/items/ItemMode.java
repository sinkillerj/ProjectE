package moze_intel.projecte.gameObjs.items;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ItemMode extends ItemPE implements IItemMode, IItemCharge
{
	private final int numCharge;
	private final String[] modes;
	
	public ItemMode(Properties props, int numCharge, String[] modeDescrp)
	{
		super(props);
		this.numCharge = numCharge;
		this.modes = modeDescrp;
		addItemCapability(new ChargeItemCapabilityWrapper());
		addItemCapability(new ModeChangerItemCapabilityWrapper());
	}

	@Override
	public String[] getModeTranslationKeys() {
		return modes;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag advanced)
	{
		list.add(getToolTip(stack));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1.0D - (double) getCharge(stack) / getNumCharges(stack);
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack)
	{
		return numCharge;
	}
}

