package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GUIRelayMK1 extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/relay1.png");
	private final RelayMK1Tile tile;
	private final RelayMK1Container container;
	
	public GUIRelayMK1(InventoryPlayer invPlayer, RelayMK1Tile tile)
	{
		super(new RelayMK1Container(invPlayer, tile));
		this.tile = tile;
		this.xSize = 175;
		this.ySize = 176;
		this.container = (RelayMK1Container) inventorySlots;
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
		this.fontRenderer.drawString(I18n.format("pe.relay.mk1"), 10, 6, 4210752);
		this.fontRenderer.drawString(Constants.EMC_FORMATTER.format(container.emc), 88, 24, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		//Emc bar progress. Max is 102.
		int progress = (int) ((double) container.emc / tile.getMaximumEmc() * 102);
		this.drawTexturedModalRect(x + 64, y + 6, 30, 177, progress, 10);
		
		//Klein start bar progress. Max is 30.
		progress = (int) (container.kleinChargeProgress * 30);
		this.drawTexturedModalRect(x + 116, y + 67, 0, 177, progress, 10);
		
		//Burn Slot bar progress. Max is 30.
		progress = (int) (container.inputBurnProgress * 30);
		drawTexturedModalRect(x + 64, y + 67, 0, 177, progress, 10);
	}
}
