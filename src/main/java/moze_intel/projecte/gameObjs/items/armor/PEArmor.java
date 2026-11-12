package moze_intel.projecte.gameObjs.items.armor;

import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PEArmor extends Item {

	private final ArmorType armorType;

	protected PEArmor(ArmorType armorType, Properties props) {
		super(props);
		this.armorType = armorType;
	}

	protected ArmorType getArmorType() {
		return armorType;
	}

	public boolean isEnchantable(@NotNull ItemStack stack) {
		return false;
	}

	public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
		return false;
	}

	@Override
	public boolean isPrimaryItemFor(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
		return false;
	}

	@Override
	public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
		return false;
	}

	@Override
	public <T extends LivingEntity> int damageItem(@NotNull ItemStack stack, int amount, T entity, @NotNull Consumer<ItemStack> onBroken) {
		return 0;
	}

	/**
	 * Minimum percent damage will be reduced to if the full set is worn
	 */
	public abstract float getFullSetBaseReduction();

	/**
	 * Gets the max damage that a piece of this armor in a given slot can absorb of a specific type.
	 *
	 * @apiNote A value of zero means that there is no special bonus blocking powers for that damage type, and the piece's base reduction will be get used instead by the
	 * damage calculation event.
	 */
	public abstract float getMaxDamageAbsorb(ArmorType type, DamageSource source);

	public ReductionInfo getReductionInfo(DamageSource source) {
		float maxDamageAbsorb = getMaxDamageAbsorb(armorType, source);
		float fullSetReduction = getFullSetBaseReduction();
		if (maxDamageAbsorb > 0 && fullSetReduction > 0) {
			float pieceEffectiveness = getPieceEffectiveness(armorType);
			return new ReductionInfo(fullSetReduction * pieceEffectiveness, maxDamageAbsorb * pieceEffectiveness);
		}
		return ReductionInfo.ZERO;
	}

	/**
	 * Gets the overall effectiveness of a given slots piece.
	 */
	public float getPieceEffectiveness(ArmorType type) {
		if (type == ArmorType.BOOTS || type == ArmorType.HELMET) {
			return 0.2F;
		} else if (type == ArmorType.CHESTPLATE || type == ArmorType.LEGGINGS) {
			return 0.3F;
		}
		return 0;
	}

	protected static boolean isArmorSlot(@Nullable EquipmentSlot slot) {
		return slot != null && slot.isArmor();
	}

	public record ReductionInfo(float percentReduced, float maxDamagedAbsorbed) {

		public static final ReductionInfo ZERO = new ReductionInfo(0, 0);

		public ReductionInfo add(ReductionInfo other) {
			if (this == ZERO) {
				return other;
			} else if (other == ZERO) {
				return this;
			}
			return new ReductionInfo(percentReduced + other.percentReduced, maxDamagedAbsorbed + other.maxDamagedAbsorbed);
		}
	}
}
