package moze_intel.projecte.emc.nbt.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

//TODO: When we add annotations and priorities we want to make sure this gets to happen first
public class DamageProcessor implements INBTProcessor {

	@Nullable
	@Override
	public CompoundNBT getPersistentNBT(@Nonnull ItemInfo info) {
		return null;
	}

	@Override
	public long recalculateEMC(@Nonnull ItemInfo info, long currentEMC) throws ArithmeticException {
		Item item = info.getItem();
		if (item.isDamageable()) {
			ItemStack fakeStack = info.createStack();
			int maxDamage = item.getMaxDamage(fakeStack);
			int damage = item.getDamage(fakeStack);
			if (damage > maxDamage) {
				//This may happen if mods implement their custom damage values incorrectly
				throw new ArithmeticException();
			}
			//maxDmg + 1 because vanilla lets you use the tool one more time
			// when item damage == max damage (shows as Durability: 0 / max)
			currentEMC = Math.multiplyExact(currentEMC, Math.addExact(maxDamage - damage, 1)) / maxDamage;
		}
		return currentEMC;
	}
}