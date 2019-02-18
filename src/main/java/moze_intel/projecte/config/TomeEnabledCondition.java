package moze_intel.projecte.config;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionSerializer;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class TomeEnabledCondition implements IConditionSerializer {
    @Nonnull
    @Override
    public BooleanSupplier parse(@Nonnull JsonObject json) {
        return ProjectEConfig.difficulty.craftableTome::get;
    }
}
