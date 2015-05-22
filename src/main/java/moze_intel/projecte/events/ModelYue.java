package moze_intel.projecte.events;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelYue extends ModelBase
{
	public void renderAll()
	{
		WorldRenderer r = Tessellator.getInstance().getWorldRenderer();
		r.startDrawingQuads();

		r.addVertexWithUV(0, 0, 0, 0, 0);
		r.addVertexWithUV(0, 0, 1, 0, 1);
		r.addVertexWithUV(1, 0, 1, 1, 1);
		r.addVertexWithUV(1, 0, 0, 1, 0);

		Tessellator.getInstance().draw();
	}
}
