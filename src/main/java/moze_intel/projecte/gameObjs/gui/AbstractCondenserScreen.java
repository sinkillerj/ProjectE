package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractCondenserScreen<T extends CondenserContainer> extends PEContainerScreen<T> {

	public AbstractCondenserScreen(T condenser, PlayerInventory playerInventory, ITextComponent title) {
		super(condenser, playerInventory, title);
		this.imageWidth = 255;
		this.imageHeight = 233;
	}

	protected abstract ResourceLocation getTexture();

	@Override
	protected void renderBg(@Nonnull MatrixStack matrix, float partialTicks, int x, int y) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bind(getTexture());

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int progress = menu.getProgressScaled();
		blit(matrix, leftPos + 33, topPos + 10, 0, 235, progress, 10);
	}

	@Override
	protected void renderLabels(@Nonnull MatrixStack matrix, int x, int y) {
		//Don't render title or inventory as we don't have space
		long toDisplay = Math.min(menu.displayEmc.get(), menu.requiredEmc.get());
		ITextComponent emc = TransmutationEMCFormatter.formatEMC(toDisplay);
		this.font.draw(matrix, emc, 140, 10, 0x404040);
	}

	@Override
	protected void renderTooltip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
		long toDisplay = Math.min(menu.displayEmc.get(), menu.requiredEmc.get());

		if (toDisplay < 1e12) {
			super.renderTooltip(matrix, mouseX, mouseY);
			return;
		}

		int emcLeft = 140 + leftPos;
		int emcRight = emcLeft + 110;
		int emcTop = 6 + topPos;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			renderTooltip(matrix, PELang.EMC_TOOLTIP.translate(Constants.EMC_FORMATTER.format(toDisplay)), mouseX, mouseY);
		} else {
			super.renderTooltip(matrix, mouseX, mouseY);
		}
	}

	public static class MK1 extends AbstractCondenserScreen<CondenserContainer> {

		public MK1(CondenserContainer condenser, PlayerInventory playerInventory, ITextComponent title) {
			super(condenser, playerInventory, title);
		}

		@Override
		protected ResourceLocation getTexture() {
			return PECore.rl("textures/gui/condenser.png");
		}
	}

	public static class MK2 extends AbstractCondenserScreen<CondenserMK2Container> {

		public MK2(CondenserMK2Container condenser, PlayerInventory playerInventory, ITextComponent title) {
			super(condenser, playerInventory, title);
		}

		@Override
		protected ResourceLocation getTexture() {
			return PECore.rl("textures/gui/condenser_mk2.png");
		}
	}
}