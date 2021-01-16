package moze_intel.projecte.gameObjs.customRecipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.items.KleinStar.EnumKleinTier;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.Constants;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class FullKleinStarIngredient extends Ingredient {

	public static final IIngredientSerializer<FullKleinStarIngredient> SERIALIZER = new IIngredientSerializer<FullKleinStarIngredient>() {
		@Nonnull
		@Override
		public FullKleinStarIngredient parse(@Nonnull PacketBuffer buffer) {
			return new FullKleinStarIngredient(buffer.readEnumValue(EnumKleinTier.class));
		}

		@Nonnull
		@Override
		public FullKleinStarIngredient parse(@Nonnull JsonObject json) {
			int tier = JSONUtils.getInt(json, "tier");
			EnumKleinTier[] tiers = EnumKleinTier.values();
			if (tier < 0 || tier >= tiers.length) {
				throw new JsonParseException("Invalid klein star tier");
			}
			return new FullKleinStarIngredient(tiers[tier]);
		}

		@Override
		public void write(@Nonnull PacketBuffer buffer, @Nonnull FullKleinStarIngredient ingredient) {
			buffer.writeEnumValue(((KleinStar) ingredient.star.getItem()).tier);
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
		super(Stream.of(new Ingredient.SingleItemList(star)));
		this.star = star;
	}

	@Override
	public boolean test(@Nullable ItemStack input) {
		return input != null && !input.isEmpty() && ItemStack.areItemStacksEqual(input, star);
	}

	@Override
	public boolean isSimple()
	{
		return false;
	}

	@Nonnull
	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Nonnull
	@Override
	public JsonElement serialize() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(SERIALIZER).toString());
		json.addProperty("tier", ((KleinStar) star.getItem()).tier.ordinal());
		return json;
	}
}