package moze_intel.projecte.gameObjs.container;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public abstract class LongContainer extends Container {
    protected LongContainer(ContainerType<?> type, int id) {
        super(type, id);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateProgressBarLong(int id, long data) {
    }
}
