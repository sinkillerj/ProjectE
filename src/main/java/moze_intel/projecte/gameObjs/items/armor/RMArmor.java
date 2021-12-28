package moze_intel.projecte.gameObjs.items.armor;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class RMArmor extends PEArmor {

	public RMArmor(EquipmentSlot armorType, Properties props) {
		super(RMArmorMaterial.INSTANCE, armorType, props);
	}

	@Override
	public float getFullSetBaseReduction() {
		return 0.9F;
	}

	@Override
	public float getMaxDamageAbsorb(EquipmentSlot slot, DamageSource source) {
		if (source.isExplosion()) {
			return 500;
		}
		if (slot == EquipmentSlot.FEET && source == DamageSource.FALL) {
			return 10 / getPieceEffectiveness(slot);
		} else if (slot == EquipmentSlot.HEAD && source == DamageSource.DROWN) {
			return 10 / getPieceEffectiveness(slot);
		}
		if (source.isBypassArmor()) {
			return 0;
		}
		//If the source is not unblockable, allow our piece to block a certain amount of damage
		if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.FEET) {
			return 250;
		}
		return 350;
	}

	private static class RMArmorMaterial implements ArmorMaterial {

		private static final RMArmorMaterial INSTANCE = new RMArmorMaterial();

		@Override
		public int getDurabilityForSlot(@Nonnull EquipmentSlot slot) {
			return 0;
		}

		@Override
		public int getDefenseForSlot(@Nonnull EquipmentSlot slot) {
			if (slot == EquipmentSlot.FEET) {
				return 3;
			} else if (slot == EquipmentSlot.LEGS) {
				return 6;
			} else if (slot == EquipmentSlot.CHEST) {
				return 8;
			} else if (slot == EquipmentSlot.HEAD) {
				return 3;
			}
			return 0;
		}

		@Override
		public int getEnchantmentValue() {
			return 0;
		}

		@Nonnull
		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ARMOR_EQUIP_DIAMOND;
		}

		@Nonnull
		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.EMPTY;
		}

		@Nonnull
		@Override
		public String getName() {
			//Only used on the client
			return PECore.rl("red_matter").toString();
		}

		@Override
		public float getToughness() {
			return 2;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.2F;
		}
	}
}