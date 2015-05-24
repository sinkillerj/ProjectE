package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.rendering.ModelYue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerRender
{
	private static ModelYue yuemodel = new ModelYue();

	@SubscribeEvent
	public void playerRender(RenderPlayerEvent.Specials.Pre evt)
	{
		if(evt.entityPlayer.getUniqueID().toString().equals("5f86012c-ca4b-451a-989c-8fab167af647")
				|| PECore.DEV_ENVIRONMENT)
		{
			GlStateManager.pushMatrix();
			// evt.renderermodelBipedMain.bipedBody.postRender(0.0625f);
			evt.renderer.getPlayerModel().bipedBody.postRender(0.0625F);
			if (evt.entityPlayer.isSneaking())
			{
				GlStateManager.rotate(-28.64789F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translate(0.0f, -0.1f, 0.0f);
			}
			GlStateManager.rotate(180, 0, 0, 1);
			GlStateManager.scale(3.0f, 3.0f, 3.0f);
			GlStateManager.translate(-0.5f, -0.498f, -0.5f);
			GlStateManager.color(0.0F, 1.0F, 0.0F, 1.0F);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("projecte:textures/models/yuecircle.png"));
			yuemodel.renderAll();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}
}
