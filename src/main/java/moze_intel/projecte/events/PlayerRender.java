package moze_intel.projecte.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class PlayerRender
{
	private static ModelYue yuemodel = new ModelYue();

	private static String sinuuid = "5f86012c-ca4b-451a-989c-8fab167af647";
	private static String claruuid = "e5c59746-9cf7-4940-a849-d09e1f1efc13";

	private String playeruuid = "";

	@SubscribeEvent
	public void playerRender(RenderPlayerEvent.Specials.Pre evt)
	{
		playeruuid = evt.entityPlayer.getUniqueID().toString();

		if(playeruuid.equals(sinuuid) || playeruuid.equals(claruuid))
		{
			GL11.glPushMatrix();
			evt.renderer.modelBipedMain.bipedBody.postRender(0.0625f);
			if (evt.entityPlayer.isSneaking())
			{
				GL11.glRotatef(-28.64789F, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(0.0f, -0.1f, 0.0f);
			}
			GL11.glRotatef(180, 0, 0, 1);
			GL11.glScalef(3.0f, 3.0f, 3.0f);
			GL11.glTranslatef(-0.5f, -0.498f, -0.5f);
			//GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

			if(playeruuid.equals(claruuid))
			{
				GL11.glColor4f(0.49F, 0.97F, 1.0F, 1.0F);
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("projecte:textures/models/heartcircle.png"));
			}
			else
			{
				GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("projecte:textures/models/yuecircle.png"));
			}

			yuemodel.renderAll();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}

	@SubscribeEvent
	public void onFOVUpdateEvent(FOVUpdateEvent evt)
	{
		if (evt.entity.getCurrentArmor(0) != null && evt.entity.getCurrentArmor(0).getItem() instanceof GemFeet)
		{
			evt.newfov = evt.fov - 0.4F;
		}
	}
}
