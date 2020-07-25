package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public abstract class PEContainerScreen<T extends Container> extends ContainerScreen<T> {

	public PEContainerScreen(T container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
	}

	@Override
	public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		this.func_230459_a_(matrix, mouseX, mouseY);
	}
}