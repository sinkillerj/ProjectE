package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.RelayMK2Container;
import moze_intel.projecte.gameObjs.container.RelayMK3Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GUIRelay<CONTAINER extends RelayMK1Container> extends PEContainerScreen<CONTAINER> {

	private final ResourceLocation texture;
	private final int emcX;
	private final int emcY;
	private final int vOffset;
	private final int emcBarShift;
	private final int shiftX;
	private final int shiftY;

	protected GUIRelay(CONTAINER container, Inventory invPlayer, Component title, ResourceLocation texture, int emcX, int emcY, int vOffset,
			int emcBarShift, int shiftX, int shiftY) {
		super(container, invPlayer, title);
		this.texture = texture;
		this.emcX = emcX;
		this.emcY = emcY;
		this.vOffset = vOffset;
		this.emcBarShift = emcBarShift;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
	}

	@Override
	protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
		graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
		//Don't render inventory as we don't have space
		graphics.drawString(font, Constants.EMC_FORMATTER.format(menu.emc.get()), emcX, emcY, 0x404040, false);
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
		graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		//Emc bar progress
		int progress = (int) ((double) menu.emc.get() / menu.relay.getMaximumEmc() * Constants.MAX_CONDENSER_PROGRESS);
		graphics.blit(texture, leftPos + emcBarShift, topPos + 6, 30, vOffset, progress, 10);

		//Klein start bar progress. Max is 30.
		progress = (int) (menu.getKleinChargeProgress() * 30);
		graphics.blit(texture, leftPos + 116 + shiftX, topPos + 67 + shiftY, 0, vOffset, progress, 10);

		//Burn Slot bar progress. Max is 30.
		progress = (int) (menu.getInputBurnProgress() * 30);
		graphics.blit(texture, leftPos + 64 + shiftX, topPos + 67 + shiftY, 0, vOffset, progress, 10);
	}

	public static class GUIRelayMK1 extends GUIRelay<RelayMK1Container> {

		private static final ResourceLocation MK1_TEXTURE = PECore.rl("textures/gui/relay1.png");

		public GUIRelayMK1(RelayMK1Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title, MK1_TEXTURE, 88, 24, 177, 64, 0, 0);
			this.imageWidth = 175;
			this.imageHeight = 176;
			this.titleLabelX = 10;
		}
	}

	public static class GUIRelayMK2 extends GUIRelay<RelayMK2Container> {

		private static final ResourceLocation MK2_TEXTURE = PECore.rl("textures/gui/relay2.png");

		public GUIRelayMK2(RelayMK2Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title, MK2_TEXTURE, 107, 25, 183, 86, 17, 1);
			this.imageWidth = 193;
			this.imageHeight = 182;
			this.titleLabelX = 28;
		}
	}

	public static class GUIRelayMK3 extends GUIRelay<RelayMK3Container> {

		private static final ResourceLocation MK3_TEXTURE = PECore.rl("textures/gui/relay3.png");

		public GUIRelayMK3(RelayMK3Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title, MK3_TEXTURE, 125, 39, 195, 105, 37, 15);
			this.imageWidth = 212;
			this.imageHeight = 194;
			this.titleLabelX = 38;
		}
	}
}