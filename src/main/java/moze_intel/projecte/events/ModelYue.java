package moze_intel.projecte.events;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
public class ModelYue extends ModelBase
{
	Tessellator tessellator;

	public ModelYue()
	{
		tessellator = Tessellator.instance;
	}

	public void renderAll()
	{
		tessellator.startDrawingQuads();

		tessellator.addVertexWithUV(0, 0, 0, 0, 0);
		tessellator.addVertexWithUV(0, 0, 1, 0, 1);
		tessellator.addVertexWithUV(1, 0, 1, 1, 1);
		tessellator.addVertexWithUV(1, 0, 0, 1, 0);

		tessellator.draw();
	}
}
