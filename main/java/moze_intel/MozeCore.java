package moze_intel;

import java.io.File;

import moze_intel.EMC.EMCMapper;
import moze_intel.EMC.RecipeMapper;
import moze_intel.config.FileHelper;
import moze_intel.config.ProjectEConfig;
import moze_intel.events.ConnectionHandler;
import moze_intel.events.PlayerChecksEvent;
import moze_intel.events.RegisterPropertiesEvent;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.network.PacketHandler;
import moze_intel.network.ThreadCheckUpdate;
import moze_intel.network.commands.AddEmcCMD;
import moze_intel.network.commands.ChangelogCMD;
import moze_intel.network.commands.ClearKnowledgeCMD;
import moze_intel.network.commands.ReloadCfgCMD;
import moze_intel.network.commands.RemoveEmcCMD;
import moze_intel.network.commands.ResetEmcCMD;
import moze_intel.playerData.AlchemicalBagData;
import moze_intel.playerData.IOHandler;
import moze_intel.playerData.TransmutationKnowledge;
import moze_intel.proxies.CommonProxy;
import moze_intel.utils.GuiHandler;
import moze_intel.utils.NeiHelper;
import moze_intel.utils.PELogger;
import moze_intel.utils.Utils;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = MozeCore.MODID, name = MozeCore.MODNAME, version = MozeCore.VERSION)
public class MozeCore
{	
    public static final String MODID = "ProjectE";
    public static final String MODNAME = "ProjectE";
    public static final String VERSION = "Alpha 0.2a";
    
    public static File CONFIG_DIR;
    
    @Instance(MODID)
	public static MozeCore instance;
    
    @SidedProxy(clientSide="moze_intel.proxies.ClientProxy", serverSide="moze_intel.proxies.CommonProxy")
	public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {	
    	CONFIG_DIR = new File(event.getModConfigurationDirectory(), "ProjectE");
    	
    	if (!CONFIG_DIR.exists())
    	{
    		CONFIG_DIR.mkdirs();
    	}
    	
    	ProjectEConfig.init(new File(CONFIG_DIR, "ProjectE.cfg"));
    	FileHelper.init();
    	
    	PacketHandler.register();
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(MozeCore.instance, new GuiHandler());
    	MinecraftForge.EVENT_BUS.register(new moze_intel.events.ItemPickupEvent());
    	MinecraftForge.EVENT_BUS.register(new RegisterPropertiesEvent());
    	
    	FMLCommonHandler.instance().bus().register(new PlayerChecksEvent());
    	FMLCommonHandler.instance().bus().register(new ConnectionHandler());
    	
    	proxy.registerClientOnlyEvents();
    	
    	ObjHandler.register();
    	ObjHandler.addRecipes();
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	proxy.registerKeyBinds();
    	proxy.registerRenderers();
    	
    	Utils.init();
    	NeiHelper.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new ChangelogCMD());
    	event.registerServerCommand(new ReloadCfgCMD());
    	event.registerServerCommand(new AddEmcCMD());
    	event.registerServerCommand(new RemoveEmcCMD());
    	event.registerServerCommand(new ResetEmcCMD());
    	event.registerServerCommand(new ClearKnowledgeCMD());
    	
    	if (!ThreadCheckUpdate.hasRunServer())
    	{
    		new ThreadCheckUpdate(true).start();
    	}
    	
    	FileHelper.readUserData();
    	
    	PELogger.logInfo("Starting server-side EMC mapping.");
    	
    	RecipeMapper.map();
    	EMCMapper.map();
    	
    	PELogger.logInfo("Registered " + EMCMapper.emc.size() + " EMC values.");
    	
    	File dir = new File(event.getServer().getEntityWorld().getSaveHandler().getWorldDirectory(), "ProjectE");
    	
    	if (!dir.exists())
    	{
    		dir.mkdirs(); 
    	}
    	
    	IOHandler.init(new File(dir, "knowledge.dat"), new File(dir, "bagdata.dat"));
    }
    
    @Mod.EventHandler
    public void serverQuit(FMLServerStoppedEvent event)
    {
    	IOHandler.saveData();
    	TransmutationKnowledge.clear();
    	AlchemicalBagData.clear();
    	PELogger.logInfo("Saved player data.");
    	
    	PlayerChecksEvent.clearLists();
    	PELogger.logInfo("Cleared player check-lists: server stopping.");
    	
    	EMCMapper.clearMaps();
    	PELogger.logInfo("Completed server-stop actions.");
    }
}
