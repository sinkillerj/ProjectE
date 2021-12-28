package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.block_entities.DMFurnaceTile;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class GUIDMFurnace extends PEContainerScreen<DMFurnaceContainer> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/dmfurnace.png");
	private final DMFurnaceTile tile;

	public GUIDMFurnace(DMFurnaceContainer container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title);
		this.imageWidth = 178;
		this.imageHeight = 165;
		this.tile = container.tile;
		this.titleLabelX = 57;
		this.inventoryLabelX = 57;
		this.inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void renderBg(@Nonnull PoseStack matrix, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int progress;
		if (tile.isBurning()) {
			progress = tile.getBurnTimeRemainingScaled(12);
			blit(matrix, leftPos + 49, topPos + 36 + 12 - progress, 179, 12 - progress, 14, progress + 2);
		}
		progress = tile.getCookProgressScaled(24);
		blit(matrix, leftPos + 73, topPos + 34, 179, 14, progress + 1, 16);
	}
}