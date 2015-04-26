package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

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
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("divining2"));
	}
}
