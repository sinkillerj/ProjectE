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
		this.xSize = 178;
		this.ySize = 165;
		this.tile = container.tile;
		this.titleX = 57;
		this.playerInventoryTitleX = 57;
		this.playerInventoryTitleY = ySize - 94;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float partialTicks, int x, int y) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);

		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);

		int progress;
		if (tile.isBurning()) {
			progress = tile.getBurnTimeRemainingScaled(12);
			blit(matrix, guiLeft + 49, guiTop + 36 + 12 - progress, 179, 12 - progress, 14, progress + 2);
		}
		progress = tile.getCookProgressScaled(24);
		blit(matrix, guiLeft + 73, guiTop + 34, 179, 14, progress + 1, 16);
	}
}