package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.RelayMK2Container;
import moze_intel.projecte.gameObjs.container.RelayMK3Container;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GUIRelay<CONTAINER extends RelayMK1Container> extends PEContainerScreen<CONTAINER> {

	private final Identifier texture;
	private final int emcX;
	private final int emcY;
	private final int vOffset;
	private final int emcBarShift;
	private final int shiftX;
	private final int shiftY;

	protected GUIRelay(CONTAINER container, Inventory invPlayer, Component title, Identifier texture, int emcX, int emcY, int vOffset,
			int emcBarShift, int shiftX, int shiftY, int imageWidth, int imageHeight) {
		super(container, invPlayer, title, imageWidth, imageHeight);
		this.texture = texture;
		this.emcX = emcX;
		this.emcY = emcY;
		this.vOffset = vOffset;
		this.emcBarShift = emcBarShift;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
	}

	@Override
	protected void peExtractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX, titleLabelY, PEGuiGraphics.LABEL_COLOR, false);
		graphics.text(font, EMCHelper.formatEmc(menu.emc.get()), emcX, emcY, PEGuiGraphics.LABEL_COLOR, false);
	}

	@Override
	protected void peExtractBackground(@NotNull GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
		PEGuiGraphics.blit(graphics, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		//Emc bar progress
		int progress = (int) ((double) menu.emc.get() / menu.relay.getMaximumEmc() * CondenserContainer.MAX_PROGRESS);
		PEGuiGraphics.blit(graphics, texture, leftPos + emcBarShift, topPos + 6, 30, vOffset, progress, 10);

		//Klein start bar progress. Max is 30.
		progress = (int) (menu.getKleinChargeProgress() * 30);
		PEGuiGraphics.blit(graphics, texture, leftPos + 116 + shiftX, topPos + 67 + shiftY, 0, vOffset, progress, 10);

		//Burn Slot bar progress. Max is 30.
		progress = (int) (menu.getInputBurnProgress() * 30);
		PEGuiGraphics.blit(graphics, texture, leftPos + 64 + shiftX, topPos + 67 + shiftY, 0, vOffset, progress, 10);
	}

	public static class GUIRelayMK1 extends GUIRelay<RelayMK1Container> {

		private static final Identifier MK1_TEXTURE = PECore.rl("textures/gui/relay1.png");

		public GUIRelayMK1(RelayMK1Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title, MK1_TEXTURE, 88, 24, 177, 64, 0, 0, 175, 176);
			this.titleLabelX = 10;
		}
	}

	public static class GUIRelayMK2 extends GUIRelay<RelayMK2Container> {

		private static final Identifier MK2_TEXTURE = PECore.rl("textures/gui/relay2.png");

		public GUIRelayMK2(RelayMK2Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title, MK2_TEXTURE, 107, 25, 183, 86, 17, 1, 193, 182);
			this.titleLabelX = 28;
		}
	}

	public static class GUIRelayMK3 extends GUIRelay<RelayMK3Container> {

		private static final Identifier MK3_TEXTURE = PECore.rl("textures/gui/relay3.png");

		public GUIRelayMK3(RelayMK3Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title, MK3_TEXTURE, 125, 39, 195, 105, 37, 15, 212, 194);
			this.titleLabelX = 38;
		}
	}
}
