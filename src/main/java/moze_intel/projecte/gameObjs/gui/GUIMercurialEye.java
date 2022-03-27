package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.MercurialEyeContainer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GUIMercurialEye extends PEContainerScreen<MercurialEyeContainer> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/mercurial_eye.png");

	public GUIMercurialEye(MercurialEyeContainer container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title);
		this.imageWidth = 171;
		this.imageHeight = 134;
	}

	@Override
	protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(@NotNull PoseStack matrix, int x, int y) {
		//Don't render title or inventory as we don't have space
	}
}