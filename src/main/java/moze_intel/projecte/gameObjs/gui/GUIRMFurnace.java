package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIRMFurnace extends PEContainerScreen<RMFurnaceContainer> {

	private static final ResourceLocation texture = PECore.rl("textures/gui/rmfurnace.png");
	private final RMFurnaceTile tile;

	public GUIRMFurnace(RMFurnaceContainer container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.xSize = 209;
		this.ySize = 165;
		this.tile = (RMFurnaceTile) container.tile;
		this.titleX = 76;
		this.playerInventoryTitleX = 76;
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
			blit(matrix, guiLeft + 66, guiTop + 38 + 10 - progress, 210, 10 - progress, 21, progress + 2);
		}
		progress = tile.getCookProgressScaled(24);
		blit(matrix, guiLeft + 88, guiTop + 35, 210, 14, progress, 17);
	}
}