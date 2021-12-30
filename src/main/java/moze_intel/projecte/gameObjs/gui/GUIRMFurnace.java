package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.block_entities.RMFurnaceBlockEntity;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GUIRMFurnace extends PEContainerScreen<RMFurnaceContainer> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/rmfurnace.png");
	private final RMFurnaceBlockEntity furnace;

	public GUIRMFurnace(RMFurnaceContainer container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title);
		this.imageWidth = 209;
		this.imageHeight = 165;
		this.furnace = (RMFurnaceBlockEntity) container.furnace;
		this.titleLabelX = 76;
		this.inventoryLabelX = 76;
		this.inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void renderBg(@Nonnull PoseStack matrix, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int progress;
		if (furnace.isBurning()) {
			progress = furnace.getBurnTimeRemainingScaled(12);
			blit(matrix, leftPos + 66, topPos + 38 + 10 - progress, 210, 10 - progress, 21, progress + 2);
		}
		progress = furnace.getCookProgressScaled(24);
		blit(matrix, leftPos + 88, topPos + 35, 210, 14, progress, 17);
	}
}