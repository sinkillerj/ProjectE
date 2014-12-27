package moze_intel.projecte.rendering;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import net.minecraft.client.model.ModelChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ChestItemRenderer implements IItemRenderer
{
	private final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/blocks/alchemy_chest.png");
	private final ModelChest model = new ModelChest();

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
				renderChest(0.5F, 0.5F, 0.5F, 0);
				break;
			case EQUIPPED:
				renderChest(1.0F, 1.0F, 1.0F, 0);
				break;
			case EQUIPPED_FIRST_PERSON:
				renderChest(1.0F, 1.0F, 1.0F, 0);
				break;
			case INVENTORY:
				renderChest(0.0F, 0.075F, 0.0F, 0);
				break;
			default:
				break;
		}
	}

	private void renderChest(float x, float y, float z, int metaData)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		GL11.glPushMatrix(); 
		GL11.glTranslatef(x, y, z); 
		GL11.glRotatef(180, 1, 0, 0);
		GL11.glRotatef(-90, 0, 1, 0);
		model.renderAll();
		GL11.glPopMatrix(); 
	}
}
