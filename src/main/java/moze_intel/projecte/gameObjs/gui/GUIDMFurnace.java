package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GUIDMFurnace extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/dmfurnace.png");
	private final DMFurnaceTile tile;

	public GUIDMFurnace(InventoryPlayer invPlayer, DMFurnaceTile tile)
	{
		super(new DMFurnaceContainer(invPlayer, tile));
		this.xSize = 178;
		this.ySize = 165;
		this.tile = tile;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		int progress;
		if (tile.isBurning())
		{
			progress = tile.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(x + 49, y + 36 + 12 - progress, 179, 12 - progress, 14, progress + 2);
		}
		progress = tile.getCookProgressScaled(24);
		this.drawTexturedModalRect(x + 73, y + 34, 179, 14, progress + 1, 16);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) 
	{
		this.fontRenderer.drawString(I18n.format("pe.dmfurnace.shortname"), 57, 5, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 57, ySize - 96 + 2, 4210752);
	}
}
