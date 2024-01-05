package moze_intel.projecte.gameObjs.gui;

import java.math.BigInteger;
import java.util.Locale;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class GUITransmutation extends PEContainerScreen<TransmutationContainer> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/transmute.png");
	private final TransmutationInventory inv;
	private EditBox textBoxFilter;

	public GUITransmutation(TransmutationContainer container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title);
		this.inv = container.transmutationInventory;
		this.imageWidth = 228;
		this.imageHeight = 196;
		this.titleLabelX = 6;
		this.titleLabelY = 8;
	}

	@Override
	public void init() {
		super.init();

		this.textBoxFilter = addWidget(new EditBox(this.font, leftPos + 88, topPos + 8, 45, 10, Component.empty()));
		this.textBoxFilter.setValue(inv.filter);
		this.textBoxFilter.setResponder(this::updateFilter);

		addRenderableWidget(Button.builder(Component.literal("<"), b -> {
					if (inv.searchpage != 0) {
						inv.searchpage--;
					}
					inv.filter = textBoxFilter.getValue().toLowerCase(Locale.ROOT);
					inv.updateClientTargets();
				}).pos(leftPos + 125, topPos + 100)
				.size(14, 14)
				.build());
		addRenderableWidget(Button.builder(Component.literal(">"), b -> {
					if (inv.getKnowledgeSize() > 12) {
						inv.searchpage++;
					}
					inv.filter = textBoxFilter.getValue().toLowerCase(Locale.ROOT);
					inv.updateClientTargets();
				}).pos(leftPos + 193, topPos + 100)
				.size(14, 14)
				.build());
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		this.textBoxFilter.render(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
		graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
		//Don't render inventory as we don't have space
		BigInteger emcAmount = inv.getAvailableEmc();
		graphics.drawString(font, PELang.EMC_TOOLTIP.translate(""), 6, this.imageHeight - 104, 0x404040, false);
		Component emc = TransmutationEMCFormatter.formatEMC(emcAmount);
		graphics.drawString(font, emc, 6, this.imageHeight - 94, 0x404040, false);

		if (inv.learnFlag > 0) {
			graphics.drawString(font, PELang.TRANSMUTATION_LEARNED_1.translate(), 98, 30, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_LEARNED_2.translate(), 99, 38, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_LEARNED_3.translate(), 100, 46, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_LEARNED_4.translate(), 101, 54, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_LEARNED_5.translate(), 102, 62, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_LEARNED_6.translate(), 103, 70, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_LEARNED_7.translate(), 104, 78, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_LEARNED_8.translate(), 107, 86, 0x404040, false);

			inv.learnFlag--;
		}

		if (inv.unlearnFlag > 0) {
			graphics.drawString(font, PELang.TRANSMUTATION_UNLEARNED_1.translate(), 97, 22, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_UNLEARNED_2.translate(), 98, 30, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_UNLEARNED_3.translate(), 99, 38, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_UNLEARNED_4.translate(), 100, 46, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_UNLEARNED_5.translate(), 101, 54, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_UNLEARNED_6.translate(), 102, 62, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_UNLEARNED_7.translate(), 103, 70, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_UNLEARNED_8.translate(), 104, 78, 0x404040, false);
			graphics.drawString(font, PELang.TRANSMUTATION_UNLEARNED_9.translate(), 107, 86, 0x404040, false);

			inv.unlearnFlag--;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textBoxFilter.isFocused()) {
			//Manually make it so that hitting escape when the filter is focused will exit the focus
			if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
				textBoxFilter.setFocused(false);
				return true;
			}
			//Otherwise have it handle the key press
			//This is where key combos and deletion is handled, and where we bypass the inventory key closing the screen
			return textBoxFilter.keyPressed(keyCode, scanCode, modifiers);
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private void updateFilter(String text) {
		String search = text.toLowerCase(Locale.ROOT);
		if (!inv.filter.equals(search)) {
			inv.filter = search;
			inv.searchpage = 0;
			inv.updateClientTargets();
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseButton) {
		if (textBoxFilter.isMouseOver(x, y)) {
			if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				//Note: Clearing filter will be handled by the text box's responder
				this.textBoxFilter.setValue("");
			}
		} else if (textBoxFilter.isFocused()) {
			if (hoveredSlot == null || (!hoveredSlot.hasItem() && menu.getCarried().isEmpty())) {
				textBoxFilter.setFocused(false);
			}
		}
		return super.mouseClicked(x, y, mouseButton);
	}

	@Override
	public void removed() {
		super.removed();
		inv.learnFlag = 0;
		inv.unlearnFlag = 0;
	}

	@Override
	protected void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
		BigInteger emcAmount = inv.getAvailableEmc();

		if (emcAmount.compareTo(Constants.MAX_EXACT_TRANSMUTATION_DISPLAY) < 0) {
			super.renderTooltip(graphics, mouseX, mouseY);
			return;
		}

		int emcLeft = leftPos;
		int emcRight = emcLeft + 82;
		int emcTop = 95 + topPos;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			setTooltipForNextRenderPass(PELang.EMC_TOOLTIP.translate(Constants.EMC_FORMATTER.format(emcAmount)));
		} else {
			super.renderTooltip(graphics, mouseX, mouseY);
		}
	}
}