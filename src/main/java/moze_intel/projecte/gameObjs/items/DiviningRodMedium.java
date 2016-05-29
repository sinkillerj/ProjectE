package moze_intel.projecte.gameObjs.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class DiviningRodMedium extends DiviningRodLow
{
	public DiviningRodMedium()
	{
		super(new String[]{"3x3x3", "16x3x3"});
		this.setUnlocalizedName("divining_rod_2");
	}

	// Only for subclasses
	protected DiviningRodMedium(String[] modeDesc)
	{
		super(modeDesc);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
	{
		if (stack.hasTagCompound())
		{
			list.add(I18n.format("pe.item.mode") + ": " + TextFormatting.AQUA + modes[getMode(stack)]);
		}
	}
}
