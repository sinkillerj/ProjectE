package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.manual.AbstractPage;
import moze_intel.projecte.manual.ImagePage;
import moze_intel.projecte.manual.ItemPage;
import moze_intel.projecte.manual.ManualPageHandler;
import moze_intel.projecte.manual.PageCategory;
import moze_intel.projecte.utils.ItemHelper;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.Lists;

import java.awt.Color;
import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GUIManual extends GuiScreen
{
	private ResourceLocation bookTexture = new ResourceLocation("projecte:textures/gui/bookTexture.png");
	private ResourceLocation tocTexture = new ResourceLocation("projecte:textures/gui/bookTexture.png");
	private static ResourceLocation bookGui = new ResourceLocation("textures/gui/book.png");

	private final int INDEX_PAGE_ID = -1;
	private final int CHARACTER_HEIGHT = Math.round(9 / 2.5f);
	private int currentPageID;
	private int offset = 3;
	
	public static List<String> bodyTexts = Lists.newArrayList();

	@Override
	public void initGui()
	{
		int i = (this.width - 256) / 2;
		
        this.buttonList.add(new PageTurnButton(0, i + 210, 160, true));
        this.buttonList.add(new PageTurnButton(1, i + 16, 160, false));
        
    	String text = StatCollector.translateToLocal("pe.manual.index_button");
    	int width = mc.fontRenderer.getStringWidth(text);
		
        this.buttonList.add(new TocButton(2, (this.width / 2) - (width / 2), 192, width, 15, text));

		currentPageID = INDEX_PAGE_ID;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		width = scaledresolution.getScaledWidth();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (isViewingIndex())
		{
	    	this.mc.getTextureManager().bindTexture(tocTexture);
		} else
		{
	    	this.mc.getTextureManager().bindTexture(bookTexture);
		}
		
	    int k = (this.width - 256) / 2;
	    int pageHeight = 180;
	    this.drawTexturedModalRect(k, 5, 0, 0, 256, pageHeight);


		if (!isViewingIndex())
		{
			AbstractPage currentPage = ManualPageHandler.pages.get(currentPageID);
			this.fontRendererObj.drawString(currentPage.getHeaderText(), k + 39, 27, 0, false);

			if (currentPage instanceof ImagePage)
			{
	    		drawImage(((ImagePage) currentPage).getImageLocation(),(scaledresolution.getScaledWidth() + 256) / 2, 80);
	    	} else
			{
	    		float scaleFactor = 0.5f;
	    		//GL11.glScalef(scaleFactor, scaleFactor, 1f);
	    		
	    		
	    		
	    		
	    		
				//this.fontRendererObj.drawSplitString(ManualPageHandler.pages.get(currentPageID).getBodyText(), Math.round((k + 18)/scaleFactor), Math.round((45)/scaleFactor), Math.round((220)/scaleFactor), 0);
	    		bodyTexts = this.fontRendererObj.listFormattedStringToWidth(ManualPageHandler.pages.get(currentPageID).getBodyText(), 220);
				
				for(int i = 0; i < bodyTexts.size(); i++)
				{
					this.fontRendererObj.drawString(bodyTexts.get(i), k + 18, 43 + 10 * i, Color.black.getRGB());
					
				}
				
				
				//GL11.glScalef(1/scaleFactor, 1/scaleFactor, 1f);
				if (currentPage instanceof ItemPage)
				{
					ItemPage itemPage = ((ItemPage) currentPage);
					drawItemStackToGui(mc, itemPage.getItemStack(), k + 19, 22, !(itemPage.getItemStack().getItem() instanceof ItemBlock)
					|| ItemHelper.areItemStacksEqual(itemPage.getItemStack(), new ItemStack(ObjHandler.confuseTorch)));
				}
			}
	    } else
		{
	    	this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.manual.index"), k + 39, 27, 0, false);
	    	drawIndex();
		}

		this.updateButtons();

		for (Object aButtonList : this.buttonList)
		{
			((GuiButton) aButtonList).drawButton(this.mc, mouseX, mouseY);
		}

	}
	
    @Override
	protected void actionPerformed(GuiButton button)
	{
		switch (button.id)
		{
			case 0: currentPageID++; break;
			case 1: currentPageID--; break;
			case 2: currentPageID = INDEX_PAGE_ID; break;
			default: currentPageID = button.id - 3;
		}
    	this.updateButtons();
    }
	
    private void updateButtons()
	{
    	if (isViewingIndex())
		{
    		((PageTurnButton) this.buttonList.get(0)).visible = true;
    		((PageTurnButton) this.buttonList.get(1)).visible = false;
    		((TocButton) this.buttonList.get(2)).visible = false;
    		for (int i = 3; i<this.buttonList.size(); i++)
			{
    			((IndexLinkButton)this.buttonList.get(i)).visible = true;
    		}
    	} else if (this.currentPageID == ManualPageHandler.pages.size() - 1)
		{
    		((PageTurnButton) this.buttonList.get(0)).visible = false;
    		((PageTurnButton) this.buttonList.get(1)).visible = true;
    		((TocButton) this.buttonList.get(2)).visible = true;
    		for (int i = 3; i<this.buttonList.size(); i++)
			{
    			((IndexLinkButton)this.buttonList.get(i)).visible = false;
    		}
    	} else
		{
    		((PageTurnButton) this.buttonList.get(0)).visible = true;
    		((PageTurnButton) this.buttonList.get(1)).visible = true;
    		((TocButton) this.buttonList.get(2)).visible = true;
    		for (int i = 3; i < this.buttonList.size(); i++)
			{
    			((IndexLinkButton)this.buttonList.get(i)).visible = false;
    		}
    	}
    }

    @SideOnly(Side.CLIENT)
	private static class TocButton extends GuiButton
	{

		public TocButton(int ID, int xPos, int yPos, int bWidth, int bHeight, String text)
		{
			super(ID, xPos, yPos, bWidth, bHeight, text);
		}
		
    }
    
    @SideOnly(Side.CLIENT)
	private static class IndexLinkButton extends GuiButton
	{
    	public IndexLinkButton(int par1, int par2, int par3, int par4, int par5, String par6)
		{
    		super(par1, par2, par3, par4, par5, par6);
    	}
    	
    	@Override
    	public void drawButton(Minecraft mc, int par2, int par3)
		{
			if (visible)
			{
				GL11.glScaled(0.4f, 0.4f, 0.4f);
				drawRect(xPosition, yPosition,(xPosition + width), (yPosition + height), 0);
				mc.fontRenderer.drawString(displayString, Math.round(xPosition * 2.5f), Math.round(yPosition * 2.5f), 0);
				GL11.glScaled(2.5f, 2.5f, 2.5f);
			}
		}
    }    	
    
	@SideOnly(Side.CLIENT)
	private static class PageTurnButton extends GuiButton
	{
		private static final int bWidth = 23;
		private static final int bHeight = 13;
		private boolean pointsRight;
		
		public PageTurnButton(int ID, int xPos, int yPos, boolean par4)
		{
			super(ID, xPos, yPos, bWidth, bHeight, "");
			pointsRight = par4;
		}
		
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				boolean hover = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(bookGui);
				int u = 0;
				int v = 192;
				
				if (hover)
				{
					 u += bWidth;
				}

				if(!pointsRight)
				{
					v += bHeight;
				}
				GL11.glEnable(GL11.GL_BLEND);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v, bWidth, bHeight);
				GL11.glDisable(GL11.GL_BLEND);
			}
		}
		
	}
	
	public static void drawItemStackToGui(Minecraft mc, ItemStack item, int x, int y, boolean fixLighting)
	{
		if (fixLighting)
		{
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, x, y);
        
        if (fixLighting)
		{
        	GL11.glDisable(GL11.GL_LIGHTING);
        }
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	public void drawImage(ResourceLocation resource, int x, int y)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(resource);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glScalef(0.5F, 0.5F, 1F);
		this.drawTexturedModalRect(x, y, 0, 0, 256, 256);
		GL11.glScalef(2F, 2F, 1F);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void drawCategory(String name, int xOffset, int yOffset)
	{
		GL11.glScalef(0.5F, 0.5F, 1F);
		mc.fontRenderer.drawString(EnumChatFormatting.UNDERLINE + name, (((this.width - 256) / 2) + xOffset)*2, yOffset * 2, 0);
		GL11.glScalef(2, 2F, 1F);
	}
	
	public void drawIndex()
	{
    	int xOffset = 30;
    	int yOffset = 0;
    	int yValue = -1;

    	for (Entry<PageCategory, List<AbstractPage>> entry : ManualPageHandler.categoryMap.entrySet())
		{
			yValue++;
			yOffset = yValue * CHARACTER_HEIGHT + 40;
			drawCategory(StatCollector.translateToLocal(entry.getKey().getUnlocalName()), xOffset, yOffset);
			yValue += 1;
			yOffset = yValue * CHARACTER_HEIGHT + 40;

			for (AbstractPage page : entry.getValue())
			{
				if (!page.shouldAppearInIndex()) {
					continue;
				}
				yValue++;
				if (yOffset >= 150) {
					xOffset += 50;
					yValue = -1;
				}

				yOffset = yValue * CHARACTER_HEIGHT + 40;
				String text = page.getHeaderText();
				int buttonID = ManualPageHandler.pages.indexOf(page) + offset;
				buttonList.add(new IndexLinkButton(buttonID, ((this.width - 256) / 2) + xOffset, yOffset, Math.round(mc.fontRenderer.getStringWidth(text) / 2.5f), CHARACTER_HEIGHT, text));
			}
		}
	}

	private boolean isViewingIndex()
	{
		return currentPageID == INDEX_PAGE_ID;
	}
}