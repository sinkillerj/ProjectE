package moze_intel.projecte.gameObjs.items.armor;

import javax.annotation.Nonnull;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO: When/If Thaumcraft gets ported add back in the abilities of the goggles of revealing
public class RMArmor extends PEArmor {

	public RMArmor(EquipmentSlotType armorType, Properties props) {
		super(RMArmorMaterial.INSTANCE, armorType, props);
	}

	@Override
	protected String getNameForLocation() {
		return "redmatter";
	}

	private static class RMArmorMaterial implements IArmorMaterial {

		private static final RMArmorMaterial INSTANCE = new RMArmorMaterial();

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
		@OnlyIn(Dist.CLIENT)
		public String getName() {
			return "red_matter";
		}

		@Override
		public float getToughness() {
			//TODO: 1.14, go through and fix damage values better. These were taken from the 1.12 shown stats on the item
			return 2;
		}
	}
}