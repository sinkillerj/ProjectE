package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

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
		addRenderableWidget(new Button(leftPos + 62, topPos + 4, 52, 20,
				(menu.inventory.isWhitelistMode() ? PELang.WHITELIST : PELang.BLACKLIST).translate(), b -> {
			menu.inventory.changeMode();
			b.setMessage(menu.inventory.isWhitelistMode() ? PELang.WHITELIST.translate() : PELang.BLACKLIST.translate());
		}));
	}

	@Override
	protected void renderBg(@Nonnull PoseStack matrix, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(@Nonnull PoseStack matrix, int x, int y) {
		//Don't render title or inventory as we don't have space
	}
}