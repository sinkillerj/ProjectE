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

	protected final T container;

	public AbstractCondenserScreen(T condenser, PlayerInventory playerInventory, ITextComponent title) {
		super(condenser, playerInventory, title);
		this.container = condenser;
		this.xSize = 255;
		this.ySize = 233;
	}

	protected abstract ResourceLocation getTexture();

	@Override
	protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float var1, int var2, int var3) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(getTexture());

		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);

		int progress = container.getProgressScaled();
		blit(matrix, guiLeft + 33, guiTop + 10, 0, 235, progress, 10);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int var1, int var2) {
		long toDisplay = Math.min(container.displayEmc.get(), container.requiredEmc.get());
		ITextComponent emc = TransmutationEMCFormatter.formatEMC(toDisplay);
		this.font.func_243248_b(matrix, emc, 140, 10, 0x404040);
	}

	@Override
	protected void renderHoveredTooltip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
		long toDisplay = Math.min(container.displayEmc.get(), container.requiredEmc.get());

		if (toDisplay < 1e12) {
			super.renderHoveredTooltip(matrix, mouseX, mouseY);
			return;
		}

		int emcLeft = 140 + guiLeft;
		int emcRight = emcLeft + 110;
		int emcTop = 6 + guiTop;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			renderTooltip(matrix, PELang.EMC_TOOLTIP.translate(Constants.EMC_FORMATTER.format(toDisplay)), mouseX, mouseY);
		} else {
			super.renderHoveredTooltip(matrix, mouseX, mouseY);
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