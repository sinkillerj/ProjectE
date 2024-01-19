package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.block_entities.DMFurnaceBlockEntity;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GUIDMFurnace<CONTAINER extends DMFurnaceContainer> extends PEContainerScreen<CONTAINER> {

	//FurnaceScreen.LIT_PROGRESS_SPRITE
	private static final ResourceLocation LIT_PROGRESS_SPRITE = new ResourceLocation("container/furnace/lit_progress");
	private static final int LIT_SIZE = 14;

	private static final ResourceLocation DM_FURNACE = PECore.rl("textures/gui/dmfurnace.png");

	private final DMFurnaceBlockEntity furnace;
	protected final ResourceLocation texture;

	public GUIDMFurnace(CONTAINER container, Inventory invPlayer, Component title) {
		this(container, invPlayer, title, DM_FURNACE, 178, 165, 57);
	}

	public GUIDMFurnace(CONTAINER container, Inventory invPlayer, Component title, ResourceLocation texture, int textureWidth, int textureHeight,
			int labelX) {
		super(container, invPlayer, title);
		this.texture = texture;
		this.imageWidth = textureWidth;
		this.imageHeight = textureHeight;
		this.furnace = container.furnace;
		this.titleLabelX = labelX;
		this.inventoryLabelX = labelX;
		this.inventoryLabelY = imageHeight - 94;
	}

	protected int getLitX() {
		return 49;
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
		graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		if (furnace.isLit()) {
			int litProgress = Mth.ceil(furnace.getLitProgress() * 11) + 1;
			int litPortion = LIT_SIZE - litProgress;
			graphics.blitSprite(LIT_PROGRESS_SPRITE, LIT_SIZE, LIT_SIZE, 0, litPortion, leftPos + getLitX(), topPos + 36 + litPortion, LIT_SIZE, litProgress);
		}

		int burnProgress = Mth.ceil(furnace.getBurnProgress() * 24);
		renderBurnProgress(graphics, burnProgress);
	}

	protected void renderBurnProgress(@NotNull GuiGraphics graphics, int burnProgress) {
		graphics.blit(texture, leftPos + 73, topPos + 34, 179, 14, burnProgress, 16);
	}
}