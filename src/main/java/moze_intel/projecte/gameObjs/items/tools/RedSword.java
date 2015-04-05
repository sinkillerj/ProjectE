package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class RedSword extends DarkSword
{
	public RedSword() 
	{
		super("rm_sword", (byte)3, new String[]{
				StatCollector.translateToLocal("pe.redsword.mode1"),
				StatCollector.translateToLocal("pe.redsword.mode2")
		});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "sword";
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		attackWithCharge(stack, damaged, damager, 16.0F);
		return true;
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player)
	{
		attackAOE(stack, player, getMode(stack) == 1, 16.0F);
	}
}
