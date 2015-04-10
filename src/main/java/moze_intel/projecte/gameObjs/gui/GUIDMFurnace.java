package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GUIDMFurnace extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/dmfurnace.png");
	private DMFurnaceTile tile;

	public GUIDMFurnace(InventoryPlayer invPlayer, DMFurnaceTile tile)
	{
		super(new DMFurnaceContainer(invPlayer, tile));
		this.xSize = 178;
		this.ySize = 165;
		this.tile = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
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
		this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.dmfurnace.shortname"), 57, 5, 4210752);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 57, ySize - 96 + 2, 4210752);
	}
}
