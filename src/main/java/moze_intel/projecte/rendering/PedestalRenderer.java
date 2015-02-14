package moze_intel.projecte.rendering;

import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class PedestalRenderer extends TileEntitySpecialRenderer
{

	private final ModelPedestal model = new ModelPedestal();

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f1)
	{
		if (te instanceof DMPedestalTile)
		{
			DMPedestalTile pedestal = ((DMPedestalTile) te);

			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glPushMatrix();
			bindTexture(Constants.PEDESTAL_MODELTEX_LOCATION);
			GL11.glTranslated(x, y, z);
			model.render();
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			if (pedestal.getItemStack() != null)
			{
				//EntityItem hover = new EntityItem(pedestal.getWorldObj());
				//hover.setEntityItemStack(pedestal.getItemStack());
				//RenderItem.getInstance().doRender(hover, 0, 0, 0, 0, 0);
			}

			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
	}
}
