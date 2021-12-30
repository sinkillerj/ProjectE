package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class PedestalRenderer implements BlockEntityRenderer<DMPedestalBlockEntity> {

	public PedestalRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(@Nonnull DMPedestalBlockEntity pedestal, float partialTick, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource renderer, int light, int overlayLight) {
		if (!pedestal.isRemoved() && pedestal.getLevel() != null) {
			if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				matrix.pushPose();
				BlockPos pos = pedestal.getBlockPos();
				AABB aabb = pedestal.getEffectBounds().move(-pos.getX(), -pos.getY(), -pos.getZ());
				VertexConsumer vertexBuilder = renderer.getBuffer(RenderType.lines());
				LevelRenderer.renderLineBox(matrix, vertexBuilder, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1,
						1, 0, 1, 1, 1, 0, 1);
				matrix.popPose();
			}
			ItemStack stack = pedestal.getInventory().getStackInSlot(0);
			if (!stack.isEmpty()) {
				matrix.pushPose();
				matrix.translate(0.5, 0.7, 0.5);
				long gameTime = pedestal.getLevel().getGameTime();
				matrix.translate(0, Mth.sin((gameTime + partialTick) / 10.0F) * 0.1 + 0.1, 0);
				matrix.scale(0.75F, 0.75F, 0.75F);
				float angle = (gameTime + partialTick) / 20.0F * (180F / (float) Math.PI);
				matrix.mulPose(Vector3f.YP.rotationDegrees(angle));
				Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.GROUND, light, overlayLight, matrix, renderer, (int) pedestal.getBlockPos().asLong());
				matrix.popPose();
			}
		}
	}
}