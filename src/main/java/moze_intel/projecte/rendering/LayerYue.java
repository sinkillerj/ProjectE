package moze_intel.projecte.rendering;

import moze_intel.projecte.PECore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class LayerYue implements LayerRenderer<EntityPlayer> {
	private final RenderPlayer render;

	private static final UUID SIN_UUID = UUID.fromString("5f86012c-ca4b-451a-989c-8fab167af647");
	private static final UUID CLAR_UUID = UUID.fromString("e5c59746-9cf7-4940-a849-d09e1f1efc13");

	private static final ResourceLocation HEART_LOC = new ResourceLocation(PECore.MODID, "textures/models/heartcircle.png");
	private static final ResourceLocation YUE_LOC = new ResourceLocation(PECore.MODID, "textures/models/yuecircle.png");

	public LayerYue(RenderPlayer renderer)
	{
		this.render = renderer;
	}

	@Override
	public void doRenderLayer(@Nonnull EntityPlayer player, float angle1, float angle2, float partialTicks, float angle3, float angle4, float angle5, float angle8)
	{
		if (player.isInvisible())
		{
			return;
		}

		if(SIN_UUID.equals(player.getUniqueID())
				|| CLAR_UUID.equals(player.getUniqueID())
				|| PECore.DEV_ENVIRONMENT)
		{
			GlStateManager.pushMatrix();
			render.getMainModel().bipedBody.postRender(0.0625F);
			if (player.isSneaking())
			{
				GlStateManager.rotate(-28.64789F, 1.0F, 0.0F, 0.0F);
			}
			GlStateManager.rotate(180, 0, 0, 1);
			GlStateManager.scale(3.0f, 3.0f, 3.0f);
			GlStateManager.translate(-0.5f, -0.498f, -0.5f);
			GlStateManager.color(0.0F, 1.0F, 0.0F, 1.0F);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
			if (CLAR_UUID.equals(player.getUniqueID()))
			{
				Minecraft.getMinecraft().renderEngine.bindTexture(HEART_LOC);
			} else
			{
				Minecraft.getMinecraft().renderEngine.bindTexture(YUE_LOC);
			}

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder r = tess.getBuffer();
			r.begin(7, DefaultVertexFormats.POSITION_TEX);
			r.pos(0, 0, 0).tex(0, 0).endVertex();
			r.pos(0, 0, 1).tex(0, 1).endVertex();
			r.pos(1, 0, 1).tex(1, 1).endVertex();
			r.pos(1, 0, 0).tex(1, 0).endVertex();
			tess.draw();

			GlStateManager.enableLighting();
			GlStateManager.color(1F, 1F, 1F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
