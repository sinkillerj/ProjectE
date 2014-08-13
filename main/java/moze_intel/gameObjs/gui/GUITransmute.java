package moze_intel.gameObjs.gui;

import moze_intel.MozeCore;
import moze_intel.gameObjs.container.TransmuteContainer;
import moze_intel.gameObjs.tiles.TransmuteTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;


public class GUITransmute extends GuiContainer
{
	private final ResourceLocation texture = new ResourceLocation(MozeCore.MODID.toLowerCase(), "textures/gui/transmute.png");
	private TransmuteTile tile;

	public GUITransmute(InventoryPlayer invPlayer, TransmuteTile tile) 
	{
		super(new TransmuteContainer(invPlayer, tile));
		this.tile = tile;
		this.xSize = 228;
		this.ySize = 202;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) 
	{
		this.fontRendererObj.drawString("Transmutation", 28, 6, 4210752);
		String emc = String.format("EMC: %,d", (int) tile.GetStoredEMC()); 
		this.fontRendererObj.drawString(emc, 6, this.ySize - 96, 4210752);
		
		if (tile.learnFlag > 0)
		{
			this.fontRendererObj.drawString("L", 98, 36, 4210752);
			this.fontRendererObj.drawString("e", 99, 44, 4210752);
		    this.fontRendererObj.drawString("a", 100, 52, 4210752);
		    this.fontRendererObj.drawString("r", 101, 60, 4210752);
		    this.fontRendererObj.drawString("n", 102, 68, 4210752);
		    this.fontRendererObj.drawString("e", 103, 76, 4210752);
		    this.fontRendererObj.drawString("d", 104, 84, 4210752);
		    this.fontRendererObj.drawString("!", 107, 92, 4210752);
		    
		    tile.learnFlag--;
		}
	}
	
	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		tile.learnFlag = 0;
	}
}
