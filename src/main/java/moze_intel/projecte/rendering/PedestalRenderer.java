package moze_intel.projecte.rendering;

import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

public class PedestalRenderer extends TileEntitySpecialRenderer<DMPedestalTile>
{

    @Override
    public void renderTileEntityAt(@Nonnull DMPedestalTile te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if (!te.isInvalid())
        {
            if (te.getInventory().getStackInSlot(0) != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5, y + 0.65, z + 0.5);
                GlStateManager.translate(0, MathHelper.sin((te.getWorld().getTotalWorldTime() + partialTicks) / 10.0F) * 0.1F + 0.1F, 0);
                GlStateManager.scale(0.75, 0.75, 0.75);
                float angle = (te.getWorld().getTotalWorldTime() + partialTicks) / 20.0F * (180F / (float)Math.PI);
                GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
                Minecraft.getMinecraft().getRenderItem().renderItem(te.getInventory().getStackInSlot(0), ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.popMatrix();
            }
        }
    }

}
