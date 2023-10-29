package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GUIEternalDensity extends PEContainerScreen<EternalDensityContainer> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/eternal_density.png");

	public GUIEternalDensity(EternalDensityContainer container, Inventory inv, Component title) {
		super(container, inv, title);
		this.imageWidth = 180;
		this.imageHeight = 180;
	}

	@Override
	public void init() {
		super.init();
		addRenderableWidget(Button.builder((menu.inventory.isWhitelistMode() ? PELang.WHITELIST : PELang.BLACKLIST).translate(), b -> {
					menu.inventory.changeMode();
					b.setMessage(menu.inventory.isWhitelistMode() ? PELang.WHITELIST.translate() : PELang.BLACKLIST.translate());
				}).pos(leftPos + 62, topPos + 4)
				.size(52, 20)
				.build());
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
		graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
		//Don't render title or inventory as we don't have space
	}
}