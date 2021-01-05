package moze_intel.projecte.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class ConditionWrappedRecipeResult implements IFinishedRecipe {

	private final IFinishedRecipe internal;
	private final ICondition[] conditions;

	public ConditionWrappedRecipeResult(IFinishedRecipe internal, ICondition... conditions) {
		this.internal = internal;
		this.conditions = conditions;
	}

	@Override
	public void serialize(@Nonnull JsonObject json) {
		//Add our conditions to the serializer
		if (conditions.length > 0) {
			JsonArray conditionsArray = new JsonArray();
			for (ICondition condition : conditions) {
				conditionsArray.add(CraftingHelper.serialize(condition));
			}
			json.add("conditions", conditionsArray);
		}
		internal.serialize(json);
	}

	@Nonnull
	@Override
	public ResourceLocation getID() {
		return internal.getID();
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return internal.getSerializer();
	}

	@Nullable
	@Override
	public JsonObject getAdvancementJson() {
		return internal.getAdvancementJson();
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementID() {
		return internal.getAdvancementID();
	}
}