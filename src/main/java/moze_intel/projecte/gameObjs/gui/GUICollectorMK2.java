package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GUICollectorMK2 extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/collector2.png");
	private final CollectorMK2Tile tile;
	private final CollectorMK2Container container;
	
	public GUICollectorMK2(InventoryPlayer invPlayer, CollectorMK2Tile tile)
	{
		super(new CollectorMK2Container(invPlayer, tile));
		this.container = ((CollectorMK2Container) inventorySlots);
		this.tile = tile;
		this.xSize = 200;
		this.ySize = 165;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2)
	{
		this.fontRendererObj.drawString(Integer.toString(container.emc), 75, 32, 4210752);
		
		double kleinCharge = container.kleinEmc;
		if (kleinCharge > 0)
			this.fontRendererObj.drawString(Constants.EMC_FORMATTER.format(kleinCharge), 75, 44, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		//Ligh Level. Max is 12
		int progress = (int) (container.sunLevel * 12.0 / 16);
		this.drawTexturedModalRect(x + 142, y + 49 - progress, 202, 13 - progress, 12, progress);
				
		//EMC storage. Max is 48
		this.drawTexturedModalRect(x + 80, y + 18, 0, 166, (int) (container.emc / tile.getMaximumEmc() * 48), 10);
				
		//Klein Star Charge Progress. Max is 48
		progress = (int) (container.kleinChargeProgress * 48);
		this.drawTexturedModalRect(x + 80, y + 58, 0, 166, progress, 10);
		
		//Fuel Progress. Max is 24.
		progress = (int) (container.fuelProgress * 24);
		this.drawTexturedModalRect(x + 154, y + 55 - progress, 201, 38 - progress, 10, progress + 1);
	}
}
