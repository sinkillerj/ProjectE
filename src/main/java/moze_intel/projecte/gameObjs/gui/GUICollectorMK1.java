package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GUICollectorMK1 extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/collector1.png");
	private CollectorMK1Tile tile;
	
	public GUICollectorMK1(InventoryPlayer invPlayer, CollectorMK1Tile tile)
	{
		super(new CollectorMK1Container(invPlayer, tile));
		this.tile = tile;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2)
	{
		this.fontRendererObj.drawString(Double.toString(tile.displayEmc), 60, 32, 4210752);
		
		double kleinCharge = tile.displayKleinCharge;
		
		if (kleinCharge != -1)
		{
			this.fontRendererObj.drawString(Double.toString(kleinCharge), 60, 44, 4210752);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		//Ligh Level. Max is 12
		double progress = tile.getSunLevelScaled(12);
		this.drawTexturedModalRect(x + 126, y + 49 - (int)progress, 177, 13 - (int)progress, 12, (int)progress);
		
		//EMC storage. Max is 48
		this.drawTexturedModalRect(x + 64, y + 18, 0, 166, (int)tile.getEmcScaled(48), 10);
		
		//Klein Star Charge Progress. Max is 48
		progress = tile.getKleinStarChargeScaled(48);
		this.drawTexturedModalRect(x + 64, y + 58, 0, 166, (int)progress, 10);
		
		//Fuel Progress. Max is 24.
		progress = tile.getFuelProgressScaled(24);
		this.drawTexturedModalRect(x + 138, y + 55 - (int)progress, 176, 38 - (int)progress, 10, (int)progress + 1);
	}
}
