package moze_intel.projecte.manual;

public enum PageCategory
{
    NONE("none"),
    ITEM("items"),
    BLOCK("blocks"),
    TOOLS("tools"),
    ARMOR("armors"),
    MUSTFIGUREOUTTHERESTOFTHESE("kappa");

    public final String unlocalName;

    PageCategory(String unlocalName)
    {
        this.unlocalName = unlocalName;
    }
}
