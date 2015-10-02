package moze_intel.projecte.gameObjs.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.manual.AbstractPage;
import moze_intel.projecte.manual.ImagePage;
import moze_intel.projecte.manual.IndexPage;
import moze_intel.projecte.manual.ItemPage;
import moze_intel.projecte.manual.ManualFontRenderer;
import moze_intel.projecte.manual.ManualPageHandler;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GUIManual extends GuiScreen
{
	public static final int WINDOW_WIDTH = 256;
	public static final int TEXT_WIDTH = 145;
	public static final int PAGE_HEIGHT = 226;
	public static final int TEXT_HEIGHT = PAGE_HEIGHT - 43 - 20;
	public static final int TEXT_Y_OFFSET = 10;
	public static final float GUI_SCALE_FACTOR = 1.5f;
	public static final int BUTTON_HEIGHT = 13;
	private static final int CHARACTER_HEIGHT = 9;
	private static final int BUTTON_ID_OFFSET = 3; // Offset of button ID's due to the page turn and TOC buttons
	private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation("projecte:textures/gui/bookTexture.png");
	private static final ManualFontRenderer peFontRenderer = new ManualFontRenderer();
	public static final int ENTRIES_PER_PAGE = TEXT_HEIGHT / CHARACTER_HEIGHT - 2; // Number of entries per index page
	public static final Multimap<IndexPage, IndexLinkButton> indexLinks = ArrayListMultimap.create(); // IndexPage -> IndexLinkButtons
	private static ResourceLocation bookGui = new ResourceLocation("textures/gui/book.png");
	public List<String> bodyTexts = Lists.newArrayList();
	private int currentSpread;
	private int k;

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

	@SuppressWarnings("unchecked")
	public static List<String> splitBody(String s)
	{
		return peFontRenderer.listFormattedStringToWidth(s, TEXT_WIDTH);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		GL11.glScalef(GUI_SCALE_FACTOR, 1, GUI_SCALE_FACTOR);
		k = (Math.round(this.width / GUI_SCALE_FACTOR) - WINDOW_WIDTH) / 2;
		GL11.glScalef(1 / GUI_SCALE_FACTOR, 1, 1 / GUI_SCALE_FACTOR);

		this.buttonList.add(new PageTurnButton(0, Math.round((k + 256 - 40) * GUI_SCALE_FACTOR), PAGE_HEIGHT - Math.round(BUTTON_HEIGHT * 1.4f), true));
		this.buttonList.add(new PageTurnButton(1, Math.round((k + 20) * GUI_SCALE_FACTOR), PAGE_HEIGHT - Math.round(BUTTON_HEIGHT * 1.4f), false));

		String text = StatCollector.translateToLocal("pe.manual.index_button");
		int stringWidth = mc.fontRenderer.getStringWidth(text);
		this.buttonList.add(new TocButton(2, (this.width / 2) - (stringWidth / 2), PAGE_HEIGHT - Math.round(BUTTON_HEIGHT * 1.3f), stringWidth, 15, text));

		addIndexButtons(Math.round((k + 20) * GUI_SCALE_FACTOR));
		currentSpread = 0;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(BOOK_TEXTURE);

		GL11.glScalef(GUI_SCALE_FACTOR, 1, GUI_SCALE_FACTOR);
		this.drawTexturedModalRect(k, 5, 0, 0, WINDOW_WIDTH, PAGE_HEIGHT);
		GL11.glScalef(1 / GUI_SCALE_FACTOR, 1, 1 / GUI_SCALE_FACTOR);

		AbstractPage currentPage = ManualPageHandler.spreads.get(currentSpread).getLeft();
		AbstractPage nextPage = ManualPageHandler.spreads.get(currentSpread).getRight();

		if (currentPage != null)
			drawPage(currentPage, k + 40, k + 20);
		if (nextPage != null)
			drawPage(nextPage, k + 160, k + 140);

		this.updateButtons();

		for (GuiButton button : ((List<GuiButton>) this.buttonList))
		{
			button.drawButton(this.mc, mouseX, mouseY);
		}

	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		switch (button.id)
		{
			case 0:
				currentSpread++;
				break;
			case 1:
				currentSpread--;
				break;
			case 2:
				currentSpread = 0;
				break;
			default:
				int val = Math.round((button.id - 3) / 2.0F);
				PELogger.logDebug("Clicked button %d which is supposed to be page %d, taking you to spread %d which has page %d on the left", button.id, button.id - 3, val, ManualPageHandler.pages.indexOf(ManualPageHandler.spreads.get(val).getLeft()));
				currentSpread = val;
		}
		this.updateButtons();
	}

	private void updateButtons()
	{
		if (isViewingIndex())
		{
			((PageTurnButton) this.buttonList.get(0)).visible = true;
			((PageTurnButton) this.buttonList.get(1)).visible = currentSpread != 0;
			((TocButton) this.buttonList.get(2)).visible = false;
			for (int i = 3; i < this.buttonList.size(); i++)
			{
				Pair<AbstractPage, AbstractPage> spread = ManualPageHandler.spreads.get(currentSpread);

				// Display if the indexLinks map has this button for the current spread, handling nulls on the right as necessary
				((IndexLinkButton) buttonList.get(i)).visible = indexLinks.get(((IndexPage) spread.getLeft())).contains(buttonList.get(i))
						|| (spread.getRight() != null && indexLinks.get(((IndexPage) spread.getRight())).contains(buttonList.get(i)));
			}
		} else if (currentSpread == ManualPageHandler.spreads.size() - 1)
		{
			((PageTurnButton) this.buttonList.get(0)).visible = false;
			((PageTurnButton) this.buttonList.get(1)).visible = true;
			((TocButton) this.buttonList.get(2)).visible = true;
			for (int i = 3; i < this.buttonList.size(); i++)
			{
				((IndexLinkButton) this.buttonList.get(i)).visible = false;
			}
		} else
		{
			((PageTurnButton) this.buttonList.get(0)).visible = true;
			((PageTurnButton) this.buttonList.get(1)).visible = true;
			((TocButton) this.buttonList.get(2)).visible = true;
			for (int i = 3; i < this.buttonList.size(); i++)
			{
				((IndexLinkButton) this.buttonList.get(i)).visible = false;
			}
		}
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

	@SuppressWarnings("unchecked")
	private void addIndexButtons(int x)
	{
		int yOffset = 42;

		Iterator<IndexPage> iter = ManualPageHandler.indexPages.iterator();
		IndexPage addingTo = iter.next();
		int entriesOnCurrentPage = 0;

		for (AbstractPage page : ManualPageHandler.pages)
		{
			if (!page.shouldAppearInIndex())
			{
				continue;
			}

			if (entriesOnCurrentPage == ENTRIES_PER_PAGE)
			{
				// Reset when changing pages
				entriesOnCurrentPage = 0;
				addingTo = iter.next();
				if (ManualPageHandler.indexPages.indexOf(addingTo) % 2 == 0)
				{
					x -= 175; // Left
				} else
				{
					x += 175; // Right
				}
				yOffset = 42;
			}

			String text = page.getHeaderText();
			int buttonID = ManualPageHandler.pages.indexOf(page) + BUTTON_ID_OFFSET;
			IndexLinkButton button = new IndexLinkButton(buttonID, x, yOffset, mc.fontRenderer.getStringWidth(text),
					CHARACTER_HEIGHT, text);
			buttonList.add(button);
			indexLinks.put(addingTo, button);

			entriesOnCurrentPage++;
			yOffset += CHARACTER_HEIGHT + 1;
		}

	}

	private boolean isViewingIndex()
	{
		return ManualPageHandler.spreads.get(currentSpread).getLeft() instanceof IndexPage;
	}

	// Header = k+40, k+160, Image/Text = k+20, k+140
	public void drawPage(AbstractPage page, int headerX, int contentX)
	{
		this.fontRendererObj.drawString(page.getHeaderText(), Math.round(headerX * GUI_SCALE_FACTOR), 27, 0, false);

		if (page instanceof IndexPage)
		{
			// Noop
		} else if (page instanceof ImagePage)
		{
			drawImage(((ImagePage) page).getImageLocation(), Math.round(contentX * GUI_SCALE_FACTOR * 2), 80);
		} else
		{
			bodyTexts = splitBody(page.getBodyText());

			for (int i = 0; i < bodyTexts.size() && i < Math.floor(GUIManual.TEXT_HEIGHT / GUIManual.TEXT_Y_OFFSET); i++)
			{
				this.fontRendererObj.drawString(bodyTexts.get(i).charAt(0) == 32 ? bodyTexts.get(i).substring(1) : bodyTexts.get(i),
						Math.round(contentX * GUI_SCALE_FACTOR), 43 + TEXT_Y_OFFSET * i, Color.black.getRGB());
			}

			if (page instanceof ItemPage)
			{
				ItemPage itemPage = ((ItemPage) page);
				drawItemStackToGui(mc, itemPage.getItemStack(), Math.round(contentX * GUI_SCALE_FACTOR), 22, !(itemPage.getItemStack().getItem() instanceof ItemBlock)
						|| itemPage.getItemStack().getItem() == Item.getItemFromBlock(ObjHandler.confuseTorch));
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
				mc.fontRenderer.drawString(displayString, Math.round(xPosition), Math.round(yPosition), 0);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static class PageTurnButton extends GuiButton
	{
		private static final int bWidth = 23;
		private boolean pointsRight;

		public PageTurnButton(int ID, int xPos, int yPos, boolean par4)
		{
			super(ID, xPos, yPos, bWidth, BUTTON_HEIGHT, "");
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

				if (!pointsRight)
				{
					v += BUTTON_HEIGHT;
				}
				GL11.glEnable(GL11.GL_BLEND);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v, bWidth, BUTTON_HEIGHT);
				GL11.glDisable(GL11.GL_BLEND);
			}
		}
	}
}
