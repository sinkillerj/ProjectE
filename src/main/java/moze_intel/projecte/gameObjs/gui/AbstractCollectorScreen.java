package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCollectorScreen<T extends CollectorMK1Container> extends PEContainerScreen<T> {

	public AbstractCollectorScreen(T container, Inventory invPlayer, Component title, int imageWidth, int imageHeight) {
		super(container, invPlayer, title, imageWidth, imageHeight);
	}

	protected abstract Identifier getTexture();

	protected int getBonusXShift() {
		return 0;
	}

	protected int getTextureBonusXShift() {
		return 0;
	}

	protected void peExtractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, Long.toString(menu.emc.get()), 60 + getBonusXShift(), 32, PEGuiGraphics.LABEL_COLOR, false);
		long kleinCharge = menu.kleinEmc.get();
		if (kleinCharge > 0) {
			graphics.text(font, EMCHelper.formatEmc(kleinCharge), 60 + getBonusXShift(), 44, PEGuiGraphics.LABEL_COLOR, false);
		}
	}

	protected void peExtractBackground(@NotNull GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
		PEGuiGraphics.blit(graphics, getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int progress = (int) (menu.sunLevel.get() * 12.0 / 16);
		PEGuiGraphics.blit(graphics, getTexture(), leftPos + 126 + getBonusXShift(), topPos + 49 - progress, 177 + getTextureBonusXShift(), 13 - progress, 12, progress);

		PEGuiGraphics.blit(graphics, getTexture(), leftPos + 64 + getBonusXShift(), topPos + 18, 0, 166, (int) ((double) menu.emc.get() / menu.collector.getMaximumEmc() * 48), 10);

		progress = (int) (menu.getKleinChargeProgress() * 48);
		PEGuiGraphics.blit(graphics, getTexture(), leftPos + 64 + getBonusXShift(), topPos + 58, 0, 166, progress, 10);

		progress = (int) (menu.getFuelProgress() * 24);
		PEGuiGraphics.blit(graphics, getTexture(), leftPos + 138 + getBonusXShift(), topPos + 55 - progress, 176 + getTextureBonusXShift(), 38 - progress, 10, progress + 1);
	}

	public static class MK1 extends AbstractCollectorScreen<CollectorMK1Container> {

		public MK1(CollectorMK1Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title, 184, 165);
		}

		@Override
		protected Identifier getTexture() {
			return PECore.rl("textures/gui/collector1.png");
		}
	}

	public static class MK2 extends AbstractCollectorScreen<CollectorMK2Container> {

		public MK2(CollectorMK2Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title, 200, 165);
		}

		@Override
		protected Identifier getTexture() {
			return PECore.rl("textures/gui/collector2.png");
		}

		@Override
		protected int getBonusXShift() {
			return 16;
		}

		@Override
		protected int getTextureBonusXShift() {
			return 25;
		}
	}

	public static class MK3 extends AbstractCollectorScreen<CollectorMK3Container> {

		public MK3(CollectorMK3Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title, 218, 165);
		}

		@Override
		protected Identifier getTexture() {
			return PECore.rl("textures/gui/collector3.png");
		}

		@Override
		protected int getBonusXShift() {
			return 34;
		}

		@Override
		protected int getTextureBonusXShift() {
			return 43;
		}
	}
}
