package moze_intel.projecte.rendering;

import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.rendering.model.ModelPedestal;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class PedestalRenderer extends TileEntitySpecialRenderer
{
	private final ModelPedestal model = new ModelPedestal();

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f1, int par9)
	{
		if (te instanceof DMPedestalTile)
		{
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			bindTexture(Constants.PEDESTAL_MODELTEX_LOCATION);
			GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5); // Feel free to improve this, anyone.
			GlStateManager.rotate(180.0F, 0, 0, 0);
			model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
		}
	}
}
