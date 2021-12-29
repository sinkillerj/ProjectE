package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.UUID;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class LayerYue extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	private static final UUID SIN_UUID = UUID.fromString("5f86012c-ca4b-451a-989c-8fab167af647");
	private static final UUID CLAR_UUID = UUID.fromString("e5c59746-9cf7-4940-a849-d09e1f1efc13");
	private static final ResourceLocation HEART_LOC = PECore.rl("textures/models/heartcircle.png");
	private static final ResourceLocation YUE_LOC = PECore.rl("textures/models/yuecircle.png");

	private final PlayerRenderer render;

	public LayerYue(PlayerRenderer renderer) {
		super(renderer);
		this.render = renderer;
	}

	@Override
	public void render(@Nonnull PoseStack matrix, @Nonnull MultiBufferSource renderer, int light, @Nonnull AbstractClientPlayer player,
			float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (player.isInvisible()) {
			return;
		}
		if (!FMLEnvironment.production || SIN_UUID.equals(player.getUUID()) || CLAR_UUID.equals(player.getUUID())) {
			matrix.pushPose();
			render.getModel().jacket.translateAndRotate(matrix);
			double yShift = -0.498;
			if (player.isCrouching()) {
				//Only modify where it renders if the player's pose is crouching
				matrix.mulPose(Vector3f.XP.rotationDegrees(-28.64789F));
				yShift = -0.44;
			}
			matrix.mulPose(Vector3f.ZP.rotationDegrees(180));
			matrix.scale(3, 3, 3);
			matrix.translate(-0.5, yShift, -0.5);
			VertexConsumer builder = renderer.getBuffer(PERenderType.YEU_RENDERER.apply(CLAR_UUID.equals(player.getUUID()) ? HEART_LOC : YUE_LOC));
			Matrix4f matrix4f = matrix.last().pose();
			builder.vertex(matrix4f, 0, 0, 0).color(0, 255, 0, 255).uv(0, 0).endVertex();
			builder.vertex(matrix4f, 0, 0, 1).color(0, 255, 0, 255).uv(0, 1).endVertex();
			builder.vertex(matrix4f, 1, 0, 1).color(0, 255, 0, 255).uv(1, 1).endVertex();
			builder.vertex(matrix4f, 1, 0, 0).color(0, 255, 0, 255).uv(1, 0).endVertex();
			matrix.popPose();
		}
	}
}