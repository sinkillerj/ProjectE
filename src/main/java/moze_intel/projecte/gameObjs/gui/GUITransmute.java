package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmuteContainer;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GUITransmute extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/transmute.png");
	private TransmuteTile tile;
	private GuiTextField textBoxFilter;

	int xLocation;
	int yLocation;

	public GUITransmute(InventoryPlayer invPlayer, TransmuteTile tile) 
	{
		super(new TransmuteContainer(invPlayer, tile));
		this.tile = tile;
		this.xSize = 228;
		this.ySize = 196;
	}
	
	@Override
	public void initGui() 
	{
		tile.setPlayer(Minecraft.getMinecraft().thePlayer);
		super.initGui();

		this.xLocation = (this.width - this.xSize) / 2;
		this.yLocation = (this.height - this.ySize) / 2;

		this.textBoxFilter = new GuiTextField(this.fontRendererObj, this.xLocation + 88, this.yLocation + 8, 45, 10);
		this.textBoxFilter.setText(tile.filter);

		this.buttonList.add(new GuiButton(1, this.xLocation + 125, this.yLocation + 100, 14, 14, "<"));
		this.buttonList.add(new GuiButton(2, this.xLocation + 193, this.yLocation + 100, 14, 14, ">"));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1, par2, par3);
		this.textBoxFilter.drawTextBox();
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
		this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.transmute"), 6, 8, 4210752);
		String emc = String.format(StatCollector.translateToLocal("pe.emc.emc_tooltip_prefix") + " %,d", (int) tile.getStoredEmc());
		this.fontRendererObj.drawString(emc, 6, this.ySize - 94, 4210752);

		if (tile.learnFlag > 0)
		{
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned0"), 98, 30, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned1"), 99, 38, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned2"), 100, 46, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned3"), 101, 54, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned4"), 102, 62, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned5"), 103, 70, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned6"), 104, 78, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned7"), 107, 86, 4210752);
			
			tile.learnFlag--;
		}
	}
	
	@Override
	public void updateScreen() 
	{
		super.updateScreen();
		this.textBoxFilter.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char par1, int par2)
	{
		if (this.textBoxFilter.isFocused()) 
		{
			this.textBoxFilter.textboxKeyTyped(par1, par2);

			String srch = this.textBoxFilter.getText().toLowerCase();

			if (!tile.filter.equals(srch)) 
			{
				PacketHandler.sendToServer(new SearchUpdatePKT(srch, 0));
				tile.filter = srch;
				tile.searchpage = 0;
				tile.updateOutputs();
			}
		}

		if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode() && !this.textBoxFilter.isFocused())
		{
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton)
	{
		super.mouseClicked(x, y, mouseButton);

		int minX = textBoxFilter.xPosition;
		int minY = textBoxFilter.yPosition;
		int maxX = minX + textBoxFilter.width;
		int maxY = minY + textBoxFilter.height;

		if (mouseButton == 1 && x >= minX && x <= maxX && y <= maxY)
		{
			PacketHandler.sendToServer(new SearchUpdatePKT("", 0));
			tile.filter = "";
			tile.searchpage = 0;
			tile.updateOutputs();
			this.textBoxFilter.setText("");
		}

		this.textBoxFilter.mouseClicked(x, y, mouseButton);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		tile.learnFlag = 0;
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		String srch = this.textBoxFilter.getText().toLowerCase();

		if (button.id == 1)
		{
			if (tile.searchpage != 0)
			{
				tile.searchpage--;
			}
		}
		else if (button.id == 2)
		{
			if (!(tile.knowledge.size() <= 12))
			{
				tile.searchpage++;
			}
		}
		PacketHandler.sendToServer(new SearchUpdatePKT(srch, tile.searchpage));
		tile.filter = srch;
		tile.updateOutputs();
	}
}
