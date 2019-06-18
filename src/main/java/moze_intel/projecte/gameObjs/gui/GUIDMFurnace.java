package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GUIDMFurnace extends ContainerScreen
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/dmfurnace.png");
	private final DMFurnaceTile tile;

	public GUIDMFurnace(PlayerInventory invPlayer, DMFurnaceTile tile)
	{
		super(new DMFurnaceContainer(invPlayer, tile));
		this.xSize = 178;
		this.ySize = 165;
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
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.blit(x, y, 0, 0, xSize, ySize);
		
		int progress;
		if (tile.isBurning())
		{
			progress = tile.getBurnTimeRemainingScaled(12);
			this.blit(x + 49, y + 36 + 12 - progress, 179, 12 - progress, 14, progress + 2);
		}
		progress = tile.getCookProgressScaled(24);
		this.blit(x + 73, y + 34, 179, 14, progress + 1, 16);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) 
	{
		this.font.drawString(I18n.format("pe.dmfurnace.shortname"), 57, 5, 4210752);
		this.font.drawString(I18n.format("container.inventory"), 57, ySize - 96 + 2, 4210752);
	}
}
