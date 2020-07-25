package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractCollectorScreen<T extends CollectorMK1Container> extends PEContainerScreen<T> {

	public AbstractCollectorScreen(T container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
	}

	protected abstract ResourceLocation getTexture();

	protected int getBonusXShift() {
		return 0;
	}

	protected int getTextureBonusXShift() {
		return 0;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int var1, int var2) {
		this.font.drawString(matrix, Long.toString(container.emc.get()), 60 + getBonusXShift(), 32, 0x404040);
		long kleinCharge = container.kleinEmc.get();
		if (kleinCharge > 0) {
			this.font.drawString(matrix, Constants.EMC_FORMATTER.format(kleinCharge), 60 + getBonusXShift(), 44, 0x404040);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float var1, int var2, int var3) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(getTexture());

		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);

		//Light Level. Max is 12
		int progress = (int) (container.sunLevel.get() * 12.0 / 16);
		blit(matrix, guiLeft + 126 + getBonusXShift(), guiTop + 49 - progress, 177 + getTextureBonusXShift(), 13 - progress, 12, progress);

		//EMC storage. Max is 48
		blit(matrix, guiLeft + 64 + getBonusXShift(), guiTop + 18, 0, 166, (int) ((double) container.emc.get() / container.tile.getMaximumEmc() * 48), 10);

		//Klein Star Charge Progress. Max is 48
		progress = (int) (container.getKleinChargeProgress() * 48);
		blit(matrix, guiLeft + 64 + getBonusXShift(), guiTop + 58, 0, 166, progress, 10);

		//Fuel Progress. Max is 24.
		progress = (int) (container.getFuelProgress() * 24);
		blit(matrix, guiLeft + 138 + getBonusXShift(), guiTop + 55 - progress, 176 + getTextureBonusXShift(), 38 - progress, 10, progress + 1);
	}

	public static class MK1 extends AbstractCollectorScreen<CollectorMK1Container> {

		public MK1(CollectorMK1Container container, PlayerInventory invPlayer, ITextComponent title) {
			super(container, invPlayer, title);
		}

		@Override
		protected ResourceLocation getTexture() {
			return new ResourceLocation(PECore.MODID, "textures/gui/collector1.png");
		}
	}

	public static class MK2 extends AbstractCollectorScreen<CollectorMK2Container> {

		public MK2(CollectorMK2Container container, PlayerInventory invPlayer, ITextComponent title) {
			super(container, invPlayer, title);
			this.xSize = 200;
			this.ySize = 165;
		}

		@Override
		protected ResourceLocation getTexture() {
			return new ResourceLocation(PECore.MODID, "textures/gui/collector2.png");
		}

		@Override
		protected int getBonusXShift() {
			return 16;
		}

		@Override
		protected int getTextureBonusXShift() {
			return 25;
		}
	}

	public static class MK3 extends AbstractCollectorScreen<CollectorMK3Container> {

		public MK3(CollectorMK3Container container, PlayerInventory invPlayer, ITextComponent title) {
			super(container, invPlayer, title);
			this.xSize = 218;
			this.ySize = 165;
		}

		@Override
		protected ResourceLocation getTexture() {
			return new ResourceLocation(PECore.MODID, "textures/gui/collector3.png");
		}

		@Override
		protected int getBonusXShift() {
			return 34;
		}

		@Override
		protected int getTextureBonusXShift() {
			return 43;
		}
	}
}