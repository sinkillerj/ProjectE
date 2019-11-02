package moze_intel.projecte.gameObjs;

import javax.annotation.Nonnull;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IStringSerializable;

public enum EnumMatterType implements IStringSerializable, IItemTier {
	DARK_MATTER("dark_matter", 3, 14, 12, 4),
	RED_MATTER("red_matter", 4, 16, 14, 5);

	private final String name;
	private final float attackDamage;
	private final float efficiency;
	private final float chargeModifier;
	private final int harvestLevel;

	EnumMatterType(String name, float attackDamage, float efficiency, float chargeModifier, int harvestLevel) {
		this.name = name;
		this.attackDamage = attackDamage;
		this.efficiency = efficiency;
		this.chargeModifier = chargeModifier;
		this.harvestLevel = harvestLevel;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int getMaxUses() {
		return 0;
	}

	public float getChargeModifier() {
		return chargeModifier;
	}

	@Override
	public float getEfficiency() {
		return efficiency;
	}

	@Override
	public float getAttackDamage() {
		//TODO
		//Dark matter sword 9
		//Red matter sword 12
		return attackDamage;
	}

	@Override
	public int getHarvestLevel() {
		return harvestLevel;
	}

	@Override
	public int getEnchantability() {
		return 0;
	}

	@Nonnull
	@Override
	public Ingredient getRepairMaterial() {
		return Ingredient.EMPTY;
	}
}