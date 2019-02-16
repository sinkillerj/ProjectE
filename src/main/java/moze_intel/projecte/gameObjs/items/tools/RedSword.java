package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;

public class RedSword extends DarkSword
{
	public RedSword(Properties props)
	{
		super(props, (byte)3, new String[]{
				"pe.redsword.mode1",
				"pe.redsword.mode2"
		});
		this.peToolMaterial = EnumMatterType.RED_MATTER;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, EnumHand hand)
	{
		if (player.getCooledAttackStrength(0F) == 1)
		{
			attackAOE(stack, player, getMode(stack) == 1, REDSWORD_BASE_ATTACK, 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		}
		else
		{
			return false;
		}
	}
}
