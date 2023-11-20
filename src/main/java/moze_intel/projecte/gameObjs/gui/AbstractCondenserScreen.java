package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCondenserScreen<T extends CondenserContainer> extends PEContainerScreen<T> {

	public AbstractCondenserScreen(T condenser, Inventory playerInventory, Component title) {
		super(condenser, playerInventory, title);
		this.imageWidth = 255;
		this.imageHeight = 233;
	}

	protected abstract ResourceLocation getTexture();

	@Override
	protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, getTexture());

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int progress = menu.getProgressScaled();
		blit(matrix, leftPos + 33, topPos + 10, 0, 235, progress, 10);
	}

	@Override
	protected void renderLabels(@NotNull PoseStack matrix, int x, int y) {
		//Don't render title or inventory as we don't have space
		switch (ProjectEConfig.client.condenserEmcDisplayMode.get()) {
		//Normal Old Rendering
		default:
			long toDisplay = Math.min(menu.displayEmc.get(), menu.requiredEmc.get());
			Component emc = TransmutationEMCFormatter.formatEMC(toDisplay);
			this.font.draw(matrix, emc, 140, 10, 0x404040);
		break;
			
		//Turns green and shows total value if Stored > Required
		case 1:
			toDisplay = menu.displayEmc.get();
			int displayColor = 0x404040;
			if (toDisplay > menu.requiredEmc.get())
				displayColor = 0x008000;
			emc = TransmutationEMCFormatter.formatEMC(toDisplay);
			this.font.draw(matrix, emc, 140, 10, displayColor);
		break;
		
		case 2:
			toDisplay = Math.min(menu.displayEmc.get(), menu.requiredEmc.get());
			emc = TransmutationEMCFormatter.formatEMC(toDisplay);
			this.font.draw(matrix, emc, 140, 10, 0x404040);
			
			if (menu.displayEmc.get() > menu.requiredEmc.get()) {
				int xOff = 140 + this.font.width(emc);
				emc = TransmutationEMCFormatter.formatEMC(menu.displayEmc.get());
				if ((xOff + this.font.width(" / " + emc.getString())) < this.getXSize()) {
					this.font.draw(matrix, " / " + emc.getString(), xOff, 10, 0x008000);
				} else {
					this.font.draw(matrix, " / Overflow", xOff, 10, 0x008000);
				}
			}
		break;
		}
	}

	@Override
	protected void renderTooltip(@NotNull PoseStack matrix, int mouseX, int mouseY) {
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

		public MK1(CondenserContainer condenser, Inventory playerInventory, Component title) {
			super(condenser, playerInventory, title);
		}

		@Override
		protected ResourceLocation getTexture() {
			return PECore.rl("textures/gui/condenser.png");
		}
	}

	public static class MK2 extends AbstractCondenserScreen<CondenserMK2Container> {

		public MK2(CondenserMK2Container condenser, Inventory playerInventory, Component title) {
			super(condenser, playerInventory, title);
		}

		@Override
		protected ResourceLocation getTexture() {
			return PECore.rl("textures/gui/condenser_mk2.png");
		}
	}
}