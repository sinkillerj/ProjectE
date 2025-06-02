package moze_intel.projecte.api.block_entity;

import org.jetbrains.annotations.NotNull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IRelay {
    /**
     * @return The bonus emc to add
     */
    double getBonusToAdd();

    /**
     * Add the collector bonus
     *
     * @param level The level the Relay is in. (Used for saving, pass in the values that you get passed in)
     * @param pos   The position the Relay is at. (Used for saving, pass in the values that you get passed in)
     */
    void addBonus(@NotNull Level level, @NotNull BlockPos pos);
}