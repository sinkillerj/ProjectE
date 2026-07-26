package moze_intel.projecte.emc.components.processor;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.DataComponentProcessor;
import moze_intel.projecte.api.components.IDataComponentProcessor;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@DataComponentProcessor(priority = Integer.MAX_VALUE)
public class DamageProcessor implements IDataComponentProcessor {

	@DataComponentProcessor.Instance
	public static final DamageProcessor INSTANCE = new DamageProcessor();

	@Override
	public String getName() {
		return PEConfigTranslations.DCP_DAMAGE.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.DCP_DAMAGE.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.DCP_DAMAGE.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC) throws ArithmeticException {
		ItemStack fakeStack = info.createStack();
		if (fakeStack.isDamaged()) {
			int maxDamage = fakeStack.getMaxDamage();
			int remainingDurability = maxDamage - fakeStack.getDamageValue();
			if (remainingDurability <= 0) {
				//Vanilla normally destroys an item at zero durability. Malformed or modded stacks may survive at or beyond that point;
				//fail closed rather than allowing invalid damage state to preserve the full base EMC value.
				return 0;
			} else if (remainingDurability < maxDamage) {
				currentEMC = multiplyDivide(currentEMC, remainingDurability, maxDamage);
			}
		}
		return currentEMC;
	}

	/**
	 * Calculates {@code value * multiplier / divisor} without overflowing when the final result fits in a long.
	 */
	private static long multiplyDivide(long value, int multiplier, int divisor) {
		long quotient = value / divisor;
		long remainder = value % divisor;
		return Math.addExact(
				Math.multiplyExact(quotient, multiplier),
				Math.multiplyExact(remainder, multiplier) / divisor
		);
	}
}
