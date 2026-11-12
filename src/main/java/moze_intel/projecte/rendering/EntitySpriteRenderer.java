package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/** Camera-facing projectile sprite. */
public class EntitySpriteRenderer<ENTITY extends Entity> extends EntityRenderer<ENTITY, EntityRenderState> {

	private final Identifier texture;

	public EntitySpriteRenderer(EntityRendererProvider.Context context, Identifier texture) {
		super(context);
		this.texture = texture;
	}

	@Override
	public @NotNull EntityRenderState createRenderState() {
		return new EntityRenderState();
	}

	@Override
	public void submit(@NotNull EntityRenderState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.rotate(camera.orientation);
        collector.submitCustomGeometry(poseStack, PERenderType.SPRITE_RENDERER.apply(texture), (pose, buffer) -> {
            vertex(buffer, pose, state.lightCoords, 0, 0, 0, 1);
            vertex(buffer, pose, state.lightCoords, 1, 0, 1, 1);
            vertex(buffer, pose, state.lightCoords, 1, 1, 1, 0);
            vertex(buffer, pose, state.lightCoords, 0, 1, 0, 0);
        });
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
	}

    @Override
    protected int getBlockLightLevel(ENTITY entity, BlockPos pos) {
        return 15;
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, int light, float x, int y, int u, int v) {
        buffer.addVertex(pose, x - 0.5F, y, 0).setColor(-1).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
    }

	@NotNull
	public Identifier getTextureLocation(@NotNull ENTITY entity) {
		return texture;
	}
}
