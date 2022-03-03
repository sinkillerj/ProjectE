package moze_intel.projecte.gameObjs;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.TierSortingRegistry;

public enum EnumMatterType implements StringRepresentable, Tier {
	DARK_MATTER("dark_matter", 3, 14, 12, 4, PETags.Blocks.NEEDS_DARK_MATTER_TOOL, Tiers.NETHERITE, PECore.rl("red_matter")),
	RED_MATTER("red_matter", 4, 16, 14, 5, PETags.Blocks.NEEDS_RED_MATTER_TOOL, DARK_MATTER, null);

	private final String name;
	private final float attackDamage;
	private final float efficiency;
	private final float chargeModifier;
	private final int harvestLevel;
	private final TagKey<Block> neededTag;

	EnumMatterType(String name, float attackDamage, float efficiency, float chargeModifier, int harvestLevel, TagKey<Block> neededTag, Tier previous,
			@Nullable ResourceLocation next) {
		this.name = name;
		this.attackDamage = attackDamage;
		this.efficiency = efficiency;
		this.chargeModifier = chargeModifier;
		this.harvestLevel = harvestLevel;
		this.neededTag = neededTag;
		TierSortingRegistry.registerTier(this, PECore.rl(name), List.of(previous), next == null ? Collections.emptyList() : List.of(next));
	}

	@Nonnull
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

	@Override
	public int getLevel() {
		return harvestLevel;
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Nonnull
	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.EMPTY;
	}

	public int getMatterTier() {
		return ordinal();
	}

	@Nonnull
	@Override
	public TagKey<Block> getTag() {
		return neededTag;
	}
}