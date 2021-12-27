package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public abstract class PEContainerScreen<T extends Container> extends ContainerScreen<T> {

	public boolean switchingToJEI;

	public PEContainerScreen(T container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
	}

	@Override
	public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrix, mouseX, mouseY);
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

	@Override
	public void init(@Nonnull Minecraft minecraft, int width, int height) {
		//Mark that we are not switching to JEI if we start being initialized again
		switchingToJEI = false;
		super.init(minecraft, width, height);
	}
}