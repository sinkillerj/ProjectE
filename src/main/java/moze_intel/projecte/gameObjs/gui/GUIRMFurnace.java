package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GUIRMFurnace extends GUIDMFurnace<RMFurnaceContainer> {

	private static final Identifier RM_FURNACE = PECore.rl("textures/gui/rmfurnace.png");

	public GUIRMFurnace(RMFurnaceContainer container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title, RM_FURNACE, 209, 165, 76);
	}

	@Override
	protected int getLitX() {
		return 66;
	}

	@Override
	protected void renderBurnProgress(@NotNull GuiGraphicsExtractor graphics, int burnProgress) {
		PEGuiGraphics.blit(graphics, texture, leftPos + 88, topPos + 34, 210, 14, burnProgress, 16);
	}
}
