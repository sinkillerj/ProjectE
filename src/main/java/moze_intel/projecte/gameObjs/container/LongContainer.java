package moze_intel.projecte.gameObjs.container;

import net.minecraft.inventory.Container;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class LongContainer extends Container {
    @OnlyIn(Dist.CLIENT)
    public void updateProgressBarLong(int id, long data) {
    }
}
