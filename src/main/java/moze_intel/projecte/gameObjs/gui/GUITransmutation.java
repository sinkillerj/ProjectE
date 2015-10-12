package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
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

import java.util.Locale;

public class GUITransmutation extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/transmute.png");
	TransmutationInventory inv;
	private GuiTextField textBoxFilter;

	int xLocation;
	int yLocation;

	public GUITransmutation(InventoryPlayer invPlayer, TransmutationInventory inventory)
	{
		super(new TransmutationContainer(invPlayer, inventory));
		this.inv = inventory;
		this.xSize = 228;
		this.ySize = 196;
	}
	
	@Override
	public void initGui() 
	{
		super.initGui();

		this.xLocation = (this.width - this.xSize) / 2;
		this.yLocation = (this.height - this.ySize) / 2;

		this.textBoxFilter = new GuiTextField(this.fontRendererObj, this.xLocation + 88, this.yLocation + 8, 45, 10);
		this.textBoxFilter.setText(inv.filter);

		this.buttonList.add(new GuiButton(1, this.xLocation + 125, this.yLocation + 100, 14, 14, "<"));
		this.buttonList.add(new GuiButton(2, this.xLocation + 193, this.yLocation + 100, 14, 14, ">"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		this.textBoxFilter.drawTextBox();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) 
	{
		this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.transmute"), 6, 8, 4210752);
		String emc = String.format(StatCollector.translateToLocal("pe.emc.emc_tooltip_prefix") + " %,d", (int) inv.emc);
		this.fontRendererObj.drawString(emc, 6, this.ySize - 94, 4210752);

		if (inv.learnFlag > 0)
		{
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned0"), 98, 30, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned1"), 99, 38, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned2"), 100, 46, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned3"), 101, 54, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned4"), 102, 62, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned5"), 103, 70, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned6"), 104, 78, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.learned7"), 107, 86, 4210752);
			
			inv.learnFlag--;
		}

		if (inv.unlearnFlag > 0)
		{
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.unlearned0"), 97, 22, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.unlearned1"), 98, 30, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.unlearned2"), 99, 38, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.unlearned3"), 100, 46, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.unlearned4"), 101, 54, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.unlearned5"), 102, 62, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.unlearned6"), 103, 70, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.unlearned7"), 104, 78, 4210752);
			this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.transmutation.unlearned8"), 107, 86, 4210752);
			
			inv.unlearnFlag--;
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

			if (!inv.filter.equals(srch))
			{
				inv.filter = srch;
				inv.searchpage = 0;
				inv.updateOutputs(true);
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
			inv.filter = "";
			inv.searchpage = 0;
			inv.updateOutputs(true);
			this.textBoxFilter.setText("");
		}

		this.textBoxFilter.mouseClicked(x, y, mouseButton);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		inv.learnFlag = 0;
		inv.unlearnFlag = 0;
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		String srch = this.textBoxFilter.getText().toLowerCase(Locale.ROOT);

		if (button.id == 1)
		{
			if (inv.searchpage != 0)
			{
				inv.searchpage--;
			}
		}
		else if (button.id == 2)
		{
			if (!(inv.knowledge.size() <= 12))
			{
				inv.searchpage++;
			}
		}
		inv.filter = srch;
		inv.updateOutputs(true);
	}
}
