package moze_intel.projecte.gameObjs.customRecipes;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipesCovalenceRepair extends SpecialRecipe {

	public RecipesCovalenceRepair(ResourceLocation id) {
		super(id);
	}

	@Nullable
	private RepairTargetInfo findIngredients(CraftingInventory inv) {
		List<ItemStack> dust = new ArrayList<>();
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack input = inv.getStackInSlot(i);
			if (!input.isEmpty()) {
				if (input.getItem().isIn(PETags.Items.COVALENCE_DUST)) {
					dust.add(input);
				} else if (tool.isEmpty() && ItemHelper.isRepairableDamagedItem(input)) {
					tool = input;
				} else {//Invalid item
					return null;
				}
			}
		}
		if (tool.isEmpty() || dust.isEmpty()) {
			//If there is no tool, or no dusts where found, return that we don't have any matching ingredients
			return null;
		}
		return new RepairTargetInfo(tool, dust.stream().mapToLong(EMCHelper::getEmcValue).sum());
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
		RepairTargetInfo targetInfo = findIngredients(inv);
		return targetInfo != null && targetInfo.emcPerDurability <= targetInfo.dustEmc;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
		RepairTargetInfo targetInfo = findIngredients(inv);
		if (targetInfo == null) {
			//If there isn't actually a match return no result
			return ItemStack.EMPTY;
		}
		ItemStack output = targetInfo.tool.copy();
		output.setDamage((int) Math.max(output.getDamage() - targetInfo.dustEmc / targetInfo.emcPerDurability, 0));
		return output;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width > 1 || height > 1;
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return PERecipeSerializers.COVALENCE_REPAIR.get();
	}

	private static class RepairTargetInfo {

		private final ItemStack tool;
		private final long emcPerDurability;
		private final long dustEmc;

		public RepairTargetInfo(ItemStack tool, long dustEmc) {
			this.tool = tool;
			this.dustEmc = dustEmc;
			this.emcPerDurability = EMCHelper.getEMCPerDurability(tool);
		}
	}
}