package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.MercurialEyeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIMercurialEye extends PEContainerScreen<MercurialEyeContainer> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/mercurial_eye.png");

	public GUIMercurialEye(MercurialEyeContainer container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.imageWidth = 171;
		this.imageHeight = 134;
	}

	@Override
	protected void renderBg(@Nonnull MatrixStack matrix, float partialTicks, int x, int y) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bind(texture);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(@Nonnull MatrixStack matrix, int x, int y) {
		//Don't render title or inventory as we don't have space
	}
}