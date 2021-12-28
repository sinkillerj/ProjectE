package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.math.BigInteger;
import java.util.Locale;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
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

		this.textBoxFilter = new EditBox(this.font, leftPos + 88, topPos + 8, 45, 10, TextComponent.EMPTY);
		this.textBoxFilter.setValue(inv.filter);

		addRenderableWidget(new Button(leftPos + 125, topPos + 100, 14, 14, new TextComponent("<"), b -> {
			if (inv.searchpage != 0) {
				inv.searchpage--;
			}
			inv.filter = textBoxFilter.getValue().toLowerCase(Locale.ROOT);
			inv.updateClientTargets();
		}));
		addRenderableWidget(new Button(leftPos + 193, topPos + 100, 14, 14, new TextComponent(">"), b -> {
			if (inv.getKnowledgeSize() > 12) {
				inv.searchpage++;
			}
			inv.filter = textBoxFilter.getValue().toLowerCase(Locale.ROOT);
			inv.updateClientTargets();
		}));
	}

	@Override
	protected void renderBg(@Nonnull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		this.textBoxFilter.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderLabels(@Nonnull PoseStack matrix, int x, int y) {
		this.font.draw(matrix, title, titleLabelX, titleLabelY, 0x404040);
		//Don't render inventory as we don't have space
		BigInteger emcAmount = inv.getAvailableEmc();
		this.font.draw(matrix, PELang.EMC_TOOLTIP.translate(""), 6, this.imageHeight - 104, 0x404040);
		Component emc = TransmutationEMCFormatter.formatEMC(emcAmount);
		this.font.draw(matrix, emc, 6, this.imageHeight - 94, 0x404040);

		if (inv.learnFlag > 0) {
			this.font.draw(matrix, PELang.TRANSMUTATION_LEARNED_1.translate(), 98, 30, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_LEARNED_2.translate(), 99, 38, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_LEARNED_3.translate(), 100, 46, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_LEARNED_4.translate(), 101, 54, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_LEARNED_5.translate(), 102, 62, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_LEARNED_6.translate(), 103, 70, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_LEARNED_7.translate(), 104, 78, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_LEARNED_8.translate(), 107, 86, 0x404040);

			inv.learnFlag--;
		}

		if (inv.unlearnFlag > 0) {
			this.font.draw(matrix, PELang.TRANSMUTATION_UNLEARNED_1.translate(), 97, 22, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_UNLEARNED_2.translate(), 98, 30, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_UNLEARNED_3.translate(), 99, 38, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_UNLEARNED_4.translate(), 100, 46, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_UNLEARNED_5.translate(), 101, 54, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_UNLEARNED_6.translate(), 102, 62, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_UNLEARNED_7.translate(), 103, 70, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_UNLEARNED_8.translate(), 104, 78, 0x404040);
			this.font.draw(matrix, PELang.TRANSMUTATION_UNLEARNED_9.translate(), 107, 86, 0x404040);

			inv.unlearnFlag--;
		}
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		this.textBoxFilter.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textBoxFilter.isFocused()) {
			//Manually make it so that hitting escape when the filter is focused will exit the focus
			if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
				textBoxFilter.setFocus(false);
				return true;
			}
			//Otherwise have it handle the key press
			//This is where key combos and deletion is handled
			if (textBoxFilter.keyPressed(keyCode, scanCode, modifiers)) {
				//If the filter reacted from the key press, then something happened and we should update the filter
				updateFilter();
				return true;
			}
			return false;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char c, int keyCode) {
		if (textBoxFilter.isFocused()) {
			//If our filter is focused have it handle the character being typed
			//This is where adding characters is handled
			if (textBoxFilter.charTyped(c, keyCode)) {
				//If the filter reacted from to a character being typed, then something happened and we should update the filter
				updateFilter();
				return true;
			}
			return false;
		}
		return super.charTyped(c, keyCode);
	}

	private void updateFilter() {
		String search = textBoxFilter.getValue().toLowerCase(Locale.ROOT);
		if (!inv.filter.equals(search)) {
			inv.filter = search;
			inv.searchpage = 0;
			inv.updateClientTargets();
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseButton) {
		int minX = textBoxFilter.x;
		int minY = textBoxFilter.y;
		int maxX = minX + textBoxFilter.getWidth();
		int maxY = minY + textBoxFilter.getHeight();

		if (x >= minX && x <= maxX && y <= maxY) {
			if (mouseButton == 1) {
				inv.filter = "";
				inv.searchpage = 0;
				inv.updateClientTargets();
				this.textBoxFilter.setValue("");
			}
			return this.textBoxFilter.mouseClicked(x, y, mouseButton);
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
	protected void renderTooltip(@Nonnull PoseStack matrix, int mouseX, int mouseY) {
		BigInteger emcAmount = inv.getAvailableEmc();

		if (emcAmount.compareTo(Constants.MAX_EXACT_TRANSMUTATION_DISPLAY) < 0) {
			super.renderTooltip(matrix, mouseX, mouseY);
			return;
		}

		int emcLeft = leftPos;
		int emcRight = emcLeft + 82;
		int emcTop = 95 + topPos;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			renderTooltip(matrix, PELang.EMC_TOOLTIP.translate(Constants.EMC_FORMATTER.format(emcAmount)), mouseX, mouseY);
		} else {
			super.renderTooltip(matrix, mouseX, mouseY);
		}
	}
}