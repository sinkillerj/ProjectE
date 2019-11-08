package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.MercurialEyeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIMercurialEye extends PEContainerScreen<MercurialEyeContainer> {

	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID, "textures/gui/mercurial_eye.png");

	public GUIMercurialEye(MercurialEyeContainer container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.xSize = 171;
		this.ySize = 134;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}