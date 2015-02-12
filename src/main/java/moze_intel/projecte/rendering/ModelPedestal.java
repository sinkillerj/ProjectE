package moze_intel.projecte.rendering;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.utils.Constants;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

/**
 * Created by Vincent on 2/11/2015.
 */

@SideOnly(Side.CLIENT)
public class ModelPedestal
{
	private IModelCustom modelPedestal;

	public ModelPedestal()
	{
		modelPedestal = AdvancedModelLoader.loadModel(Constants.PEDESTAL_MODEL_LOCATION);
	}

	public void render()
	{
		modelPedestal.renderAll();
	}
}
