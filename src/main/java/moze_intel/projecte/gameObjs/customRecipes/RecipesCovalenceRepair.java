package moze_intel.projecte.gameObjs.customRecipes;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipesCovalenceRepair extends CustomRecipe {

	public static final RecipesCovalenceRepair INSTANCE = new RecipesCovalenceRepair();
	public static final MapCodec<RecipesCovalenceRepair> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, RecipesCovalenceRepair> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	public static final RecipeSerializer<RecipesCovalenceRepair> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	public RecipesCovalenceRepair() {
		super();
	}

	@Nullable
	private RepairTargetInfo findIngredients(CraftingInput inv) {
		List<ItemStack> dust = new ArrayList<>();
		ItemStack tool = ItemStack.EMPTY;
		for (ItemStack input : inv.items()) {
			if (!input.isEmpty()) {
				if (input.is(PETags.Items.COVALENCE_DUST)) {
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
		return new RepairTargetInfo(tool, dust.stream().mapToLong(IEMCProxy.INSTANCE::getValue).sum());
	}

	@Override
	public boolean matches(@NotNull CraftingInput inv, @NotNull Level level) {
		RepairTargetInfo targetInfo = findIngredients(inv);
		return targetInfo != null && targetInfo.emcPerDurability <= targetInfo.dustEmc;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingInput inv) {
		RepairTargetInfo targetInfo = findIngredients(inv);
		if (targetInfo == null) {
			//If there isn't actually a match return no result
			return ItemStack.EMPTY;
		}
		ItemStack output = targetInfo.tool.copy();
		output.setDamageValue((int) Math.max(output.getDamageValue() - targetInfo.dustEmc / targetInfo.emcPerDurability, 0));
		return output;
	}

	@NotNull
	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return SERIALIZER;
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
