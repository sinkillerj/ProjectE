package moze_intel.projecte.utils;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class PEFontRenderer extends net.minecraft.client.gui.FontRenderer {

  private FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;

  public PEFontRenderer() {
    super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
  }

  @Override
  public List listFormattedStringToWidth(String p_78271_1_, int p_78271_2_)
  {
    return Arrays.asList(this.wrapFormStringToWidth(p_78271_1_, p_78271_2_).split("\n"));
  }

  String wrapFormStringToWidth(String p_78280_1_, int p_78280_2_)
  {
    int j = this.sizeStringToWidth(p_78280_1_, p_78280_2_);

    if(p_78280_1_.length() <= j)
    {
      return p_78280_1_;
    }
    else
    {
      String s1 = p_78280_1_.substring(0, j);
      char c0 = p_78280_1_.charAt(j);
      boolean flag = c0 == 10;
      String s2 = this.getFormatFromString(s1) + p_78280_1_.substring(j + (flag ? 1 : 0));
      return s1 + "\n" + this.wrapFormStringToWidth(s2, p_78280_2_);
    }
  }

  /**
   * Determines how many characters from the string will fit into the specified
   * width.
   */
  private int sizeStringToWidth(String p_78259_1_, int p_78259_2_)
  {
    int j = p_78259_1_.length();
    int k = 0;
    int l = 0;
    int i1 = -1;

    for (boolean flag = false; l < j; ++l)
    {
      char c0 = p_78259_1_.charAt(l);

      switch (c0)
      {
      case 10:
        --l;
        break;
      case 167:
        if(l < j - 1)
        {
          ++l;
          char c1 = p_78259_1_.charAt(l);

          if(c1 != 108 && c1 != 76)
          {
            if(c1 == 114 || c1 == 82 || isFormatColor(c1))
            {
              flag = false;
            }
          }
          else
          {
            flag = true;
          }
        }

        break;
      case 32:
        i1 = l;
      default:
        k += fontRendererObj.getCharWidth(c0);

        if(flag)
        {
          ++k;
        }
      }

      if(c0 == 10)
      {
        ++l;
        i1 = l;
        break;
      }

      if(k > p_78259_2_)
      {
        break;
      }
    }

    return l != j && i1 != -1 && i1 < l ? i1 : l;
  }

  /**
   * Digests a string for nonprinting formatting characters then returns a
   * string containing only that formatting.
   */
  private static String getFormatFromString(String p_78282_0_)
  {
    String s1 = "";
    int i = -1;
    int j = p_78282_0_.length();

    while ((i = p_78282_0_.indexOf(167, i + 1)) != -1)
    {
      if(i < j - 1)
      {
        char c0 = p_78282_0_.charAt(i + 1);

        if(isFormatColor(c0))
        {
          s1 = "\u00a7" + c0;
        }
        else if(isFormatSpecial(c0))
        {
          s1 = s1 + "\u00a7" + c0;
        }
      }
    }

    return s1;
  }

  /**
   * Checks if the char code is a hexadecimal character, used to set colour.
   */
  private static boolean isFormatColor(char p_78272_0_)
  {
    return p_78272_0_ >= 48 && p_78272_0_ <= 57 || p_78272_0_ >= 97 && p_78272_0_ <= 102 || p_78272_0_ >= 65 && p_78272_0_ <= 70;
  }

  /**
   * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
   */
  private static boolean isFormatSpecial(char p_78270_0_)
  {
    return p_78270_0_ >= 107 && p_78270_0_ <= 111 || p_78270_0_ >= 75 && p_78270_0_ <= 79 || p_78270_0_ == 114 || p_78270_0_ == 82;
  }

}