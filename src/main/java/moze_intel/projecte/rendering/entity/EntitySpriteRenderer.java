package moze_intel.projecte.rendering.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import javax.annotation.Nonnull;
import moze_intel.projecte.rendering.PERenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

public abstract class EntitySpriteRenderer<T extends Entity> extends EntityRenderer<T> {

	public EntitySpriteRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(@Nonnull T entity, float entityYaw, float partialTick, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource renderer, int light) {
		matrix.pushPose();
		matrix.mulPose(entityRenderDispatcher.cameraOrientation());
		matrix.scale(0.5F, 0.5F, 0.5F);
		//TODO - 1.18: Make the texture locations be stored in static variables rather than recreating a RL each time for rendering
		VertexConsumer builder = renderer.getBuffer(PERenderType.SPRITE_RENDERER.apply(getTextureLocation(entity)));
		Matrix4f matrix4f = matrix.last().pose();
		builder.vertex(matrix4f, -1, -1, 0).uv(1, 1).endVertex();
		builder.vertex(matrix4f, -1, 1, 0).uv(1, 0).endVertex();
		builder.vertex(matrix4f, 1, 1, 0).uv(0, 0).endVertex();
		builder.vertex(matrix4f, 1, -1, 0).uv(0, 1).endVertex();
		matrix.popPose();
	}
}