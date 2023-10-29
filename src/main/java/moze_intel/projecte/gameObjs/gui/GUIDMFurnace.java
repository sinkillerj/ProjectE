package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.block_entities.DMFurnaceBlockEntity;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GUIDMFurnace extends PEContainerScreen<DMFurnaceContainer> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/dmfurnace.png");
	private final DMFurnaceBlockEntity furnace;

	public GUIDMFurnace(DMFurnaceContainer container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title);
		this.imageWidth = 178;
		this.imageHeight = 165;
		this.furnace = container.furnace;
		this.titleLabelX = 57;
		this.inventoryLabelX = 57;
		this.inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);

		graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int progress;
		if (furnace.isBurning()) {
			progress = furnace.getBurnTimeRemainingScaled(12);
			graphics.blit(texture, leftPos + 49, topPos + 36 + 12 - progress, 179, 12 - progress, 14, progress + 2);
		}
		progress = furnace.getCookProgressScaled(24);
		graphics.blit(texture, leftPos + 73, topPos + 34, 179, 14, progress + 1, 16);
	}
}