package moze_intel.projecte.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class PedestalRenderer extends TileEntityRenderer<DMPedestalTile> {

	public PedestalRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void func_225616_a_(@Nonnull DMPedestalTile te, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int otherLight) {
		if (!te.isRemoved()) {
			if (Minecraft.getInstance().getRenderManager().isDebugBoundingBox()) {
				matrix.func_227860_a_();
				AxisAlignedBB aabb = te.getEffectBounds().offset(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
				IVertexBuilder vertexBuilder = renderer.getBuffer(RenderType.func_228659_m_());
				WorldRenderer.func_228428_a_(matrix, vertexBuilder, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1,
						1, 0, 1, 1, 1, 0, 1);
				matrix.func_227865_b_();
			}
			ItemStack stack = te.getInventory().getStackInSlot(0);
			if (!stack.isEmpty()) {
				matrix.func_227860_a_();
				matrix.func_227861_a_(0.5, 0.7, 0.5);
				matrix.func_227861_a_(0, MathHelper.sin((te.getWorld().getGameTime() + partialTick) / 10.0F) * 0.1 + 0.1, 0);
				matrix.func_227862_a_(0.75F, 0.75F, 0.75F);
				float angle = (te.getWorld().getGameTime() + partialTick) / 20.0F * (180F / (float) Math.PI);
				matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(angle));
				Minecraft.getInstance().getItemRenderer().func_229110_a_(stack, TransformType.GROUND, light, otherLight, matrix, renderer);
				matrix.func_227865_b_();
			}
		}
	}
}