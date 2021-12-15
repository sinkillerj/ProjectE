package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIDMFurnace extends PEContainerScreen<DMFurnaceContainer> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/dmfurnace.png");
	private final DMFurnaceTile tile;

	public GUIDMFurnace(DMFurnaceContainer container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.imageWidth = 178;
		this.imageHeight = 165;
		this.tile = container.tile;
		this.titleLabelX = 57;
		this.inventoryLabelX = 57;
		this.inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void renderBg(@Nonnull MatrixStack matrix, float partialTicks, int x, int y) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bind(texture);

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