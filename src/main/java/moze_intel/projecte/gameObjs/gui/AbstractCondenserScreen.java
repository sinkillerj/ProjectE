package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
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
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(getTexture());

		blit(guiLeft, guiTop, 0, 0, xSize, ySize);

		int progress = container.getProgressScaled();
		blit(guiLeft + 33, guiTop + 10, 0, 235, progress, 10);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) {
		long toDisplay = Math.min(container.displayEmc.get(), container.requiredEmc.get());
		String emc = TransmutationEMCFormatter.formatEMC(toDisplay);
		this.font.drawString(emc, 140, 10, 0x404040);
	}

	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		long toDisplay = Math.min(container.displayEmc.get(), container.requiredEmc.get());

		if (toDisplay < 1e12) {
			super.renderHoveredToolTip(mouseX, mouseY);
			return;
		}

		int emcLeft = 140 + guiLeft;
		int emcRight = emcLeft + 110;
		int emcTop = 6 + guiTop;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			String emcAsString = I18n.format("pe.emc.emc_tooltip_prefix") + " " + Constants.EMC_FORMATTER.format(toDisplay);
			renderTooltip(Collections.singletonList(emcAsString), mouseX, mouseY);
		} else {
			super.renderHoveredToolTip(mouseX, mouseY);
		}
	}

	public static class MK1 extends AbstractCondenserScreen<CondenserContainer> {

		public MK1(CondenserContainer condenser, PlayerInventory playerInventory, ITextComponent title) {
			super(condenser, playerInventory, title);
		}

		@Override
		protected ResourceLocation getTexture() {
			return new ResourceLocation(PECore.MODID, "textures/gui/condenser.png");
		}
	}

	public static class MK2 extends AbstractCondenserScreen<CondenserMK2Container> {

		public MK2(CondenserMK2Container condenser, PlayerInventory playerInventory, ITextComponent title) {
			super(condenser, playerInventory, title);
		}

		@Override
		protected ResourceLocation getTexture() {
			return new ResourceLocation(PECore.MODID, "textures/gui/condenser_mk2.png");
		}
	}
}