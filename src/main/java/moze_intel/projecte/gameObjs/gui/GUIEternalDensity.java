package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.network.packets.to_server.UpdateGemModePKT;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;

public class GUIEternalDensity extends PEContainerScreen<EternalDensityContainer> {

	private static final Identifier texture = PECore.rl("textures/gui/eternal_density.png");

	public GUIEternalDensity(EternalDensityContainer container, Inventory inv, Component title) {
		super(container, inv, title, 180, 180);
	}

	@Override
	public void init() {
		super.init();
		addRenderableWidget(Button.builder((menu.isWhitelistMode() ? PELang.WHITELIST : PELang.BLACKLIST).translate(), b -> {
					boolean isWhitelistMode = !menu.isWhitelistMode();
					ClientPacketDistributor.sendToServer(new UpdateGemModePKT(menu.hand, isWhitelistMode));
					b.setMessage(isWhitelistMode ? PELang.WHITELIST.translate() : PELang.BLACKLIST.translate());
				}).pos(leftPos + 62, topPos + 4)
				.size(52, 20)
				.build());
	}

	@Override
	protected void peExtractBackground(@NotNull GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
		PEGuiGraphics.blit(graphics, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
