package moze_intel.projecte.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class PedestalRenderer extends TileEntityRenderer<DMPedestalTile> {

	public PedestalRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(@Nonnull DMPedestalTile te, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight) {
		if (!te.isRemoved() && te.getLevel() != null) {
			if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				matrix.pushPose();
				BlockPos pos = te.getBlockPos();
				AxisAlignedBB aabb = te.getEffectBounds().move(-pos.getX(), -pos.getY(), -pos.getZ());
				IVertexBuilder vertexBuilder = renderer.getBuffer(RenderType.lines());
				WorldRenderer.renderLineBox(matrix, vertexBuilder, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1,
						1, 0, 1, 1, 1, 0, 1);
				matrix.popPose();
			}
			ItemStack stack = te.getInventory().getStackInSlot(0);
			if (!stack.isEmpty()) {
				matrix.pushPose();
				matrix.translate(0.5, 0.7, 0.5);
				long gameTime = te.getLevel().getGameTime();
				matrix.translate(0, MathHelper.sin((gameTime + partialTick) / 10.0F) * 0.1 + 0.1, 0);
				matrix.scale(0.75F, 0.75F, 0.75F);
				float angle = (gameTime + partialTick) / 20.0F * (180F / (float) Math.PI);
				matrix.mulPose(Vector3f.YP.rotationDegrees(angle));
				Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.GROUND, light, overlayLight, matrix, renderer);
				matrix.popPose();
			}
		}
	}
}