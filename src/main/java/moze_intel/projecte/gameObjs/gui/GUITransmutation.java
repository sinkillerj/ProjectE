package moze_intel.projecte.gameObjs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Arrays;

public class GUITransmutation extends ContainerScreen
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/transmute.png");
	private final TransmutationInventory inv;
	private TextFieldWidget textBoxFilter;

	public GUITransmutation(PlayerInventory invPlayer, TransmutationInventory inventory, @Nullable Hand hand)
	{
		super(new TransmutationContainer(invPlayer, inventory, hand));
		this.inv = inventory;
		this.xSize = 228;
		this.ySize = 196;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	@Override
	public void init()
	{
		super.init();

		int xLocation = (this.width - this.xSize) / 2;
		int yLocation = (this.height - this.ySize) / 2;

		this.textBoxFilter = new TextFieldWidget(this.font, xLocation + 88, yLocation + 8, 45, 10, "");
		this.textBoxFilter.setText(inv.filter);

		this.buttons.add(new Button(xLocation + 125, yLocation + 100, 14, 14, "<", b -> {
			if (inv.searchpage != 0)
			{
				inv.searchpage--;
			}
			inv.filter = textBoxFilter.getText().toLowerCase(Locale.ROOT);
			inv.updateClientTargets();
		}));
		this.buttons.add(new Button(xLocation + 193, yLocation + 100, 14, 14, ">", b -> {
			if (!(inv.knowledge.size() <= 12))
			{
				inv.searchpage++;
			}
			inv.filter = textBoxFilter.getText().toLowerCase(Locale.ROOT);
			inv.updateClientTargets();
		}));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		this.blit(guiLeft, guiTop, 0, 0, xSize, ySize);
		this.textBoxFilter.render(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) 
	{
		this.font.drawString(I18n.format("pe.transmutation.transmute"), 6, 8, 4210752);
		double emcAmount = inv.player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).map(IKnowledgeProvider::getEmc).orElse(0.0);
		String emcLabel = I18n.format("pe.emc.emc_tooltip_prefix");
		this.font.drawString(emcLabel, 6, this.ySize - 104, 4210752);
		String emc = TransmutationEMCFormatter.EMCFormat(emcAmount);
		this.font.drawString(emc, 6, this.ySize - 94, 4210752);

		if (inv.learnFlag > 0)
		{
			this.font.drawString(I18n.format("pe.transmutation.learned0"), 98, 30, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.learned1"), 99, 38, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.learned2"), 100, 46, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.learned3"), 101, 54, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.learned4"), 102, 62, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.learned5"), 103, 70, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.learned6"), 104, 78, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.learned7"), 107, 86, 4210752);
			
			inv.learnFlag--;
		}

		if (inv.unlearnFlag > 0)
		{
			this.font.drawString(I18n.format("pe.transmutation.unlearned0"), 97, 22, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.unlearned1"), 98, 30, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.unlearned2"), 99, 38, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.unlearned3"), 100, 46, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.unlearned4"), 101, 54, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.unlearned5"), 102, 62, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.unlearned6"), 103, 70, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.unlearned7"), 104, 78, 4210752);
			this.font.drawString(I18n.format("pe.transmutation.unlearned8"), 107, 86, 4210752);
			
			inv.unlearnFlag--;
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		this.textBoxFilter.tick();
	}

	@Override
	public boolean charTyped(char par1, int par2)
	{
		boolean res = super.charTyped(par1, par2);
		if (this.textBoxFilter.isFocused()) 
		{
			String srch = this.textBoxFilter.getText().toLowerCase();

			if (!inv.filter.equals(srch))
			{
				inv.filter = srch;
				inv.searchpage = 0;
				inv.updateClientTargets();
			}
		}
		return res;
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseButton)
	{
		int minX = textBoxFilter.x;
		int minY = textBoxFilter.y;
		int maxX = minX + textBoxFilter.getWidth();
		int maxY = minY + textBoxFilter.getHeight();

		if (mouseButton == 1 && x >= minX && x <= maxX && y <= maxY)
		{
			inv.filter = "";
			inv.searchpage = 0;
			inv.updateClientTargets();
			this.textBoxFilter.setText("");
		}

		return this.textBoxFilter.mouseClicked(x, y, mouseButton) || super.mouseClicked(x, y, mouseButton);
	}

	@Override
	public void onClose()
	{
		super.onClose();
		inv.learnFlag = 0;
		inv.unlearnFlag = 0;
	}

	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		double emcAmount = inv.player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).map(IKnowledgeProvider::getEmc).orElse(0.0);

		if (emcAmount < 1e12) {
			super.renderHoveredToolTip(mouseX, mouseY);
			return;
		}

		int emcLeft = (this.width - this.xSize) / 2;
		int emcRight = emcLeft + 82;
		int emcTop = 95 + (this.height - this.ySize) / 2;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			String emcAsString = I18n.format("pe.emc.emc_tooltip_prefix") + " " + Constants.EMC_FORMATTER.format(emcAmount);
			renderTooltip(Collections.singletonList(emcAsString), mouseX, mouseY);
		} else {
			super.renderHoveredToolTip(mouseX, mouseY);
		}
	}
}
