package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Hand;

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
	public boolean hitEntity(ItemStack stack, LivingEntity damaged, LivingEntity damager)
	{
		attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand)
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
