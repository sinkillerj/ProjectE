package moze_intel.projecte.rendering.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import moze_intel.projecte.rendering.PERenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
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
		matrix.func_227863_a_(renderManager.func_229098_b_());
		matrix.func_227862_a_(0.5F, 0.5F, 0.5F);
		IVertexBuilder builder = renderer.getBuffer(PERenderType.spriteRenderer(getEntityTexture(entity)));
		Matrix4f matrix4f = matrix.func_227866_c_().func_227870_a_();
		builder.func_227888_a_(matrix4f, -1, -1, 0).func_225583_a_(1, 1).endVertex();
		builder.func_227888_a_(matrix4f, -1, 1, 0).func_225583_a_(1, 0).endVertex();
		builder.func_227888_a_(matrix4f, 1, 1, 0).func_225583_a_(0, 0).endVertex();
		builder.func_227888_a_(matrix4f, 1, -1, 0).func_225583_a_(0, 1).endVertex();
		matrix.func_227865_b_();
	}
}