package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.block_entities.RMFurnaceBlockEntity;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

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
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
		graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int progress;
		if (furnace.isBurning()) {
			progress = furnace.getBurnTimeRemainingScaled(12);
			graphics.blit(texture, leftPos + 66, topPos + 38 + 10 - progress, 210, 10 - progress, 21, progress + 2);
		}
		progress = furnace.getCookProgressScaled(24);
		graphics.blit(texture, leftPos + 88, topPos + 35, 210, 14, progress, 17);
	}
}