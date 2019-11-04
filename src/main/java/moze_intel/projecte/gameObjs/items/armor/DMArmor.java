package moze_intel.projecte.gameObjs.items.armor;

import javax.annotation.Nonnull;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class DMArmor extends PEArmor {

	public DMArmor(EquipmentSlotType armorPiece, Properties props) {
		super(DMArmorMaterial.INSTANCE, armorPiece, props);
	}

	@Override
	protected String getNameForLocation() {
		return "darkmatter";
	}

	private static class DMArmorMaterial implements IArmorMaterial {

		private static final DMArmorMaterial INSTANCE = new DMArmorMaterial();

		@Override
		public int getDurability(@Nonnull EquipmentSlotType slot) {
			return 0;
		}

		@Override
		public int getDamageReductionAmount(@Nonnull EquipmentSlotType slot) {
			//TODO: 1.14, go through and fix damage values better. These were taken from the 1.12 shown stats on the item
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
			return "dark_matter";
		}

		@Override
		public float getToughness() {
			//TODO: 1.14, go through and fix damage values better. These were taken from the 1.12 shown stats on the item
			return 2;
		}
	}
}