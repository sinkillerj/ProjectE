package mapeper.projecte.neirecipecollector;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = ProjectENEIRecipeCollector.MODID, version = ProjectENEIRecipeCollector.VERSION)
public class ProjectENEIRecipeCollector
{
    public static final String MODID = "ProjectENEIRecipeCollector";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new NEIRecipeCollectorCommand());
    }
}
