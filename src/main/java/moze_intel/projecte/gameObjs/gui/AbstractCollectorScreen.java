package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCollectorScreen<T extends CollectorMK1Container> extends PEContainerScreen<T> {

	public AbstractCollectorScreen(T container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title);
	}

	protected abstract ResourceLocation getTexture();

	protected int getBonusXShift() {
		return 0;
	}

	protected int getTextureBonusXShift() {
		return 0;
	}

	@Override
	protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
		//Don't render title or inventory as we don't have space
		graphics.drawString(font, Long.toString(menu.emc.get()), 60 + getBonusXShift(), 32, 0x404040, false);
		long kleinCharge = menu.kleinEmc.get();
		if (kleinCharge > 0) {
			graphics.drawString(font, Constants.EMC_FORMATTER.format(kleinCharge), 60 + getBonusXShift(), 44, 0x404040, false);
		}
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
		graphics.blit(getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);

		//Light Level. Max is 12
		int progress = (int) (menu.sunLevel.get() * 12.0 / 16);
		graphics.blit(getTexture(), leftPos + 126 + getBonusXShift(), topPos + 49 - progress, 177 + getTextureBonusXShift(), 13 - progress, 12, progress);

		//EMC storage. Max is 48
		graphics.blit(getTexture(), leftPos + 64 + getBonusXShift(), topPos + 18, 0, 166, (int) ((double) menu.emc.get() / menu.collector.getMaximumEmc() * 48), 10);

		//Klein Star Charge Progress. Max is 48
		progress = (int) (menu.getKleinChargeProgress() * 48);
		graphics.blit(getTexture(), leftPos + 64 + getBonusXShift(), topPos + 58, 0, 166, progress, 10);

		//Fuel Progress. Max is 24.
		progress = (int) (menu.getFuelProgress() * 24);
		graphics.blit(getTexture(), leftPos + 138 + getBonusXShift(), topPos + 55 - progress, 176 + getTextureBonusXShift(), 38 - progress, 10, progress + 1);
	}

	public static class MK1 extends AbstractCollectorScreen<CollectorMK1Container> {

		public MK1(CollectorMK1Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title);
		}

		@Override
		protected ResourceLocation getTexture() {
			return PECore.rl("textures/gui/collector1.png");
		}
	}

	public static class MK2 extends AbstractCollectorScreen<CollectorMK2Container> {

		public MK2(CollectorMK2Container container, Inventory invPlayer, Component title) {
			super(container, invPlayer, title);
			this.imageWidth = 200;
			this.imageHeight = 165;
		}

		@Override
		protected ResourceLocation getTexture() {
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
			super(container, invPlayer, title);
			this.imageWidth = 218;
			this.imageHeight = 165;
		}

		@Override
		protected ResourceLocation getTexture() {
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