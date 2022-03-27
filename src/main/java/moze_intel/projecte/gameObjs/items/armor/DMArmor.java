package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.PECore;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class DMArmor extends PEArmor {

	public DMArmor(EquipmentSlot armorPiece, Properties props) {
		super(DMArmorMaterial.INSTANCE, armorPiece, props);
	}

	@Override
	public float getFullSetBaseReduction() {
		return 0.8F;
	}

	@Override
	public float getMaxDamageAbsorb(EquipmentSlot slot, DamageSource source) {
		if (source.isExplosion()) {
			return 350;
		}
		if (slot == EquipmentSlot.FEET && source == DamageSource.FALL) {
			return 5 / getPieceEffectiveness(slot);
		} else if (slot == EquipmentSlot.HEAD && source == DamageSource.DROWN) {
			return 5 / getPieceEffectiveness(slot);
		}
		if (source.isBypassArmor()) {
			return 0;
		}
		//If the source is not unblockable, allow our piece to block a certain amount of damage
		if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.FEET) {
			return 100;
		}
		return 150;
	}

	private static class DMArmorMaterial implements ArmorMaterial {

		private static final DMArmorMaterial INSTANCE = new DMArmorMaterial();

		@Override
		public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {
			return 0;
		}

		@Override
		public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
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

		@NotNull
		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ARMOR_EQUIP_DIAMOND;
		}

		@NotNull
		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.EMPTY;
		}

		@NotNull
		@Override
		public String getName() {
			//Only used on the client
			return PECore.rl("dark_matter").toString();
		}

		@Override
		public float getToughness() {
			return 2;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.1F;
		}
	}
}