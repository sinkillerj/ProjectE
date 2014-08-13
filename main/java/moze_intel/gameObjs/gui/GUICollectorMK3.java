package moze_intel.gameObjs.gui;

import moze_intel.MozeCore;
import moze_intel.gameObjs.container.CollectorMK3Container;
import moze_intel.gameObjs.tiles.CollectorMK3Tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GUICollectorMK3 extends GuiContainer
{
	private final ResourceLocation texture = new ResourceLocation(MozeCore.MODID.toLowerCase(), "textures/gui/collector3.png");
	private CollectorMK3Tile tile;
	
	public GUICollectorMK3(InventoryPlayer invPlayer, CollectorMK3Tile tile)
	{
		super(new CollectorMK3Container(invPlayer, tile));
		this.tile = tile;
		this.xSize = 218;
	    this.ySize = 165;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2)
	{
		this.fontRendererObj.drawString(Integer.toString(tile.displayEmc), 91, 32, 4210752);
		
		int kleinCharge = tile.displayKleinCharge;
		if (kleinCharge != -1)
			this.fontRendererObj.drawString(Integer.toString(kleinCharge), 91, 44, 4210752);
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
		int progress = tile.GetSunLevelScaled(12);
		this.drawTexturedModalRect(x + 160, y + 49 - progress, 220, 13 - progress, 12, progress);
				
		//EMC storage. Max is 48
		progress = tile.GetEmcScaled(48);
		this.drawTexturedModalRect(x + 98, y + 18, 0, 166, progress, 10);
				
		//Klein Star Charge Progress. Max is 48
		progress = tile.GetKleinStarChargeScaled(48);
		this.drawTexturedModalRect(x + 98, y + 58, 0, 166, progress, 10);
	}
}
