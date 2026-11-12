package moze_intel.projecte.gameObjs.gui;
import java.math.BigInteger;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
public class GUITransmutation extends PEContainerScreen<TransmutationContainer> {
	private static final BigInteger MAX_EXACT_TRANSMUTATION_DISPLAY = BigInteger.valueOf(1_000_000_000_000L);
	private static final Identifier texture = PECore.rl("textures/gui/transmute.png");
	private final TransmutationInventory inv;
	private EditBox textBoxFilter;
	private Button previous, next;
	public GUITransmutation(TransmutationContainer container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title, 228, 196);
		this.inv = container.transmutationInventory;
		this.titleLabelX = 6;
		this.titleLabelY = 8;
	}

	@Override
	public void init() {
		super.init();
		this.textBoxFilter = addWidget(new EditBox(this.font, leftPos + 83, topPos + 8, 55, 10, Component.empty()));
		this.textBoxFilter.setResponder(inv::updateFilter);
		previous = addRenderableWidget(Button.builder(Component.literal("<"), b -> inv.previousPage())
				.pos(leftPos + 125, topPos + 100)
				.size(14, 14)
				.build());
		next = addRenderableWidget(Button.builder(Component.literal(">"), b -> inv.nextPage())
				.pos(leftPos + 193, topPos + 100)
				.size(14, 14)
				.build());
		updateButtons();
	}

	private static boolean isShiftDown() {
		return Minecraft.getInstance().hasShiftDown();
	}

	@Override
	public void resize(int width, int height) {
		String filter = this.textBoxFilter.getValue();
		super.resize(width, height);
		this.textBoxFilter.setValue(filter);
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		updateButtons();
	}

	private void updateButtons() {
		if (previous != null) {
			previous.active = inv.hasPreviousPage();
		}

		if (next != null) {
			next.active = inv.hasNextPage();
		}
	}

	@Override
	protected void peExtractBackground(@NotNull GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
		PEGuiGraphics.blit(graphics, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void peExtractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX, titleLabelY, 0xFF404040, false);
		graphics.text(font, PELang.EMC_TOOLTIP.translate(""), 6, this.imageHeight - 104, 0xFF404040, false);
		Component emc = TransmutationEMCFormatter.formatEMC(inv.getAvailableEmc());
		graphics.text(font, emc, 6, this.imageHeight - 94, 0xFF404040, false);
		if (inv.learnFlag > 0) {
			graphics.text(font, PELang.TRANSMUTATION_LEARNED_1.translate(), 98, 30, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_LEARNED_2.translate(), 99, 38, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_LEARNED_3.translate(), 100, 46, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_LEARNED_4.translate(), 101, 54, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_LEARNED_5.translate(), 102, 62, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_LEARNED_6.translate(), 103, 70, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_LEARNED_7.translate(), 104, 78, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_LEARNED_8.translate(), 107, 86, 0xFF404040, false);
			inv.learnFlag--;
		}

		if (inv.unlearnFlag > 0) {
			graphics.text(font, PELang.TRANSMUTATION_UNLEARNED_1.translate(), 97, 22, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_UNLEARNED_2.translate(), 98, 30, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_UNLEARNED_3.translate(), 99, 38, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_UNLEARNED_4.translate(), 100, 46, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_UNLEARNED_5.translate(), 101, 54, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_UNLEARNED_6.translate(), 102, 62, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_UNLEARNED_7.translate(), 103, 70, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_UNLEARNED_8.translate(), 104, 78, 0xFF404040, false);
			graphics.text(font, PELang.TRANSMUTATION_UNLEARNED_9.translate(), 107, 86, 0xFF404040, false);
			inv.unlearnFlag--;
		}
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (textBoxFilter.isFocused()) {
			if (event.isEscape()) {
				textBoxFilter.setFocused(false);
				return true;
			}

			return textBoxFilter.keyPressed(event);
		}

		return super.keyPressed(event);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double x = event.x();
		double y = event.y();
		if (textBoxFilter.isMouseOver(x, y) && event.button() == 1) {
			this.textBoxFilter.setValue("");
			setFocused(this.textBoxFilter);
			return true;
		}
		if (super.mouseClicked(event, doubleClick)) {
			return true;
		}
		if (textBoxFilter.isFocused()) {
			if (hoveredSlot == null || (!hoveredSlot.hasItem() && menu.getCarried().isEmpty())) {
				textBoxFilter.setFocused(false);
			}
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public void removed() {
		super.removed();
		inv.learnFlag = 0;
		inv.unlearnFlag = 0;
	}

	@Override
	protected void extractTooltip(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		BigInteger emcAmount = inv.getAvailableEmc();
		if (emcAmount.compareTo(MAX_EXACT_TRANSMUTATION_DISPLAY) < 0) {
			super.extractTooltip(graphics, mouseX, mouseY);
			return;
		}

		int emcLeft = leftPos;
		int emcRight = emcLeft + 82;
		int emcTop = 95 + topPos;
		int emcBottom = emcTop + 15;
		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			graphics.setTooltipForNextFrame(font, PELang.EMC_TOOLTIP.translate(EMCHelper.formatEmc(emcAmount)), mouseX, mouseY);
		} else {
			super.extractTooltip(graphics, mouseX, mouseY);
		}
	}
}
