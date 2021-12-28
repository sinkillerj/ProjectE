package moze_intel.projecte.gameObjs.customRecipes;

import com.google.gson.JsonObject;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class TomeEnabledCondition implements ICondition {

	public static final TomeEnabledCondition INSTANCE = new TomeEnabledCondition();
	private static final ResourceLocation ID = PECore.rl("tome_enabled");

	private TomeEnabledCondition() {
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test() {
		return ProjectEConfig.common.craftableTome.get();
	}

	public static final IConditionSerializer<TomeEnabledCondition> SERIALIZER = new IConditionSerializer<>() {
		@Override
		public void write(JsonObject json, TomeEnabledCondition value) {
		}

		@Override
		public TomeEnabledCondition read(JsonObject json) {
			return INSTANCE;
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}
	};
}