package moze_intel.projecte.rendering;

import moze_intel.projecte.PECore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LayerModelYue implements LayerRenderer {
	private static final ModelYue yuemodel = new ModelYue();
	private final RenderPlayer render;

	private static final String SIN_UUID = "5f86012c-ca4b-451a-989c-8fab167af647";
	private static final String CLAR_UUID = "e5c59746-9cf7-4940-a849-d09e1f1efc13";

	public LayerModelYue(RenderPlayer renderer)
	{
		this.render = renderer;
	}

	@Override
	public void doRenderLayer(EntityLivingBase ent, float angle1, float angle2, float partialTicks, float angle3, float angle4, float angle5, float angle8)
	{
		if (ent instanceof EntityPlayer)
		{
			EntityPlayer player = ((EntityPlayer) ent);
			if(SIN_UUID.equals(player.getUniqueID().toString())
					|| CLAR_UUID.equals(player.getUniqueID().toString())
					|| PECore.DEV_ENVIRONMENT)
			{
				GlStateManager.pushMatrix();
				render.getMainModel().bipedBody.postRender(0.0625F);
				if (player.isSneaking())
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
				if (CLAR_UUID.equals(player.getUniqueID().toString()))
				{
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("projecte:textures/models/heartcircle.png"));
				} else
				{
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("projecte:textures/models/yuecircle.png"));
				}
				yuemodel.renderAll();
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
