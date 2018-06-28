package moze_intel.projecte.gameObjs.container;

import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class LongContainer extends Container {
    @SideOnly(Side.CLIENT)
    public void updateProgressBarLong(int id, long data) {
    }
}
