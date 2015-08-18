package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GUICollectorMK2 extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/collector2.png");
	private CollectorMK2Tile tile;
	
	public GUICollectorMK2(InventoryPlayer invPlayer, CollectorMK2Tile tile)
	{
		super(new CollectorMK2Container(invPlayer, tile));
		this.tile = tile;
		this.xSize = 200;
		this.ySize = 165;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2)
	{
		this.fontRendererObj.drawString(Integer.toString(tile.displayEmc), 75, 32, 4210752);
		
		double kleinCharge = tile.displayItemCharge;
		if (kleinCharge != -1)
			this.fontRendererObj.drawString(Constants.EMC_FORMATTER.format(kleinCharge), 75, 44, 4210752);
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
		int progress = tile.getSunLevelScaled(12);
		this.drawTexturedModalRect(x + 142, y + 49 - progress, 202, 13 - progress, 12, progress);
				
		//EMC storage. Max is 48
		progress = tile.getEmcScaled(48);
		this.drawTexturedModalRect(x + 80, y + 18, 0, 166, progress, 10);
				
		//Klein Star Charge Progress. Max is 48
		progress = tile.getKleinStarChargeScaled(48);
		this.drawTexturedModalRect(x + 80, y + 58, 0, 166, progress, 10);
		
		//Fuel Progress. Max is 24.
		progress = tile.getFuelProgressScaled(24);
		this.drawTexturedModalRect(x + 154, y + 55 - progress, 201, 38 - progress, 10, progress + 1);
	}
}
