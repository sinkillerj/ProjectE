package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.config.ProjectEConfig;
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
		boolean flag = ProjectEConfig.useOldDamage;
		attackWithCharge(stack, damaged, damager, flag ? REDSWORD_BASE_ATTACK : 1.0F);
		return true;
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player)
	{
		attackAOE(stack, player, getMode(stack) == 1, REDSWORD_BASE_ATTACK, 0);
	}
}
