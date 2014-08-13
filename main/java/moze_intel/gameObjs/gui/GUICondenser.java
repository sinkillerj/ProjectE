package moze_intel.gameObjs.gui;

import moze_intel.MozeCore;
import moze_intel.gameObjs.container.CondenserContainer;
import moze_intel.gameObjs.tiles.CondenserTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GUICondenser extends GuiContainer
{
	private final ResourceLocation texture = new ResourceLocation(MozeCore.MODID.toLowerCase(), "textures/gui/condenser.png");
	private CondenserTile tile;
	
	public GUICondenser(InventoryPlayer invPlayer, CondenserTile tile)
	{
		super(new CondenserContainer(invPlayer, tile));
		this.tile = tile;
		this.xSize = 255;
	    this.ySize = 233;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		int progress = tile.GetProgressScaled();
		this.drawTexturedModalRect(x + 33, y + 10, 0, 235, progress, 10);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) 
	{
		this.fontRendererObj.drawString(Integer.toString(tile.displayEmc), 140, 10, 4210752);
	}
}
