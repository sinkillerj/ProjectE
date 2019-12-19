package moze_intel.projecte.rendering.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public abstract class EntitySpriteRenderer<T extends Entity> extends EntityRenderer<T> {

	public EntitySpriteRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Override
	public void func_225623_a_(@Nonnull T entity, float entityYaw, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light) {
		RenderSystem.pushMatrix();
		////TODO: 1.15 FIXME
		//RenderSystem.translatef((float) x, (float) y, (float) z);
		RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
		//TODO: Is this correct
		RenderSystem.multMatrix(new Matrix4f(renderManager.info.func_227995_f_()));
		//RenderSystem.rotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		//RenderSystem.rotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		RenderSystem.scalef(0.5F, 0.5F, 0.5F);
		RenderSystem.disableLighting();
		RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, 240F, 240F);

		renderManager.textureManager.bindTexture(getEntityTexture(entity));

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder r = tess.getBuffer();
		r.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		r.func_225582_a_(-1, -1, 0).func_225583_a_(1, 1).endVertex();
		r.func_225582_a_(-1, 1, 0).func_225583_a_(1, 0).endVertex();
		r.func_225582_a_(1, 1, 0).func_225583_a_(0, 0).endVertex();
		r.func_225582_a_(1, -1, 0).func_225583_a_(0, 1).endVertex();
		tess.draw();

		RenderSystem.enableLighting();
		RenderSystem.popMatrix();
	}
}