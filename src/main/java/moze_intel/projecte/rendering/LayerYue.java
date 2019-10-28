package moze_intel.projecte.rendering;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.UUID;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class LayerYue extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

	private final PlayerRenderer render;

	private static final UUID SIN_UUID = UUID.fromString("5f86012c-ca4b-451a-989c-8fab167af647");
	private static final UUID CLAR_UUID = UUID.fromString("e5c59746-9cf7-4940-a849-d09e1f1efc13");

	private static final ResourceLocation HEART_LOC = new ResourceLocation(PECore.MODID, "textures/models/heartcircle.png");
	private static final ResourceLocation YUE_LOC = new ResourceLocation(PECore.MODID, "textures/models/yuecircle.png");

	public LayerYue(PlayerRenderer renderer) {
		super(renderer);
		this.render = renderer;
	}

	@Override
	public void render(@Nonnull AbstractClientPlayerEntity player, float angle1, float angle2, float partialTicks, float angle3, float angle4, float angle5, float angle8) {
		if (player.isInvisible()) {
			return;
		}

		if (SIN_UUID.equals(player.getUniqueID()) || CLAR_UUID.equals(player.getUniqueID()) || PECore.DEV_ENVIRONMENT) {
			GlStateManager.pushMatrix();
			render.getEntityModel().bipedBodyWear.postRender(0.0625F);
			if (player.isSneaking()) {
				GlStateManager.rotatef(-28.64789F, 1.0F, 0.0F, 0.0F);
			}
			GlStateManager.rotatef(180, 0, 0, 1);
			GlStateManager.scalef(3.0f, 3.0f, 3.0f);
			GlStateManager.translatef(-0.5f, -0.498f, -0.5f);
			GlStateManager.color4f(0.0F, 1.0F, 0.0F, 1.0F);
			GlStateManager.disableLighting();
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240f, 240f);
			if (CLAR_UUID.equals(player.getUniqueID())) {
				Minecraft.getInstance().textureManager.bindTexture(HEART_LOC);
			} else {
				Minecraft.getInstance().textureManager.bindTexture(YUE_LOC);
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
			GlStateManager.color3f(1F, 1F, 1F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}