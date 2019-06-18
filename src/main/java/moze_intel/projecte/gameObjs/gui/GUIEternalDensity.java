package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GUIEternalDensity extends ContainerScreen
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/eternal_density.png");
	private final EternalDensityInventory inventory;
	
	public GUIEternalDensity(PlayerInventory invPlayer, EternalDensityInventory invGem)
	{
		super (new EternalDensityContainer(invPlayer, invGem));
		
		this.inventory = invGem;
		
		this.xSize = 180;
		this.ySize = 180;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	@Override
	public void initGui() 
	{
		super.initGui();
		
		this.buttons.add(new Button(1, (width - xSize) / 2 + 62, (height - ySize) / 2 + 4, 52, 20, inventory.isWhitelistMode() ? "Whitelist" : "Blacklist") {
			@Override
			public void onClick(double mouseX, double mouseY)
			{
				inventory.changeMode();
				displayString = I18n.format(inventory.isWhitelistMode() ? "pe.gemdensity.whitelist" : "pe.gemdensity.blacklist");
			}
		});
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) 
	{
		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		this.drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}
}
