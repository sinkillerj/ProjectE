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

  private final int CHARACTER_HEIGHT = Math.round(9);
  private int indexPages = 1;
  private int currentPageID;
  private int offset = 3;
  private int indexPageID = 0;
  private int entriesPerPage = 0;
  private int indexEntries = 0;

  public static List<String> bodyTexts = Lists.newArrayList();
  public static int windowWidth = 256;
  public static int textWidth = 145;
  public static int pageHeight = 180;
  public static int textHeight = pageHeight - 43 - 10;
  public static int textYOffset = 10;
  public static float guiScaleFactor = 1.5f;
  private static boolean firstRun = true;

  @Override
  public void initGui()
  {
    ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

    width = scaledresolution.getScaledWidth();

    int i = (this.width - windowWidth) / 2;

    this.buttonList.add(new PageTurnButton(0, Math.round((i + 210) * (guiScaleFactor * 0.75f)), 160, true));
    this.buttonList.add(new PageTurnButton(1, Math.round((i + 16) / guiScaleFactor), 160, false));

    String text = StatCollector.translateToLocal("pe.manual.index_button");
    int stringWidth = mc.fontRenderer.getStringWidth(text);

    this.buttonList.add(new TocButton(2, (this.width / 2) - (stringWidth / 2), 192, stringWidth, 15, text));

    entriesPerPage = (int) Math.floor(textHeight / CHARACTER_HEIGHT) - 2;
    indexPages = (int) Math.ceil(ManualPageHandler.pages.size() / entriesPerPage);

    addIndexButtons(((Math.round(this.width / guiScaleFactor) - windowWidth) / 2) + 40);

    if(firstRun)
      indexPageID -= indexPages;
    currentPageID = indexPageID;
    firstRun = false;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks)
  {
    System.out.println("PEDEBUG:" + currentPageID);
    ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    if(isViewingIndex())
    {
      this.mc.getTextureManager().bindTexture(tocTexture);
    } else
    {
      this.mc.getTextureManager().bindTexture(bookTexture);
    }

    GL11.glScalef(guiScaleFactor, 1, guiScaleFactor);
    int k = (Math.round(this.width / guiScaleFactor) - windowWidth) / 2;
    this.drawTexturedModalRect(k, 5, 0, 0, windowWidth, pageHeight);
    GL11.glScalef(1 / guiScaleFactor, 1, 1 / guiScaleFactor);

    if(!isViewingIndex())
    {
      AbstractPage currentPage = ManualPageHandler.pages.get(currentPageID);
      AbstractPage nextPage = ManualPageHandler.pages.get(currentPageID + 1);

      if(currentPage != null)
        drawPage(scaledresolution, currentPage, k + 40, k + 20, 0);
      if(nextPage != null)
        drawPage(scaledresolution, nextPage, k + 160, k + 140, 0);
    } else
    {

      this.fontRendererObj.drawString(StatCollector.translateToLocal("pe.manual.index"), k + 40, 27, 0, false);
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
    case 0:
      if(currentPageID != -1)
        currentPageID += 2;
      else
        currentPageID++;
      break;
    case 1:
      if(currentPageID != 0)
        currentPageID -= 2;
      else
        currentPageID--;
      break;
    case 2:
      currentPageID = indexPageID;
      break;
    default:
      currentPageID = button.id - 3 - ((button.id - 3) % 2);
    }
    this.updateButtons();
  }

  private void updateButtons()
  {
    if(isViewingIndex())
    {
      ((PageTurnButton) this.buttonList.get(0)).visible = true;
      if(currentPageID != indexPageID)
        ((PageTurnButton) this.buttonList.get(1)).visible = true;
      else
        ((PageTurnButton) this.buttonList.get(1)).visible = false;
      ((TocButton) this.buttonList.get(2)).visible = false;
      for (int i = 3; i < this.buttonList.size(); i++)
      {
        if(i > (entriesPerPage * ((indexPages + 1 - Math.abs(currentPageID)) - 1)) + offset &&
            i <= (entriesPerPage * (indexPages + 1 - Math.abs(currentPageID + 1))) + offset)
        {
          ((IndexLinkButton) this.buttonList.get(i)).visible = true;
        } else
          ((IndexLinkButton) this.buttonList.get(i)).visible = false;
      }
    } else if(this.currentPageID >= ManualPageHandler.pages.size() - 2)
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
      if(visible)
      {
        drawRect(xPosition, yPosition, (xPosition + width), (yPosition + height), 0);
        mc.fontRenderer.drawString(displayString, Math.round(xPosition), Math.round(yPosition), 0);
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
      if(this.visible)
      {
        boolean hover = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(bookGui);
        int u = 0;
        int v = 192;

        if(hover)
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
    if(fixLighting)
    {
      GL11.glEnable(GL11.GL_LIGHTING);
    }

    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, x, y);

    if(fixLighting)
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

  public void drawCategory(String name, int xOffset, int yOffset, int x)
  {
    mc.fontRenderer.drawString(EnumChatFormatting.BOLD + (EnumChatFormatting.UNDERLINE + name), Math.round(x * guiScaleFactor) / 2 + xOffset, yOffset, 0);
  }

  public void addIndexButtons(int x)
  {

    int yOffset = 37;
    int side = 0;
    int sideWas = 0;
    int skipped = 0;
    x *= guiScaleFactor;

    for (AbstractPage page : ManualPageHandler.pages)
    {
      if(!page.shouldAppearInIndex()) {
        skipped++;
        continue;
      }

      if(side != sideWas)
      {
        yOffset = 37;
        if(side == 1)
          x += 160 * guiScaleFactor;
        else
          x -= 160 * guiScaleFactor;
        sideWas = side;
      }

      String text = page.getHeaderText();
      int buttonID = ManualPageHandler.pages.indexOf(page) + offset;
      addIndexButton(buttonID, x, yOffset, text);
      yOffset += CHARACTER_HEIGHT + 1;
      side = ((ManualPageHandler.pages.indexOf(page) - skipped) / (entriesPerPage)) % 2;
    }

  }

  private boolean isViewingIndex()
  {
    return currentPageID < 0;
  }

  private void addIndexButton(int buttonID, int x, int yOffset, String text)
  {
    buttonList.add(new IndexLinkButton(buttonID, Math.round((x * guiScaleFactor) / 2), yOffset, Math.round(mc.fontRenderer.getStringWidth(text)),
        CHARACTER_HEIGHT, text));
  }

  // Header = k+40, k+160, Image/Text = k+20, k+140
  public void drawPage(ScaledResolution scaledresolution, AbstractPage page, int headerX, int contentX, int side)
  {
    this.fontRendererObj.drawString(page.getHeaderText(), Math.round(headerX * guiScaleFactor), 27, 0, false);

    if(page instanceof ImagePage)
    {
      drawImage(((ImagePage) page).getImageLocation(), Math.round(contentX * guiScaleFactor * 2), 80);
    } else
    {

      bodyTexts = page.getBodyList();

      for (int i = 0; i < bodyTexts.size() && i < Math.floor(GUIManual.textHeight / GUIManual.textYOffset); i++)
      {
        this.fontRendererObj.drawString(bodyTexts.get(i), Math.round(contentX * guiScaleFactor), 43 + textYOffset * i, Color.black.getRGB());
      }

      if(page instanceof ItemPage)
      {
        ItemPage itemPage = ((ItemPage) page);
        drawItemStackToGui(mc, itemPage.getItemStack(), Math.round(contentX * guiScaleFactor), 22, !(itemPage.getItemStack().getItem() instanceof ItemBlock)
            || ItemHelper.areItemStacksEqual(itemPage.getItemStack(), new ItemStack(ObjHandler.confuseTorch)));
      }

    }

  }
}