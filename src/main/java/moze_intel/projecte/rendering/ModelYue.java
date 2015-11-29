package moze_intel.projecte.rendering;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsVertexFormat;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelYue extends ModelBase
{
	public void renderAll()
	{
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer r = tess.getWorldRenderer();
		r.begin(7, DefaultVertexFormats.POSITION_TEX);
		r.pos(0, 0, 0).tex(0, 0).endVertex();
		r.pos(0, 0, 1).tex(0, 1).endVertex();
		r.pos(1, 0, 1).tex(1, 1).endVertex();
		r.pos(1, 0, 0).tex(1, 0).endVertex();
		tess.draw();
	}
}
