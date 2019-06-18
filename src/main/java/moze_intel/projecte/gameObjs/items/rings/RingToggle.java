package moze_intel.projecte.gameObjs.items.rings;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand)
	{
        if (!stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
		{
			player.playSound(PESounds.HEAL, 1.0F, 1.0F);
			stack.getTag().putBoolean(TAG_ACTIVE, true);
		}
		else
		{
			player.playSound(PESounds.UNCHARGE, 1.0F, 1.0F);
			stack.getTag().putBoolean(TAG_ACTIVE, false);
		}
		return true;
	}
}
