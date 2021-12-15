package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractCollectorScreen<T extends CollectorMK1Container> extends PEContainerScreen<T> {

	public AbstractCollectorScreen(T container, PlayerInventory invPlayer, ITextComponent title) {
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
	protected void renderLabels(@Nonnull MatrixStack matrix, int x, int y) {
		//Don't render title or inventory as we don't have space
		this.font.draw(matrix, Long.toString(menu.emc.get()), 60 + getBonusXShift(), 32, 0x404040);
		long kleinCharge = menu.kleinEmc.get();
		if (kleinCharge > 0) {
			this.font.draw(matrix, Constants.EMC_FORMATTER.format(kleinCharge), 60 + getBonusXShift(), 44, 0x404040);
		}
	}

	@Override
	protected void renderBg(@Nonnull MatrixStack matrix, float partialTicks, int x, int y) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bind(getTexture());

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		//Light Level. Max is 12
		int progress = (int) (menu.sunLevel.get() * 12.0 / 16);
		blit(matrix, leftPos + 126 + getBonusXShift(), topPos + 49 - progress, 177 + getTextureBonusXShift(), 13 - progress, 12, progress);

		//EMC storage. Max is 48
		blit(matrix, leftPos + 64 + getBonusXShift(), topPos + 18, 0, 166, (int) ((double) menu.emc.get() / menu.tile.getMaximumEmc() * 48), 10);

		//Klein Star Charge Progress. Max is 48
		progress = (int) (menu.getKleinChargeProgress() * 48);
		blit(matrix, leftPos + 64 + getBonusXShift(), topPos + 58, 0, 166, progress, 10);

		//Fuel Progress. Max is 24.
		progress = (int) (menu.getFuelProgress() * 24);
		blit(matrix, leftPos + 138 + getBonusXShift(), topPos + 55 - progress, 176 + getTextureBonusXShift(), 38 - progress, 10, progress + 1);
	}

	public static class MK1 extends AbstractCollectorScreen<CollectorMK1Container> {

		public MK1(CollectorMK1Container container, PlayerInventory invPlayer, ITextComponent title) {
			super(container, invPlayer, title);
		}

		@Override
		protected ResourceLocation getTexture() {
			return PECore.rl("textures/gui/collector1.png");
		}
	}

	public static class MK2 extends AbstractCollectorScreen<CollectorMK2Container> {

		public MK2(CollectorMK2Container container, PlayerInventory invPlayer, ITextComponent title) {
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

		public MK3(CollectorMK3Container container, PlayerInventory invPlayer, ITextComponent title) {
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