package moze_intel.projecte.manual;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum PageCategory
{
    INDEX("index"),
    NONE("none"),
    ITEM("items"),
    BLOCK("blocks"),
    TOOLS("tools"),
    ARMOR("armors"),
    MUSTFIGUREOUTTHERESTOFTHESE("kappa");

    private final String identifier;

    PageCategory(String identifier)
    {
        this.identifier = identifier;
    }

    public String getUnlocalName()
    {
        return "pe.manual.category." + identifier;
    }
}
