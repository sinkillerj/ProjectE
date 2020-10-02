package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RelayMK2Container;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIRelayMK2 extends PEContainerScreen<RelayMK2Container> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/relay2.png");

	public GUIRelayMK2(RelayMK2Container container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.xSize = 193;
		this.ySize = 182;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int x, int y) {
		this.font.func_243248_b(matrix, PELang.GUI_RELAY_MK2.translate(), 28, 6, 0x404040);
		this.font.drawString(matrix, Constants.EMC_FORMATTER.format(container.emc.get()), 107, 25, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float partialTicks, int x, int y) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);

		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);

		//Emc bar progress
		int progress = (int) ((double) container.emc.get() / container.tile.getMaximumEmc() * Constants.MAX_CONDENSER_PROGRESS);
		blit(matrix, guiLeft + 86, guiTop + 6, 30, 183, progress, 10);

		//Klein start bar progress. Max is 30.
		progress = (int) (container.getKleinChargeProgress() * 30);
		blit(matrix, guiLeft + 133, guiTop + 68, 0, 183, progress, 10);

		//Burn Slot bar progress. Max is 30.
		progress = (int) (container.getInputBurnProgress() * 30);
		blit(matrix, guiLeft + 81, guiTop + 68, 0, 183, progress, 10);
	}
}