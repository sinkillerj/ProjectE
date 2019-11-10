package moze_intel.projecte.config;

import com.google.gson.JsonObject;
import moze_intel.projecte.PECore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class TomeEnabledCondition implements ICondition {

	private static final ResourceLocation ID = new ResourceLocation(PECore.MODID, "tome_enabled");

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test() {
		return ProjectEConfig.server.difficulty.craftableTome.get();
	}

	public static final IConditionSerializer<TomeEnabledCondition> SERIALIZER = new IConditionSerializer<TomeEnabledCondition>() {
		@Override
		public void write(JsonObject json, TomeEnabledCondition value) {
		}

		@Override
		public TomeEnabledCondition read(JsonObject json) {
			return new TomeEnabledCondition();
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}
	};
}