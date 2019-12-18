package moze_intel.projecte.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class PedestalRenderer extends TileEntityRenderer<DMPedestalTile> {

	public PedestalRenderer() {
		super(TileEntityRendererDispatcher.instance);
	}

	@Override
	public void func_225616_a_(@Nonnull DMPedestalTile te, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int otherLight) {
		//TODO: 1.15 FIXME
		/*if (!te.isRemoved()) {
			if (Minecraft.getInstance().getRenderManager().isDebugBoundingBox()) {
				RenderSystem.pushMatrix();
				RenderSystem.translated(x, y, z);
				RenderSystem.depthMask(false);
				RenderSystem.disableTexture();
				RenderSystem.disableLighting();
				RenderSystem.disableCull();
				RenderSystem.disableBlend();
				AxisAlignedBB aabb = te.getEffectBounds().offset(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
				IVertexBuilder vertexBuilder = renderer.getBuffer();
				//public static void func_228428_a_(MatrixStack p_228428_0_, IVertexBuilder p_228428_1_, double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
				// float p_228428_14_, float p_228428_15_, float p_228428_16_, float p_228428_17_, float p_228428_18_, float p_228428_19_, float p_228428_20_) {
				//public static void drawBoundingBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
				// float red, float green, float blue, float alpha) {
				WorldRenderer.func_228428_a_(matrix, vertexBuilder,
						aabb.minX, aabb.minY, aabb.minZ,
						aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1,
						1F, 0F, 1F, 1F);
				RenderSystem.enableBlend();
				RenderSystem.enableCull();
				RenderSystem.enableLighting();
				RenderSystem.enableTexture();
				RenderSystem.depthMask(true);
				RenderSystem.popMatrix();
			}

			if (!te.getInventory().getStackInSlot(0).isEmpty()) {
				RenderSystem.pushMatrix();
				RenderSystem.translated(x + 0.5, y + 0.7, z + 0.5);
				RenderSystem.translated(0, MathHelper.sin((te.getWorld().getGameTime() + partialTick) / 10.0F) * 0.1F + 0.1F, 0);
				RenderSystem.scaled(0.75, 0.75, 0.75);
				float angle = (te.getWorld().getGameTime() + partialTick) / 20.0F * (180F / (float) Math.PI);
				RenderSystem.rotatef(angle, 0.0F, 1.0F, 0.0F);
				Minecraft.getInstance().getItemRenderer().renderItem(te.getInventory().getStackInSlot(0), ItemCameraTransforms.TransformType.GROUND);
				RenderSystem.popMatrix();
			}
		}*/
	}
}