package moze_intel.projecte.gameObjs.items.armor;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.equipment.ArmorType;

public class DMArmor extends PEArmor {

	public DMArmor(ArmorType armorPiece, Properties props) {
		super(armorPiece, props);
	}

	@Override
	public float getFullSetBaseReduction() {
		return 0.8F;
	}

	@Override
	public float getMaxDamageAbsorb(ArmorType type, DamageSource source) {
		if (source.is(DamageTypeTags.IS_EXPLOSION)) {
			return 350;
		}
		if (type == ArmorType.BOOTS && source.is(DamageTypeTags.IS_FALL)) {
			return 5 / getPieceEffectiveness(type);
		} else if (type == ArmorType.HELMET && source.is(DamageTypeTags.IS_DROWNING)) {
			return 5 / getPieceEffectiveness(type);
		}
		if (source.is(DamageTypeTags.BYPASSES_ARMOR)) {
			return 0;
		}
		//If the source is not unblockable, allow our piece to block a certain amount of damage
		if (type == ArmorType.HELMET || type == ArmorType.BOOTS) {
			return 100;
		}
		return 150;
	}
}
