package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUICollectorMK3 extends ContainerScreen<CollectorMK3Container> {

	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID, "textures/gui/collector3.png");

	public GUICollectorMK3(CollectorMK3Container container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.xSize = 218;
		this.ySize = 165;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) {
		this.font.drawString(Long.toString(container.emc.get()), 91, 32, 0x404040);

		long kleinCharge = container.kleinEmc.get();
		if (kleinCharge > 0) {
			this.font.drawString(Constants.EMC_FORMATTER.format(kleinCharge), 91, 44, 0x404040);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);

		this.blit(guiLeft, guiTop, 0, 0, xSize, ySize);

		//Light Level. Max is 12
		int progress = (int) (container.sunLevel.get() * 12.0 / 16);

		this.blit(guiLeft + 160, guiTop + 49 - progress, 220, 13 - progress, 12, progress);

		//EMC storage. Max is 48
		this.blit(guiLeft + 98, guiTop + 18, 0, 166, (int) (container.emc.get() / container.tile.getMaximumEmc() * 48), 10);

		//Klein Star Charge Progress. Max is 48
		progress = (int) (container.getKleinChargeProgress() * 48);
		this.blit(guiLeft + 98, guiTop + 58, 0, 166, progress, 10);

		//Fuel Progress. Max is 24.
		progress = (int) (container.getFuelProgress() * 24);
		this.blit(guiLeft + 172, guiTop + 55 - progress, 219, 38 - progress, 10, progress + 1);
	}
}