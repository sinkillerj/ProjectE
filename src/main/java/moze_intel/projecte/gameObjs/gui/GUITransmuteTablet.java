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
		this.ySize = 202;
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
		this.fontRendererObj.drawString("Transmutation", 28, 6, 4210752);
		String emc = String.format("EMC: %,d", (int) table.emc); 
		this.fontRendererObj.drawString(emc, 6, this.ySize - 96, 4210752);
		
		if (table.learnFlag > 0)
		{
			this.fontRendererObj.drawString("L", 98, 36, 4210752);
			this.fontRendererObj.drawString("e", 99, 44, 4210752);
			this.fontRendererObj.drawString("a", 100, 52, 4210752);
			this.fontRendererObj.drawString("r", 101, 60, 4210752);
			this.fontRendererObj.drawString("n", 102, 68, 4210752);
			this.fontRendererObj.drawString("e", 103, 76, 4210752);
			this.fontRendererObj.drawString("d", 104, 84, 4210752);
			this.fontRendererObj.drawString("!", 107, 92, 4210752);
			
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
