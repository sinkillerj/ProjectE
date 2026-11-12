package moze_intel.projecte.gameObjs;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public interface IMatterType {

	int getUses();

	float getSpeed();

	float getAttackDamageBonus();

	@NotNull
	TagKey<Block> getIncorrectBlocksForDrops();

	int getEnchantmentValue();

	@NotNull
	Ingredient getRepairIngredient();

	float getChargeModifier();

	int getMatterTier();
}
