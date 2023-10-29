package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.PECore;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class RMArmor extends PEArmor {

	public RMArmor(ArmorItem.Type armorType, Properties props) {
		super(RMArmorMaterial.INSTANCE, armorType, props);
	}

	@Override
	public float getFullSetBaseReduction() {
		return 0.9F;
	}

	@Override
	public float getMaxDamageAbsorb(ArmorItem.Type type, DamageSource source) {
		if (source.is(DamageTypeTags.IS_EXPLOSION)) {
			return 500;
		}
		if (type == ArmorItem.Type.BOOTS && source.is(DamageTypeTags.IS_FALL)) {
			return 10 / getPieceEffectiveness(type);
		} else if (type == ArmorItem.Type.HELMET && source.is(DamageTypeTags.IS_DROWNING)) {
			return 10 / getPieceEffectiveness(type);
		}
		if (source.is(DamageTypeTags.BYPASSES_ARMOR)) {
			return 0;
		}
		//If the source is not unblockable, allow our piece to block a certain amount of damage
		if (type == ArmorItem.Type.HELMET || type == ArmorItem.Type.BOOTS) {
			return 250;
		}
		return 350;
	}

	private static class RMArmorMaterial implements ArmorMaterial {

		private static final RMArmorMaterial INSTANCE = new RMArmorMaterial();

		@Override
		public int getDurabilityForType(@NotNull ArmorItem.Type type) {
			return 0;
		}

		@Override
		public int getDefenseForType(@NotNull ArmorItem.Type type) {
			return switch (type) {
				case BOOTS -> 3;
				case LEGGINGS -> 6;
				case CHESTPLATE -> 8;
				case HELMET -> 3;
			};
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