package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIRelayMK1 extends PEContainerScreen<RelayMK1Container> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/relay1.png");

	public GUIRelayMK1(RelayMK1Container container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.xSize = 175;
		this.ySize = 176;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int var1, int var2) {
		this.font.func_243248_b(matrix, PELang.GUI_RELAY_MK1.translate(), 10, 6, 0x404040);
		this.font.drawString(matrix, Constants.EMC_FORMATTER.format(container.emc.get()), 88, 24, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float var1, int var2, int var3) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);

		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);

		//Emc bar progress
		int progress = (int) ((double) container.emc.get() / container.tile.getMaximumEmc() * Constants.MAX_CONDENSER_PROGRESS);
		blit(matrix, guiLeft + 64, guiTop + 6, 30, 177, progress, 10);

		//Klein start bar progress. Max is 30.
		progress = (int) (container.getKleinChargeProgress() * 30);
		blit(matrix, guiLeft + 116, guiTop + 67, 0, 177, progress, 10);

		//Burn Slot bar progress. Max is 30.
		progress = (int) (container.getInputBurnProgress() * 30);
		blit(matrix, guiLeft + 64, guiTop + 67, 0, 177, progress, 10);
	}
}