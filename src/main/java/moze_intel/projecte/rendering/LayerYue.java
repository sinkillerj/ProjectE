package moze_intel.projecte.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.UUID;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL13;

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

	//TODO: 1.15, I am not sure the angle params are right
	@Override
	public void func_225628_a_(@Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, @Nonnull AbstractClientPlayerEntity player,
			float angle1, float angle2, float partialTick, float angle3, float angle4, float angle5) {
		if (player.isInvisible()) {
			return;
		}

		if (SIN_UUID.equals(player.getUniqueID()) || CLAR_UUID.equals(player.getUniqueID()) || PECore.DEV_ENVIRONMENT) {
			RenderSystem.pushMatrix();
			//TODO: 1.15 FIXME
			//render.getEntityModel().bipedBodyWear.postRender(0.0625F);
			if (player.func_225608_bj_()) {
				RenderSystem.rotatef(-28.64789F, 1.0F, 0.0F, 0.0F);
			}
			RenderSystem.rotatef(180, 0, 0, 1);
			RenderSystem.scalef(3.0f, 3.0f, 3.0f);
			RenderSystem.translatef(-0.5f, -0.498f, -0.5f);
			RenderSystem.color4f(0.0F, 1.0F, 0.0F, 1.0F);
			RenderSystem.disableLighting();
			RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, 240f, 240f);
			if (CLAR_UUID.equals(player.getUniqueID())) {
				Minecraft.getInstance().textureManager.bindTexture(HEART_LOC);
			} else {
				Minecraft.getInstance().textureManager.bindTexture(YUE_LOC);
			}

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder r = tess.getBuffer();
			r.begin(7, DefaultVertexFormats.POSITION_TEX);
			r.func_225582_a_(0, 0, 0).func_225583_a_(0, 0).endVertex();
			r.func_225582_a_(0, 0, 1).func_225583_a_(0, 1).endVertex();
			r.func_225582_a_(1, 0, 1).func_225583_a_(1, 1).endVertex();
			r.func_225582_a_(1, 0, 0).func_225583_a_(1, 0).endVertex();
			tess.draw();

			RenderSystem.enableLighting();
			RenderSystem.color3f(1F, 1F, 1F);
			RenderSystem.popMatrix();
		}
	}

	//TODO: 1.15??
	/*@Override
	public boolean shouldCombineTextures() {
		return false;
	}*/
}