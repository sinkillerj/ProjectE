package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class PedestalRenderer implements BlockEntityRenderer<DMPedestalBlockEntity> {

	private final BlockEntityRendererProvider.Context context;

	public PedestalRenderer(BlockEntityRendererProvider.Context context) {
		this.context = context;
	}

	@Override
	public void render(@NotNull DMPedestalBlockEntity pedestal, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
		if (!pedestal.isRemoved() && pedestal.getLevel() != null) {
			if (this.context.getEntityRenderer().shouldRenderHitBoxes()) {
				matrix.pushPose();
				BlockPos pos = pedestal.getBlockPos();
				AABB aabb = pedestal.getEffectBounds().move(-pos.getX(), -pos.getY(), -pos.getZ());
				VertexConsumer vertexBuilder = renderer.getBuffer(RenderType.lines());
				LevelRenderer.renderLineBox(matrix, vertexBuilder, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ,
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
				float angle = (gameTime + partialTick) / ((float) SharedConstants.TICKS_PER_SECOND) * (180F / (float) Math.PI);
				matrix.mulPose(Axis.YP.rotationDegrees(angle));
				this.context.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlayLight, matrix, renderer, pedestal.getLevel(), (int) pedestal.getBlockPos().asLong());
				matrix.popPose();
			}
		}
	}

	@NotNull
	@Override
	public AABB getRenderBoundingBox(@NotNull DMPedestalBlockEntity pedestal) {
		if (this.context.getEntityRenderer().shouldRenderHitBoxes()) {
			return pedestal.getEffectBounds();
		}
		return BlockEntityRenderer.super.getRenderBoundingBox(pedestal);
	}
}