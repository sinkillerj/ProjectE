package moze_intel.projecte.gameObjs.items.armor;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public abstract class GemArmorBase extends PEArmor {

	public GemArmorBase(EquipmentSlot armorType, Properties props) {
		super(GemArmorMaterial.INSTANCE, armorType, props);
	}

	@Override
	public float getFullSetBaseReduction() {
		return 0.9F;
	}

	@Override
	public float getMaxDamageAbsorb(EquipmentSlot slot, DamageSource source) {
		if (source.isExplosion()) {
			return 750;
		}
		if (slot == EquipmentSlot.FEET && source == DamageSource.FALL) {
			return 15 / getPieceEffectiveness(slot);
		} else if (slot == EquipmentSlot.HEAD && source == DamageSource.DROWN) {
			return 15 / getPieceEffectiveness(slot);
		}
		if (source.isBypassArmor()) {
			return 0;
		}
		//If the source is not unblockable, allow our piece to block a certain amount of damage
		if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.FEET) {
			return 400;
		}
		return 500;
	}

	public static boolean hasAnyPiece(Player player) {
		return player.getInventory().armor.stream().anyMatch(i -> !i.isEmpty() && i.getItem() instanceof GemArmorBase);
	}

	public static boolean hasFullSet(Player player) {
		return player.getInventory().armor.stream().noneMatch(i -> i.isEmpty() || !(i.getItem() instanceof GemArmorBase));
	}

	private static class GemArmorMaterial implements ArmorMaterial {

		private static final GemArmorMaterial INSTANCE = new GemArmorMaterial();

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
			return PECore.rl("gem_armor").toString();
		}

		@Override
		public float getToughness() {
			return 2;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.25F;
		}
	}
}