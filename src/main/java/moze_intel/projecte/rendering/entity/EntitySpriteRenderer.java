package moze_intel.projecte.rendering.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;

public abstract class EntitySpriteRenderer<T extends Entity> extends EntityRenderer<T> {

	public EntitySpriteRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Override
	public void func_225623_a_(@Nonnull T entity, float entityYaw, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light) {
		matrix.func_227860_a_();
		//TODO
		//RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
		//TODO: I believe this is correct
		//matrix.func_227866_c_().func_227870_a_().func_226595_a_(new Matrix4f(renderManager.info.func_227995_f_()));
		matrix.func_227863_a_(renderManager.func_229098_b_());
		//RenderSystem.multMatrix(new Matrix4f(renderManager.info.func_227995_f_()));
		matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_((float) -renderManager.info.getProjectedView().getY()));
		matrix.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_((float) renderManager.info.getProjectedView().getX()));
		//RenderSystem.rotatef(-renderManager.info.playerViewY, 0.0F, 1.0F, 0.0F);
		//RenderSystem.rotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		matrix.func_227862_a_(0.5F, 0.5F, 0.5F);
		//TODO: Make sure it is full bright
		//RenderSystem.disableLighting();
		//RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, 240F, 240F);

		IVertexBuilder vertexBuilder = renderer.getBuffer(RenderType.func_228638_b_(getEntityTexture(entity)));

		//Tessellator tess = Tessellator.getInstance();
		//BufferBuilder r = tess.getBuffer();
		//r.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		//TODO: FIXME
		//vertexBuilder.func_225582_a_(-1, -1, 0).func_225583_a_(1, 1).endVertex();
		//vertexBuilder.func_225582_a_(-1, 1, 0).func_225583_a_(1, 0).endVertex();
		//vertexBuilder.func_225582_a_(1, 1, 0).func_225583_a_(0, 0).endVertex();
		//vertexBuilder.func_225582_a_(1, -1, 0).func_225583_a_(0, 1).endVertex();
		//tess.draw();

		//RenderSystem.enableLighting();
		matrix.func_227865_b_();
	}
}