package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RelayMK3Container;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIRelayMK3 extends ContainerScreen<RelayMK3Container>
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/relay3.png");

	public GUIRelayMK3(RelayMK3Container container, PlayerInventory invPlayer, ITextComponent title)
	{
		super(container, invPlayer, title);
		this.xSize = 212;
		this.ySize = 194;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2)
	{
		this.font.drawString(I18n.format("pe.relay.mk3"), 38, 6, 4210752);
		this.font.drawString(Constants.EMC_FORMATTER.format(container.emc.get()), 125, 39, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.blit(x, y, 0, 0, xSize, ySize);
		
		//Emc bar progress
		int progress = (int) ((double) container.emc.get() / container.tile.getMaximumEmc() * 102);
		this.blit(x + 105, y + 6, 30, 195, progress, 10);

		//Klein start bar progress. Max is 30.
		progress = (int) (container.getKleinChargeProgress() * 30);
		this.blit(x + 153, y + 82, 0, 195, progress, 10);
				
		//Burn Slot bar progress. Max is 30.
		progress = (int) (container.getInputBurnProgress() * 30);
		blit(x + 101, y + 82, 0, 195, progress, 10);
	}	
}
