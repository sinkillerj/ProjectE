package moze_intel.projecte.gameObjs.items.armor;

import javax.annotation.Nonnull;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

//TODO: When/If Thaumcraft gets ported add back in the abilities of the goggles of revealing
public class RMArmor extends PEArmor {

	public RMArmor(EquipmentSlotType armorType, Properties props) {
		super(RMArmorMaterial.INSTANCE, armorType, props);
	}

	@Override
	protected String getNameForLocation() {
		return "redmatter";
	}

	@Override
	public float getFullSetBaseReduction() {
		return 0.9F;
	}

	@Override
	public float getMaxDamageAbsorb(EquipmentSlotType slot, DamageSource source) {
		if (source.isExplosion()) {
			return 500;
		}
		if (slot == EquipmentSlotType.FEET && source == DamageSource.FALL) {
			return 10 / getPieceEffectiveness(slot);
		}
		if (source.isUnblockable()) {
			return 0;
		}
		//If the source is not unblockable, allow our piece to block a certain amount of damage
		if (slot == EquipmentSlotType.HEAD || slot == EquipmentSlotType.FEET) {
			return 250;
		}
		return 350;
	}

	private static class RMArmorMaterial implements IArmorMaterial {

		private static final RMArmorMaterial INSTANCE = new RMArmorMaterial();

		@Override
		public int getDurability(@Nonnull EquipmentSlotType slot) {
			return 0;
		}

		@Override
		public int getDamageReductionAmount(@Nonnull EquipmentSlotType slot) {
			if (slot == EquipmentSlotType.FEET) {
				return 3;
			} else if (slot == EquipmentSlotType.LEGS) {
				return 6;
			} else if (slot == EquipmentSlotType.CHEST) {
				return 8;
			} else if (slot == EquipmentSlotType.HEAD) {
				return 3;
			}
			return 0;
		}

		@Override
		public int getEnchantability() {
			return 0;
		}

		@Nonnull
		@Override
		public SoundEvent getSoundEvent() {
			return SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
		}

		@Nonnull
		@Override
		public Ingredient getRepairMaterial() {
			return Ingredient.EMPTY;
		}

		@Nonnull
		@Override
		public String getName() {
			//Only used on the client
			return "red_matter";
		}

		@Override
		public float getToughness() {
			return 2;
		}
	}
}