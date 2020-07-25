package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIRMFurnace extends PEContainerScreen<RMFurnaceContainer> {

	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID, "textures/gui/rmfurnace.png");
	private final RMFurnaceTile tile;

	public GUIRMFurnace(RMFurnaceContainer container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.xSize = 209;
		this.ySize = 165;
		this.tile = (RMFurnaceTile) container.tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float var1, int var2, int var3) {
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

	@Override
	protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int var1, int var2) {
		this.font.drawString(matrix, I18n.format("pe.rmfurnace.shortname"), 76, 5, 0x404040);
		this.font.drawString(matrix, I18n.format("container.inventory"), 76, ySize - 96 + 2, 0x404040);
	}
}