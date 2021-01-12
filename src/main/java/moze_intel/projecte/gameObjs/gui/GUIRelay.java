package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.RelayMK2Container;
import moze_intel.projecte.gameObjs.container.RelayMK3Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIRelay<CONTAINER extends RelayMK1Container> extends PEContainerScreen<CONTAINER> {

	private static final ResourceLocation MK2_TEXTURE = PECore.rl("textures/gui/relay2.png");
	private static final ResourceLocation MK3_TEXTURE = PECore.rl("textures/gui/relay3.png");

	private final ResourceLocation texture;
	private final int emcX;
	private final int emcY;
	private final int vOffset;
	private final int emcBarShift;
	private final int shift;

	protected GUIRelay(CONTAINER container, PlayerInventory invPlayer, ITextComponent title, ResourceLocation texture, int emcX, int emcY, int vOffset,
			int emcBarShift, int shift) {
		super(container, invPlayer, title);
		this.texture = texture;
		this.emcX = emcX;
		this.emcY = emcY;
		this.vOffset = vOffset;
		this.emcBarShift = emcBarShift;
		this.shift = shift;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int x, int y) {
		this.font.func_243248_b(matrix, title, titleX, titleY, 0x404040);
		//Don't render inventory as we don't have space
		this.font.drawString(matrix, Constants.EMC_FORMATTER.format(container.emc.get()), emcX, emcY, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float partialTicks, int x, int y) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);

		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);

		//Emc bar progress
		int progress = (int) ((double) container.emc.get() / container.tile.getMaximumEmc() * Constants.MAX_CONDENSER_PROGRESS);
		blit(matrix, guiLeft + emcBarShift, guiTop + 6, 30, vOffset, progress, 10);

		//Klein start bar progress. Max is 30.
		progress = (int) (container.getKleinChargeProgress() * 30);
		blit(matrix, guiLeft + 116 + shift, guiTop + 67, 0, vOffset, progress, 10);

		//Burn Slot bar progress. Max is 30.
		progress = (int) (container.getInputBurnProgress() * 30);
		blit(matrix, guiLeft + 64 + shift, guiTop + 67, 0, vOffset, progress, 10);
	}

	public static class GUIRelayMK1 extends GUIRelay<RelayMK1Container> {

		private static final ResourceLocation MK1_TEXTURE = PECore.rl("textures/gui/relay1.png");

		public GUIRelayMK1(RelayMK1Container container, PlayerInventory invPlayer, ITextComponent title) {
			super(container, invPlayer, title, MK1_TEXTURE, 88, 24, 177, 64, 0);
			this.xSize = 175;
			this.ySize = 176;
			this.titleX = 10;
		}
	}

	public static class GUIRelayMK2 extends GUIRelay<RelayMK2Container> {

		private static final ResourceLocation MK2_TEXTURE = PECore.rl("textures/gui/relay2.png");

		public GUIRelayMK2(RelayMK2Container container, PlayerInventory invPlayer, ITextComponent title) {
			super(container, invPlayer, title, MK2_TEXTURE, 107, 25, 183, 86, 17);
			this.xSize = 193;
			this.ySize = 182;
			this.titleX = 28;
		}
	}

	public static class GUIRelayMK3 extends GUIRelay<RelayMK3Container> {

		private static final ResourceLocation MK3_TEXTURE = PECore.rl("textures/gui/relay3.png");

		public GUIRelayMK3(RelayMK3Container container, PlayerInventory invPlayer, ITextComponent title) {
			super(container, invPlayer, title, MK3_TEXTURE, 125, 39, 195, 105, 37);
			this.xSize = 212;
			this.ySize = 194;
			this.titleX = 38;
		}
	}
}