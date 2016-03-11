package moze_intel.projecte.rendering;

import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;

public class PedestalRenderer extends TileEntitySpecialRenderer<DMPedestalTile>
{

    @Override
    public void renderTileEntityAt(DMPedestalTile te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if (!te.isInvalid())
        {
            if (te.getInventory().getStackInSlot(0) != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
                GlStateManager.scale(0.5, 0.5, 0.5);
                GlStateManager.translate(0, 0.3 * Math.sin(0.1 * (te.getWorld().getWorldTime() + partialTicks)), 0);
                float angle = (te.getWorld().getWorldTime() + partialTicks) / 20.0F * (180F / (float)Math.PI);
                GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                Minecraft.getMinecraft().getRenderItem().renderItem(te.getInventory().getStackInSlot(0), ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.popMatrix();
            }
        }
    }

}
