package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GUICollectorMK1 extends ContainerScreen<CollectorMK1Container>
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/collector1.png");
	private final CollectorMK1Tile tile;

	public GUICollectorMK1(PlayerInventory invPlayer, CollectorMK1Tile tile)
	{
		super(new CollectorMK1Container(invPlayer, tile));
		this.tile = tile;
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
		this.font.drawString(Long.toString(container.emc), 60, 32, 4210752);
		
		double kleinCharge = container.kleinEmc;

		if (kleinCharge > 0)
			this.font.drawString(Constants.EMC_FORMATTER.format(kleinCharge), 60, 44, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.blit(x, y, 0, 0, xSize, ySize);
		
		//Light Level. Max is 12
		int progress = (int) (container.sunLevel * 12.0 / 16);
		this.blit(x + 126, y + 49 - progress, 177, 13 - progress, 12, progress);
		
		//EMC storage. Max is 48
		this.blit(x + 64, y + 18, 0, 166, (int) (container.emc / tile.getMaximumEmc() * 48), 10);
		
		//Klein Star Charge Progress. Max is 48
		progress = (int) (container.kleinChargeProgress * 48);
		this.blit(x + 64, y + 58, 0, 166, progress, 10);
		
		//Fuel Progress. Max is 24.
		progress = (int) (container.fuelProgress * 24);
		this.blit(x + 138, y + 55 - progress, 176, 38 - progress, 10, progress + 1);
	}
}
