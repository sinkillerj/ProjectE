package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmuteTabletContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiTextField;

import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GUITransmuteTablet extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/transmute.png");
	TransmuteTabletInventory table;
	private GuiTextField textBoxFilter;

	int xLocation;
	int yLocation;

	public GUITransmuteTablet(InventoryPlayer invPlayer, TransmuteTabletInventory inventory) 
	{
		super(new TransmuteTabletContainer(invPlayer, inventory));
		this.table = inventory;
		this.xSize = 228;
		this.ySize = 196;
	}
	
	@Override
	public void initGui() 
	{
		table.setPlayer(Minecraft.getMinecraft().thePlayer);
		super.initGui();

		this.xLocation = (this.width - this.xSize) / 2;
		this.yLocation = (this.height - this.ySize) / 2;

		this.textBoxFilter = new GuiTextField(this.fontRendererObj, this.xLocation + 88, this.yLocation + 8, 45, 10);
		this.textBoxFilter.setText(table.filter);
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
		String emc = String.format(StatCollector.translateToLocal("pe.emc_emc_tooltip_prefix") + " %,d", (int) table.emc);
		this.fontRendererObj.drawString(emc, 6, this.ySize - 94, 4210752);

		if (table.learnFlag > 0)
		{
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned0"), 98, 30, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned1"), 99, 38, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned2"), 100, 46, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned3"), 101, 54, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned4"), 102, 62, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned5"), 103, 70, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned6"), 104, 78, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned7"), 107, 86, 4210752);
			
			table.learnFlag--;
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

			if (!table.filter.equals(srch)) 
			{
				PacketHandler.sendToServer(new SearchUpdatePKT(srch));
				table.filter = srch;
				table.updateOutputs();
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
			PacketHandler.sendToServer(new SearchUpdatePKT(""));
			table.filter = "";
			table.updateOutputs();
			this.textBoxFilter.setText("");
		}

		this.textBoxFilter.mouseClicked(x, y, mouseButton);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		table.learnFlag = 0;
	}
}
