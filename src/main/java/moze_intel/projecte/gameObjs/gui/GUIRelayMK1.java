package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GUIRelayMK1 extends ContainerScreen<RelayMK1Container>
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/relay1.png");
	private final RelayMK1Tile tile;

	public GUIRelayMK1(PlayerInventory invPlayer, RelayMK1Tile tile)
	{
		super(new RelayMK1Container(invPlayer, tile));
		this.tile = tile;
		this.xSize = 175;
		this.ySize = 176;
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
		this.font.drawString(I18n.format("pe.relay.mk1"), 10, 6, 4210752);
		this.font.drawString(Constants.EMC_FORMATTER.format(container.emc), 88, 24, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.blit(x, y, 0, 0, xSize, ySize);
		
		//Emc bar progress. Max is 102.
		int progress = (int) (container.emc / tile.getMaximumEmc() * 102);
		this.blit(x + 64, y + 6, 30, 177, progress, 10);
		
		//Klein start bar progress. Max is 30.
		progress = (int) (container.kleinChargeProgress * 30);
		this.blit(x + 116, y + 67, 0, 177, progress, 10);
		
		//Burn Slot bar progress. Max is 30.
		progress = (int) (container.inputBurnProgress * 30);
		this.blit(x + 64, y + 67, 0, 177, progress, 10);
	}
}
