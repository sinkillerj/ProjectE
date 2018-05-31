package moze_intel.projecte.rendering;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class NovaCataclysmRenderer extends Render<EntityNovaCataclysmPrimed>
{
	public NovaCataclysmRenderer(RenderManager manager)
	{
		super(manager);
		this.shadowSize = 0.5F;
	}

	@Override
	public void doRender(@Nonnull EntityNovaCataclysmPrimed entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y + 0.5F, (float)z);
		float f2;

		if ((float)entity.getFuse() - partialTicks + 1.0F < 10.0F)
		{
			f2 = 1.0F - ((float)entity.getFuse() - partialTicks + 1.0F) / 10.0F;
			f2 = MathHelper.clamp(f2, 0.0F, 1.0F);
			f2 *= f2;
			f2 *= f2;
			float f3 = 1.0F + f2 * 0.3F;
			GlStateManager.scale(f3, f3, f3);
		}

		f2 = (1.0F - ((float)entity.getFuse() - partialTicks + 1.0F) / 100.0F) * 0.8F;
		this.bindEntityTexture(entity);
		GlStateManager.translate(-0.5F, -0.5F, 0.5F);
		blockrendererdispatcher.renderBlockBrightness(ObjHandler.novaCataclysm.getDefaultState(), entity.getBrightness());
		GlStateManager.translate(0.0F, 0.0F, 1.0F);

		if (entity.getFuse() / 5 % 2 == 0)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 772);
			GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
			GlStateManager.doPolygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolygonOffset();
			blockrendererdispatcher.renderBlockBrightness(ObjHandler.novaCataclysm.getDefaultState(), 1.0F);
			GlStateManager.doPolygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
		}

		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Nonnull
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityNovaCataclysmPrimed entity)
	{
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}
