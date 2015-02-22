package moze_intel.projecte.rendering;

import cpw.mods.fml.client.FMLClientHandler;
import moze_intel.projecte.rendering.model.ModelPedestal;
import moze_intel.projecte.utils.Constants;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class PedestalItemRenderer implements IItemRenderer
{
	private final ResourceLocation texture = Constants.PEDESTAL_MODELTEX_LOCATION;
	private final ModelPedestal model = new ModelPedestal();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		switch (type)
		{
			case ENTITY:
				renderPedestal(0.0F, 1.0F, 0.0F, 0);
				break;
			case EQUIPPED:
				renderPedestal(1.0F, 1.15F, 1.00F, 0);
				break;
			case EQUIPPED_FIRST_PERSON:
				renderPedestal(1.0F, 1.6F, 1.0F, 0);
				break;
			case INVENTORY:
				renderPedestal(0.0F, 1.0F, 0.0F, 0);
				break;
			default:
				break;
		}
	}

	private void renderPedestal(float x, float y, float z, int meta)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(180, 1, 0, 0);
		GL11.glRotatef(-90, 0, 1, 0);
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}


}
