package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUIEternalDensity extends PEContainerScreen<EternalDensityContainer> {

	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID, "textures/gui/eternal_density.png");

	public GUIEternalDensity(EternalDensityContainer container, PlayerInventory inv, ITextComponent title) {
		super(container, inv, title);
		this.xSize = 180;
		this.ySize = 180;
	}

	@Override
	public void init() {
		super.init();
		addButton(new Button(guiLeft + 62, guiTop + 4, 52, 20, container.inventory.isWhitelistMode() ? "Whitelist" : "Blacklist", b -> {
			container.inventory.changeMode();
			b.setMessage(I18n.format(container.inventory.isWhitelistMode() ? "pe.gemdensity.whitelist" : "pe.gemdensity.blacklist"));
		}));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}