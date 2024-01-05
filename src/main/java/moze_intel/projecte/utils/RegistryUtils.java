package moze_intel.projecte.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RegistryUtils {

    private RegistryUtils() {
    }

    public static Holder<BlockEntityType<?>> getBEHolder(BlockEntityType<?> type) {
        Holder<BlockEntityType<?>> holder = type.builtInRegistryHolder();
        //I don't believe this can ever be null, but just in case the nullability annotation is valid... handle it
        if (holder == null) {
            return BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(type);
        }
        return holder;
    }
}