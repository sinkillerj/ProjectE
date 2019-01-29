package moze_intel.projecte.rendering;

import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

public class PedestalRenderer extends TileEntitySpecialRenderer<DMPedestalTile>
{

	@Override
	public void render(@Nonnull DMPedestalTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		if (!te.isInvalid())
		{
			if (Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox())
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);
				GlStateManager.depthMask(false);
				GlStateManager.disableTexture2D();
				GlStateManager.disableLighting();
				GlStateManager.disableCull();
				GlStateManager.disableBlend();
				AxisAlignedBB aabb = te.getEffectBounds().offset(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
				RenderGlobal.drawBoundingBox(
						aabb.minX, aabb.minY, aabb.minZ,
						aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1,
						1F, 0F, 1F, 1F);
				GlStateManager.enableBlend();
				GlStateManager.enableCull();
				GlStateManager.enableLighting();
				GlStateManager.enableTexture2D();
				GlStateManager.depthMask(true);
				GlStateManager.popMatrix();
			}

			ItemStack stack = te.getInventory().getStackInSlot(0);
			if (!stack.isEmpty())
			{
				GlStateManager.pushAttrib();
				GlStateManager.pushMatrix();

				RenderHelper.enableStandardItemLighting();

				GlStateManager.translate(x + 0.5, y + 0.75, z + 0.5);

				float bob = MathHelper.sin(8 * (float) Math.PI * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) * 0.1F + 0.1F;
				GlStateManager.translate(0, bob, 0);

				GlStateManager.scale(0.75, 0.75, 0.75);

				float angle = 2 * 360.0F * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL;
				GlStateManager.rotate(angle, 0, 1, 0);

				Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

				GlStateManager.popMatrix();
				GlStateManager.popAttrib();
			}
		}
	}

}
