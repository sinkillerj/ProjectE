package moze_intel.projecte.rendering;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Vincent on 2/11/2015.
 */
@SideOnly(Side.CLIENT)
public class PedestalRenderer extends TileEntitySpecialRenderer {

    private final ModelPedestal model = new ModelPedestal();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f1) {

    }
}
