package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.gameObjs.gui.PEGuiGraphics;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCondenserScreen<T extends CondenserContainer> extends PEContainerScreen<T> {

	public AbstractCondenserScreen(T condenser, Inventory playerInventory, Component title) {
		super(condenser, playerInventory, title, 255, 233);
	}

	protected int getPlayerInventoryLabelX() {
		return 48;
	}

	protected int getPlayerInventoryLabelY() {
		return 142;
	}

	@Override
	protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, (imageWidth - font.width(title)) / 2, 4, PEGuiGraphics.LABEL_COLOR, false);
		graphics.text(font, playerInventoryTitle, getPlayerInventoryLabelX(), getPlayerInventoryLabelY(), PEGuiGraphics.LABEL_COLOR, false);
		peExtractLabels(graphics, mouseX, mouseY);
	}

	protected abstract Identifier getTexture();

	@Override
	protected void peExtractBackground(@NotNull GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
		PEGuiGraphics.blit(graphics, getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int progress = menu.getProgressScaled();
		PEGuiGraphics.blit(graphics, getTexture(), leftPos + 33, topPos + 10, 0, 235, progress, 10);
	}

	@Override
	protected void peExtractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		long display = menu.displayEmc.get();
		long required = menu.requiredEmc.get();
		long toDisplay = required > 0 ? Math.min(display, required) : display;
		Component emc = TransmutationEMCFormatter.formatEMC(toDisplay);
		graphics.text(font, emc, 140, 10, PEGuiGraphics.LABEL_COLOR, false);
	}

	@Override
	protected void extractTooltip(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		long display = menu.displayEmc.get();
		long required = menu.requiredEmc.get();
		long toDisplay = required > 0 ? Math.min(display, required) : display;

		if (toDisplay < 1e12) {
			super.extractTooltip(graphics, mouseX, mouseY);
			return;
		}

		int emcLeft = 140 + leftPos;
		int emcRight = emcLeft + 110;
		int emcTop = 6 + topPos;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			graphics.setTooltipForNextFrame(font, PELang.EMC_TOOLTIP.translate(EMCHelper.formatEmc(toDisplay)), mouseX, mouseY);
		} else {
			super.extractTooltip(graphics, mouseX, mouseY);
		}
	}

	public static class MK1 extends AbstractCondenserScreen<CondenserContainer> {

		public MK1(CondenserContainer condenser, Inventory playerInventory, Component title) {
			super(condenser, playerInventory, title);
		}

		@Override
		protected Identifier getTexture() {
			return PECore.rl("textures/gui/condenser.png");
		}
	}

	public static class MK2 extends AbstractCondenserScreen<CondenserMK2Container> {

		public MK2(CondenserMK2Container condenser, Inventory playerInventory, Component title) {
			super(condenser, playerInventory, title);
		}

		@Override
		protected Identifier getTexture() {
			return PECore.rl("textures/gui/condenser_mk2.png");
		}
	}
}
