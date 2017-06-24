package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.MercurialEyeContainer;
import moze_intel.projecte.gameObjs.container.inventory.MercurialEyeInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GUIMercurialEye extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/mercurial_eye.png");

	public GUIMercurialEye(InventoryPlayer invPlayer, MercurialEyeInventory inventory) 
	{
		super(new MercurialEyeContainer(invPlayer, inventory));
		this.xSize = 171;
		this.ySize = 134;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2)
	{
		/*this.fontRendererObj.drawString("Mercurial Eye", 52, 6, 4210752);
		this.fontRendererObj.drawString("Target", 124, 24, 4210752);
		this.fontRendererObj.drawString("Klein", 5, 24, 4210752);
		this.fontRendererObj.drawString("Inventory", 5, this.ySize - 90, 4210752);
		
		ItemStack target = inventory.getTargetStack();
		ItemStack klein = inventory.getKleinStack();
		
		if (target != null)
			this.fontRendererObj.drawString(Integer.toString(Utils.getEmcValue(target)), 124, 34, 4210752);
		
		if (klein != null)
			this.fontRendererObj.drawString(Integer.toString((int) ItemBase.getEmc(klein)), 5, 34, 4210752);*/
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		this.drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}
}
