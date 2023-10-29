package moze_intel.projecte.gameObjs.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public abstract class PEContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

	public boolean switchingToJEI;

	public PEContainerScreen(T container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title);
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	public void removed() {
		if (!switchingToJEI) {
			//If we are not switching to JEI then run the super close method
			// which will exit the container. We don't want to mark the
			// container as exited if it will be revived when leaving JEI
			super.removed();
		}
	}

	//Note: Technically this really should be init(@NotNull Minecraft minecraft, int width, int height)
	// but given we don't actually have any data that would use the resize params we can get away with
	// just resetting the switchingToJEI here
	@Override
	public void init() {
		//Mark that we are not switching to JEI if we start being initialized again
		switchingToJEI = false;
		super.init();
	}
}