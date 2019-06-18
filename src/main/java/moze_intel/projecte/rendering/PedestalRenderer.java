package moze_intel.projecte.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

public class PedestalRenderer extends TileEntityRenderer<DMPedestalTile>
{

    @Override
    public void render(@Nonnull DMPedestalTile te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if (!te.isRemoved())
        {
            if (Minecraft.getInstance().getRenderManager().isDebugBoundingBox())
            {
                GlStateManager.pushMatrix();
                GlStateManager.translated(x, y, z);
                GlStateManager.depthMask(false);
                GlStateManager.disableTexture();
                GlStateManager.disableLighting();
                GlStateManager.disableCull();
                GlStateManager.disableBlend();
                AxisAlignedBB aabb = te.getEffectBounds().offset(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
                WorldRenderer.drawBoundingBox(
                        aabb.minX, aabb.minY, aabb.minZ,
                        aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1,
                        1F, 0F, 1F, 1F);
                GlStateManager.enableBlend();
                GlStateManager.enableCull();
                GlStateManager.enableLighting();
                GlStateManager.enableTexture();
                GlStateManager.depthMask(true);
                GlStateManager.popMatrix();
            }

            if (!te.getInventory().getStackInSlot(0).isEmpty())
            {
                GlStateManager.pushMatrix();
                GlStateManager.translated(x + 0.5, y + 0.7, z + 0.5);
                GlStateManager.translated(0, MathHelper.sin((te.getWorld().getGameTime() + partialTicks) / 10.0F) * 0.1F + 0.1F, 0);
                GlStateManager.scaled(0.75, 0.75, 0.75);
                float angle = (te.getWorld().getGameTime() + partialTicks) / 20.0F * (180F / (float)Math.PI);
                GlStateManager.rotatef(angle, 0.0F, 1.0F, 0.0F);
                Minecraft.getInstance().getItemRenderer().renderItem(te.getInventory().getStackInSlot(0), ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.popMatrix();
            }
        }
    }

}
