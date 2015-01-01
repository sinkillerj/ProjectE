package moze_intel.projecte.config.gui;

import cpw.mods.fml.client.config.GuiConfig;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

public class ProjectEConfigGui extends GuiConfig
{
    public ProjectEConfigGui(GuiScreen guiScreen)
    {
        super(guiScreen, new ConfigElement(ProjectEConfig.config.getCategory(ProjectEConfig.MISC_CATEGORY)).getChildElements(), PECore.MODID, false, false, GuiConfig.getAbridgedConfigPath(ProjectEConfig.config.toString()));
        this.configElements.add(new ConfigElement(ProjectEConfig.config.getCategory(ProjectEConfig.BLOCKS_CATEGORY)));
    }
}
