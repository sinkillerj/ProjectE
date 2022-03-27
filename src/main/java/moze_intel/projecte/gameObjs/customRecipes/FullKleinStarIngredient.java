package moze_intel.projecte.gameObjs.customRecipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.stream.Stream;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.items.KleinStar.EnumKleinTier;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//Removed in favor of just using partial NBT ingredients
@Deprecated(forRemoval = true)
public class FullKleinStarIngredient extends Ingredient {

	public static final IIngredientSerializer<FullKleinStarIngredient> SERIALIZER = new IIngredientSerializer<>() {
		@NotNull
		@Override
		public FullKleinStarIngredient parse(@NotNull FriendlyByteBuf buffer) {
			return new FullKleinStarIngredient(buffer.readEnum(EnumKleinTier.class));
		}

		@NotNull
		@Override
		public FullKleinStarIngredient parse(@NotNull JsonObject json) {
			int tier = GsonHelper.getAsInt(json, "tier");
			EnumKleinTier[] tiers = EnumKleinTier.values();
			if (tier < 0 || tier >= tiers.length) {
				throw new JsonParseException("Invalid klein star tier");
			}
			return new FullKleinStarIngredient(tiers[tier]);
		}

		@Override
		public void write(@NotNull FriendlyByteBuf buffer, @NotNull FullKleinStarIngredient ingredient) {
			buffer.writeEnum(((KleinStar) ingredient.star.getItem()).tier);
		}
	};

	private static ItemStack getFullStar(EnumKleinTier tier) {
		ItemStack star = new ItemStack(PEItems.getStar(tier));
		ItemPE.setEmc(star, Constants.MAX_KLEIN_EMC[tier.ordinal()]);
		return star;
	}

	private final ItemStack star;

	public FullKleinStarIngredient(EnumKleinTier tier) {
		this(getFullStar(tier));
	}

	private FullKleinStarIngredient(ItemStack star) {
		super(Stream.of(new Ingredient.ItemValue(star)));
		this.star = star;
	}

	@Override
	public boolean test(@Nullable ItemStack input) {
		return input != null && !input.isEmpty() && ItemStack.matches(input, star);
	}

	@Override
	public boolean isSimple()
	{
		return false;
	}

	@NotNull
	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@NotNull
	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(SERIALIZER).toString());
		json.addProperty("tier", ((KleinStar) star.getItem()).tier.ordinal());
		return json;
	}
}