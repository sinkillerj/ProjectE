package moze_intel.projecte.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
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

    @Nonnull
    @Override
    public List<String> listFormattedStringToWidth(@Nonnull String string, int width)
    {
        return Arrays.asList(this.wrapFormStringToWidth(string, width).split("\n"));
    }

    private String wrapFormStringToWidth(String str, int width)
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

    private static boolean isFormatColor(char color)
    {
        return color >= 48 && color <= 57 || color >= 97 && color <= 102 || color >= 65 && color <= 70;
    }
}