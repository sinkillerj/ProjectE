package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;

public class GUICondenser extends ContainerScreen
{
	protected final CondenserContainer container;

	public GUICondenser(CondenserContainer condenser, PlayerInventory playerInventory, ITextComponent title)
	{
		super(condenser, playerInventory, title);
		this.container = condenser;
		this.xSize = 255;
		this.ySize = 233;
	}

	protected ResourceLocation getTexture()
	{
		return new ResourceLocation(PECore.MODID, "textures/gui/condenser.png");
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().textureManager.bindTexture(getTexture());
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.blit(x, y, 0, 0, xSize, ySize);
		
		int progress = container.getProgressScaled();
		this.blit(x + 33, y + 10, 0, 235, progress, 10);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) 
	{
		long toDisplay = container.displayEmc > container.requiredEmc ? container.requiredEmc : container.displayEmc;
		String emc = TransmutationEMCFormatter.EMCFormat(toDisplay);
		this.font.drawString(emc, 140, 10, 4210752);
	}

	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		long toDisplay = container.displayEmc > container.requiredEmc ? container.requiredEmc : container.displayEmc;

		if (toDisplay < 1e12) {
			super.renderHoveredToolTip(mouseX, mouseY);
			return;
		}

		int emcLeft = 140 + (this.width - this.xSize) / 2;
		int emcRight = emcLeft + 110;
		int emcTop = 6 + (this.height - this.ySize) / 2;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			String emcAsString = I18n.format("pe.emc.emc_tooltip_prefix") + " " + Constants.EMC_FORMATTER.format(toDisplay);
			renderTooltip(Arrays.asList(emcAsString), mouseX, mouseY);
		} else {
			super.renderHoveredToolTip(mouseX, mouseY);
		}
	}
}
