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
	public void render(@Nonnull T entity, float entityYaw, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light) {
		matrix.push();
		matrix.rotate(renderManager.getCameraOrientation());
		matrix.scale(0.5F, 0.5F, 0.5F);
		IVertexBuilder builder = renderer.getBuffer(PERenderType.spriteRenderer(getEntityTexture(entity)));
		Matrix4f matrix4f = matrix.getLast().getPositionMatrix();
		builder.pos(matrix4f, -1, -1, 0).tex(1, 1).endVertex();
		builder.pos(matrix4f, -1, 1, 0).tex(1, 0).endVertex();
		builder.pos(matrix4f, 1, 1, 0).tex(0, 0).endVertex();
		builder.pos(matrix4f, 1, -1, 0).tex(0, 1).endVertex();
		matrix.pop();
	}
}