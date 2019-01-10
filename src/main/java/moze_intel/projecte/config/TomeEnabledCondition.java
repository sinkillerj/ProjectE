package moze_intel.projecte.config;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionSerializer;

import java.util.function.BooleanSupplier;

public class TomeEnabledCondition implements IConditionSerializer {
    @Override
    public BooleanSupplier parse(JsonObject json) {
        return () -> ProjectEConfig.difficulty.craftableTome;
    }
}
