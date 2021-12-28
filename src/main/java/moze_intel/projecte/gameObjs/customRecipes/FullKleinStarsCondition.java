package moze_intel.projecte.gameObjs.customRecipes;

import com.google.gson.JsonObject;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class FullKleinStarsCondition implements ICondition {

	public static final FullKleinStarsCondition INSTANCE = new FullKleinStarsCondition();
	private static final ResourceLocation ID = PECore.rl("full_klein_stars");

	private FullKleinStarsCondition() {
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test() {
		return ProjectEConfig.common.fullKleinStars.get();
	}

	public static final IConditionSerializer<FullKleinStarsCondition> SERIALIZER = new IConditionSerializer<>() {
		@Override
		public void write(JsonObject json, FullKleinStarsCondition value) {
		}

		@Override
		public FullKleinStarsCondition read(JsonObject json) {
			return INSTANCE;
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}
	};
}