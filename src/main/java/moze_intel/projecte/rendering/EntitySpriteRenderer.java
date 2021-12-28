package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class EntitySpriteRenderer<ENTITY extends Entity> extends EntityRenderer<ENTITY> {

	private final ResourceLocation texture;

	public EntitySpriteRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
		super(context);
		this.texture = texture;
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull ENTITY entity) {
		return texture;
	}

	@Override
	public void render(@Nonnull ENTITY entity, float entityYaw, float partialTick, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource renderer, int light) {
		matrix.pushPose();
		matrix.mulPose(entityRenderDispatcher.cameraOrientation());
		matrix.scale(0.5F, 0.5F, 0.5F);
		VertexConsumer builder = renderer.getBuffer(PERenderType.SPRITE_RENDERER.apply(getTextureLocation(entity)));
		Matrix4f matrix4f = matrix.last().pose();
		builder.vertex(matrix4f, -1, -1, 0).uv(1, 1).endVertex();
		builder.vertex(matrix4f, -1, 1, 0).uv(1, 0).endVertex();
		builder.vertex(matrix4f, 1, 1, 0).uv(0, 0).endVertex();
		builder.vertex(matrix4f, 1, -1, 0).uv(0, 1).endVertex();
		matrix.popPose();
	}
}