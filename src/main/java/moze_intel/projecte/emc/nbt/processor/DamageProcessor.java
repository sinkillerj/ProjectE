package moze_intel.projecte.emc.nbt.processor;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.nbt.INBTProcessor;
import moze_intel.projecte.api.nbt.NBTProcessor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@NBTProcessor(priority = Integer.MAX_VALUE)
public class DamageProcessor implements INBTProcessor {

	@Override
	public String getName() {
		return "DamageProcessor";
	}

	@Override
	public String getDescription() {
		return "Reduces the EMC value the more damaged an item is.";
	}

	@Override
	public long recalculateEMC(@Nonnull ItemInfo info, long currentEMC) throws ArithmeticException {
		Item item = info.getItem();
		if (item.canBeDepleted()) {
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