package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;

public class GUICondenserMK2 extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/condenser_mk2.png");
	private CondenserMK2Tile tile;
	private DecimalFormat EMCFormat = new DecimalFormat("###,###,###,###.###");

	public GUICondenserMK2(InventoryPlayer invPlayer, CondenserMK2Tile tile)
	{
		super(new CondenserMK2Container(invPlayer, tile));
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

		double progress = tile.getProgressScaled();
		this.drawTexturedModalRect(x + 33, y + 10, 0, 235, (int)progress, 10);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2)
	{
		double toDisplay = tile.displayEmc > tile.requiredEmc ? tile.requiredEmc : tile.displayEmc;
		this.fontRendererObj.drawString(EMCFormat.format(toDisplay), 140, 10, 4210752);
	}
}
