package moze_intel.projecte.rendering;

import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.rendering.model.ModelPedestal;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class PedestalRenderer extends TileEntitySpecialRenderer
{

	private final ModelPedestal model = new ModelPedestal();
	private RenderItem ghostItemRenderer;

	public PedestalRenderer()
	{
		ghostItemRenderer = new RenderItem() {
			@Override
			public boolean shouldBob()
			{
				return false;
			}
		};
		ghostItemRenderer.setRenderManager(RenderManager.instance);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f1)
	{
		if (te instanceof DMPedestalTile)
		{
			DMPedestalTile pedestal = ((DMPedestalTile) te);

			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glPushMatrix();
			bindTexture(Constants.PEDESTAL_MODELTEX_LOCATION);
			GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
			if (pedestal.getItemStack() != null)
			{
				EntityItem hover = new EntityItem(pedestal.getWorldObj());
				hover.hoverStart = 0.0F;
				hover.setEntityItemStack(pedestal.getItemStack());
				ghostItemRenderer.doRender(hover, 0, 0, 0, 0, 0);
			}

			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
	}
}
