package moze_intel.projecte.gameObjs.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
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
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		if (stack.hasTagCompound())
		{
			list.add(StatCollector.translateToLocal("pe.item.mode") + ": " + EnumChatFormatting.AQUA + modes[getMode(stack)]);
		}
	}
}
