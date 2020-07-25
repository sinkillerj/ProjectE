package moze_intel.projecte.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.UUID;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

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

	@Override
	public void render(@Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, @Nonnull AbstractClientPlayerEntity player,
			float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (player.isInvisible()) {
			return;
		}
		if (PECore.DEV_ENVIRONMENT || SIN_UUID.equals(player.getUniqueID()) || CLAR_UUID.equals(player.getUniqueID())) {
			matrix.push();
			render.getEntityModel().bipedBodyWear.translateRotate(matrix);
			double yShift = -0.498;
			if (player.isCrouching()) {
				//Only modify where it renders if the player's pose is crouching
				matrix.rotate(Vector3f.XP.rotationDegrees(-28.64789F));
				yShift = -0.44;
			}
			matrix.rotate(Vector3f.ZP.rotationDegrees(180));
			matrix.scale(3, 3, 3);
			matrix.translate(-0.5, yShift, -0.5);
			IVertexBuilder builder = renderer.getBuffer(PERenderType.yeuRenderer(CLAR_UUID.equals(player.getUniqueID()) ? HEART_LOC : YUE_LOC));
			Matrix4f matrix4f = matrix.getLast().getMatrix();
			builder.pos(matrix4f, 0, 0, 0).tex(0, 0).color(0, 1, 0, 1).endVertex();
			builder.pos(matrix4f, 0, 0, 1).tex(0, 1).color(0, 1, 0, 1).endVertex();
			builder.pos(matrix4f, 1, 0, 1).tex(1, 1).color(0, 1, 0, 1).endVertex();
			builder.pos(matrix4f, 1, 0, 0).tex(1, 0).color(0, 1, 0, 1).endVertex();
			matrix.pop();
		}
	}
}