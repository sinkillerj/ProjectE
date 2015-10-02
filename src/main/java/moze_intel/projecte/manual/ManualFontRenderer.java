package moze_intel.projecte.manual;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

/**
 * A shim class that very slightly modifies the behavior of the default fontrenderer. The modifed area is indicated via comment.
 */
@SideOnly(Side.CLIENT)
public class ManualFontRenderer extends FontRenderer
{
    public ManualFontRenderer()
    {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
    }

    @Override
    public List listFormattedStringToWidth(String string, int width)
    {
        return Arrays.asList(this.wrapFormStringToWidth(string, width).split("\n"));
    }

    String wrapFormStringToWidth(String str, int width)
    {
        int j = this.sizeStringToWidth(str, width);

        if (str.length() <= j)
        {
            return str;
        } else
        {
            String s1 = str.substring(0, j);
            char c0 = str.charAt(j);
            boolean flag = c0 == 10; // Changed here: Remove check for space (ascii 32)
            String s2 = getFormatFromString(s1) + str.substring(j + (flag ? 1 : 0));
            return s1 + "\n" + this.wrapFormStringToWidth(s2, width);
        }
    }

    /*
     * Copy of some fontrenderer methods because they are private in the superclass
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
                    if (l < j - 1)
                    {
                        ++l;
                        char c1 = p_78259_1_.charAt(l);

                        if (c1 != 108 && c1 != 76)
                        {
                            if (c1 == 114 || c1 == 82 || isFormatColor(c1))
                            {
                                flag = false;
                            }
                        } else
                        {
                            flag = true;
                        }
                    }

                    break;
                case 32:
                    i1 = l;
                default:
                    k += Minecraft.getMinecraft().fontRenderer.getCharWidth(c0); // Need to call it on the real fontrenderer due to state stuff >.>

                    if (flag)
                    {
                        ++k;
                    }
            }

            if (c0 == 10)
            {
                ++l;
                i1 = l;
                break;
            }

            if (k > p_78259_2_)
            {
                break;
            }
        }

        return l != j && i1 != -1 && i1 < l ? i1 : l;
    }

    private static String getFormatFromString(String p_78282_0_)
    {
        String s1 = "";
        int i = -1;
        int j = p_78282_0_.length();

        while ((i = p_78282_0_.indexOf(167, i + 1)) != -1)
        {
            if (i < j - 1)
            {
                char c0 = p_78282_0_.charAt(i + 1);

                if (isFormatColor(c0))
                {
                    s1 = "\u00a7" + c0;
                } else if (isFormatSpecial(c0))
                {
                    s1 = s1 + "\u00a7" + c0;
                }
            }
        }

        return s1;
    }

    private static boolean isFormatColor(char p_78272_0_)
    {
        return p_78272_0_ >= 48 && p_78272_0_ <= 57 || p_78272_0_ >= 97 && p_78272_0_ <= 102 || p_78272_0_ >= 65 && p_78272_0_ <= 70;
    }

    private static boolean isFormatSpecial(char p_78270_0_)
    {
        return p_78270_0_ >= 107 && p_78270_0_ <= 111 || p_78270_0_ >= 75 && p_78270_0_ <= 79 || p_78270_0_ == 114 || p_78270_0_ == 82;
    }

}