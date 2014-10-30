package moze_intel.projecte;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.config.NBTWhitelistParser;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.RecipeMapper;
import moze_intel.projecte.events.ConnectionHandler;
import moze_intel.projecte.events.PlayerChecksEvent;
import moze_intel.projecte.events.PlayerEvents;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.ThreadCheckUpdate;
import moze_intel.projecte.network.commands.*;
import moze_intel.projecte.playerData.AlchemicalBags;
import moze_intel.projecte.playerData.IOHandler;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.proxies.CommonProxy;
import moze_intel.projecte.utils.*;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@Mod(modid = MozeCore.MODID, name = MozeCore.MODNAME, version = MozeCore.VERSION)
public class MozeCore
{	
    public static final String MODID = "ProjectE";
    public static final String MODNAME = "ProjectE";
    public static final String VERSION = "Alpha 0.2d-dev4";
    
    public static File CONFIG_DIR;
    
    @Instance(MODID)
	public static MozeCore instance;
    
    @SidedProxy(clientSide = "moze_intel.projecte.proxies.ClientProxy", serverSide = "moze_intel.projecte.proxies.CommonProxy")
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

        CustomEMCParser.init();

        NBTWhitelistParser.init();

    	PacketHandler.register();
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(MozeCore.instance, new GuiHandler());
    	MinecraftForge.EVENT_BUS.register(new PlayerEvents());
    	
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
    	AchievementHandler.init();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	ObjHandler.registerPhiloStoneSmelting();

        NBTWhitelistParser.readUserData();
    }
    
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new ChangelogCMD());
    	event.registerServerCommand(new ReloadEmcCMD());
    	event.registerServerCommand(new SetEmcCMD());
    	event.registerServerCommand(new RemoveEmcCMD());
    	event.registerServerCommand(new ResetEmcCMD());
    	event.registerServerCommand(new ClearKnowledgeCMD());
    	
    	if (!ThreadCheckUpdate.hasRunServer())
    	{
    		new ThreadCheckUpdate(true).start();
    	}
    	
        CustomEMCParser.readUserData();

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
    public void serverStopping (FMLServerStoppingEvent event)
    {
    	IOHandler.saveData();
    	PELogger.logInfo("Saved transmutation and alchemical bag data.");
    }
    
    @Mod.EventHandler
    public void serverQuit(FMLServerStoppedEvent event)
    {
    	Transmutation.clear();
    	AlchemicalBags.clear();
    	PELogger.logInfo("Cleared player data.");
    	
    	PlayerChecksEvent.clearLists();
    	PELogger.logInfo("Cleared player check-lists: server stopping.");
    	
    	EMCMapper.clearMaps();
    	PELogger.logInfo("Completed server-stop actions.");
    }
    
    @Mod.EventHandler
    public void onIMCMessage(FMLInterModComms.IMCEvent event)
    {
    	for (IMCMessage msg : event.getMessages())
    	{
    		IMCHandler.handleIMC(msg);
    	}
    }
}
