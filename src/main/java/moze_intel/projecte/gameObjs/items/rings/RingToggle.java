package moze_intel.projecte.gameObjs.items.rings;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nonnull;

public abstract class RingToggle extends ItemPE implements IModeChanger
{
	public RingToggle(Properties props)
	{
		super(props);
		this.addPropertyOverride(ACTIVE_NAME, ACTIVE_GETTER);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return false;
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack)
	{
        return stack.getOrCreateTag().getBoolean(TAG_ACTIVE) ? (byte) 1 : 0;
	}

	@Override
	public boolean changeMode(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
        if (!stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
		{
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1.0F, 1.0F);
			stack.getTag().putBoolean(TAG_ACTIVE, true);
		}
		else
		{
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.UNCHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
			stack.getTag().putBoolean(TAG_ACTIVE, false);
		}
		return true;
	}
}
