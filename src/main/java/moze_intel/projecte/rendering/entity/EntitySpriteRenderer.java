package moze_intel.projecte.rendering.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;

public abstract class EntitySpriteRenderer<T extends Entity> extends EntityRenderer<T> {

	public EntitySpriteRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Override
	public void doRender(@Nonnull T entity, double x, double y, double z, float f, float partialTick) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) x, (float) y, (float) z);
		GlStateManager.rotatef(-this.renderManager.playerViewY, 0, 1, 0);
		if (renderManager.options.thirdPersonView == 2) {
			//TODO: Improve rendering of this in the secondary third person view
			GlStateManager.rotatef(-renderManager.playerViewX, 1, 0, 0);
		} else {
			GlStateManager.rotatef(renderManager.playerViewX - 90, 1, 0, 0);
		}
		GlStateManager.rotatef(-45, 0, 1, 0);
		GlStateManager.disableLighting();
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240F, 240F);

		bindTexture(getEntityTexture(entity));

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder r = tess.getBuffer();
		r.begin(7, DefaultVertexFormats.POSITION_TEX);
		r.pos(0, 0, 0).tex(0, 0).endVertex();
		r.pos(0, 0, 1).tex(0, 1).endVertex();
		r.pos(1, 0, 1).tex(1, 1).endVertex();
		r.pos(1, 0, 0).tex(1, 0).endVertex();
		tess.draw();

		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
