package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmuteTabletContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import moze_intel.projecte.utils.NeiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GUITransmuteTablet extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/transmute.png");
	TransmuteTabletInventory table;

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
		NeiHelper.resetSearchBar();
		super.initGui();
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
		this.fontRendererObj.drawString("Transmutation", 16, 8, 4210752);
		String emc = String.format("EMC: %,d", (int) table.emc); 
		this.fontRendererObj.drawString(emc, 6, this.ySize - 94, 4210752);
		
		if (table.learnFlag > 0)
		{
			this.fontRendererObj.drawString("L", 98, 30, 4210752);
			this.fontRendererObj.drawString("e", 99, 38, 4210752);
			this.fontRendererObj.drawString("a", 100, 46, 4210752);
			this.fontRendererObj.drawString("r", 101, 54, 4210752);
			this.fontRendererObj.drawString("n", 102, 62, 4210752);
			this.fontRendererObj.drawString("e", 103, 70, 4210752);
			this.fontRendererObj.drawString("d", 104, 78, 4210752);
			this.fontRendererObj.drawString("!", 107, 86, 4210752);
			
			table.learnFlag--;
		}
	}
	
	@Override
	public void updateScreen() 
	{
		if (NeiHelper.haveNei) 
		{
			String srch = NeiHelper.getSearchText();
			
			if (!table.filter.equals(srch)) 
			{
				PacketHandler.sendToServer(new SearchUpdatePKT(srch));
				table.filter = srch.toLowerCase();
				table.updateOutputs();
			}
		}
		
		super.updateScreen();
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		table.learnFlag = 0;
	}
}
