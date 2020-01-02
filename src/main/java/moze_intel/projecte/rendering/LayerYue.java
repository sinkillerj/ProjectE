package moze_intel.projecte.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.UUID;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;

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
	public void func_225628_a_(@Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, @Nonnull AbstractClientPlayerEntity player,
			float angle1, float angle2, float partialTick, float angle3, float angle4, float angle5) {
		if (player.isInvisible()) {
			return;
		}
		if (PECore.DEV_ENVIRONMENT || SIN_UUID.equals(player.getUniqueID()) || CLAR_UUID.equals(player.getUniqueID())) {
			matrix.func_227860_a_();
			render.getEntityModel().bipedBodyWear.func_228307_a_(matrix);
			double yShift = -0.498;
			if (player.isCrouching()) {
				//Only modify where it renders if the player's pose is crouching
				matrix.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-28.64789F));
				yShift = -0.44;
			}
			matrix.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180));
			matrix.func_227862_a_(3, 3, 3);
			matrix.func_227861_a_(-0.5, yShift, -0.5);
			IVertexBuilder builder = renderer.getBuffer(PERenderType.yeuRenderer(CLAR_UUID.equals(player.getUniqueID()) ? HEART_LOC : YUE_LOC));
			Matrix4f matrix4f = matrix.func_227866_c_().func_227870_a_();
			builder.func_227888_a_(matrix4f, 0, 0, 0).func_225583_a_(0, 0).func_227885_a_(0, 1, 0, 1).endVertex();
			builder.func_227888_a_(matrix4f, 0, 0, 1).func_225583_a_(0, 1).func_227885_a_(0, 1, 0, 1).endVertex();
			builder.func_227888_a_(matrix4f, 1, 0, 1).func_225583_a_(1, 1).func_227885_a_(0, 1, 0, 1).endVertex();
			builder.func_227888_a_(matrix4f, 1, 0, 0).func_225583_a_(1, 0).func_227885_a_(0, 1, 0, 1).endVertex();
			matrix.func_227865_b_();
		}
	}
}