package moze_intel.projecte.gameObjs;

import com.mojang.serialization.Codec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public enum EnumMatterType implements StringRepresentable, IMatterType {
	DARK_MATTER("dark_matter", 3, 14, 12, PETags.Blocks.INCORRECT_FOR_DARK_MATTER_TOOL, MapColor.COLOR_BLACK),
	RED_MATTER("red_matter", 4, 16, 14, PETags.Blocks.INCORRECT_FOR_RED_MATTER_TOOL, MapColor.COLOR_RED);

	public static final Codec<EnumMatterType> CODEC = StringRepresentable.fromEnum(EnumMatterType::values);

	private final TagKey<Block> incorrectBlockForDrops;
	private final String name;
	private final float attackDamage;
	private final float efficiency;
	private final float chargeModifier;
	private final MapColor mapColor;

	EnumMatterType(String name, float attackDamage, float efficiency, float chargeModifier, TagKey<Block> incorrectBlockForDrops, MapColor mapColor) {
		this.name = name;
		this.attackDamage = attackDamage;
		this.efficiency = efficiency;
		this.chargeModifier = chargeModifier;
		this.incorrectBlockForDrops = incorrectBlockForDrops;
		this.mapColor = mapColor;
	}

	@NotNull
	@Override
	public String getSerializedName() {
		return name;
	}

	@Override
	public String toString() {
		return getSerializedName();
	}

	@Override
	public int getUses() {
		return 0;
	}

	@Override
	public float getChargeModifier() {
		return chargeModifier;
	}

	@Override
	public float getSpeed() {
		return efficiency;
	}

	@Override
	public float getAttackDamageBonus() {
		return attackDamage;
	}

	@NotNull
	@Override
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return incorrectBlockForDrops;
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}

	@NotNull
	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.of();
	}

	public MapColor getMapColor() {
		return mapColor;
	}

	@Override
	public int getMatterTier() {
		return ordinal();
	}
}
