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
import moze_intel.network.ThreadCheckUpdate;
import moze_intel.network.commands.ChangelogCMD;
import moze_intel.network.commands.ReloadCfgCMD;
import moze_intel.network.packets.ClientCheckUpdatePKT;
import moze_intel.network.packets.ClientKnowledgeSyncPKT;
import moze_intel.network.packets.ClientSyncBagDataPKT;
import moze_intel.network.packets.ClientSyncPKT;
import moze_intel.network.packets.CollectorSyncPKT;
import moze_intel.network.packets.CondenserSyncPKT;
import moze_intel.network.packets.KeyPressPKT;
import moze_intel.network.packets.ParticlePKT;
import moze_intel.network.packets.RelaySyncPKT;
import moze_intel.network.packets.SetFlyPKT;
import moze_intel.network.packets.StepHeightPKT;
import moze_intel.network.packets.SwingItemPKT;
import moze_intel.network.packets.TTableSyncPKT;
import moze_intel.proxies.CommonProxy;
import moze_intel.utils.Constants;
import moze_intel.utils.GuiHandler;
import moze_intel.utils.MozeLogger;
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
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = MozeCore.MODID, name = MozeCore.MODNAME, version = MozeCore.VERSION)
public class MozeCore
{	
    public static final String MODID = "ProjectE";
    public static final String MODNAME = "ProjectE";
    public static final String VERSION = "Alpha 0.1j";
    public static final MozeLogger logger = new MozeLogger();
    
    public static File CONFIG_DIR;
    
    @Instance(MODID)
	public static MozeCore instance;
    
    @SidedProxy(clientSide="moze_intel.proxies.ClientProxy", serverSide="moze_intel.proxies.CommonProxy")
	public static CommonProxy proxy;
    
    public static SimpleNetworkWrapper pktHandler;
    
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
    	
    	pktHandler = NetworkRegistry.INSTANCE.newSimpleChannel("projecte");
    	pktHandler.registerMessage(ClientSyncPKT.class, ClientSyncPKT.class, 0, Side.CLIENT);
    	pktHandler.registerMessage(KeyPressPKT.class, KeyPressPKT.class, 1, Side.SERVER);
    	pktHandler.registerMessage(ParticlePKT.class, ParticlePKT.class, 2, Side.CLIENT);
    	pktHandler.registerMessage(SwingItemPKT.class, SwingItemPKT.class, 3, Side.CLIENT);
    	pktHandler.registerMessage(StepHeightPKT.class, StepHeightPKT.class, 4, Side.CLIENT);
    	pktHandler.registerMessage(SetFlyPKT.class, SetFlyPKT.class, 5, Side.CLIENT);
    	pktHandler.registerMessage(ClientKnowledgeSyncPKT.class, ClientKnowledgeSyncPKT.class, 6, Side.CLIENT);
    	pktHandler.registerMessage(TTableSyncPKT.class, TTableSyncPKT.class, 7, Side.CLIENT);
    	pktHandler.registerMessage(CondenserSyncPKT.class, CondenserSyncPKT.class, 8, Side.CLIENT);
    	pktHandler.registerMessage(CollectorSyncPKT.class, CollectorSyncPKT.class, 9, Side.CLIENT);
    	pktHandler.registerMessage(RelaySyncPKT.class, RelaySyncPKT.class, 10, Side.CLIENT);
    	pktHandler.registerMessage(ClientCheckUpdatePKT.class, ClientCheckUpdatePKT.class, 11, Side.CLIENT);
    	pktHandler.registerMessage(ClientSyncBagDataPKT.class, ClientSyncBagDataPKT.class, 12, Side.CLIENT);
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(MozeCore.instance, new GuiHandler());
    	MinecraftForge.EVENT_BUS.register(new moze_intel.events.ItemPickupEvent());
    	MinecraftForge.EVENT_BUS.register(new RegisterPropertiesEvent());
    	
    	FMLCommonHandler.instance().bus().register(new PlayerChecksEvent());
    	FMLCommonHandler.instance().bus().register(new ConnectionHandler());
    	
    	proxy.registerClientOnlyEvents();
    	
    	ObjHandler.register();
    	ObjHandler.addRecipes();
    	
    	Constants.init();
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	proxy.registerKeyBinds();
    	proxy.registerRenderers();
    	Utils.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new ChangelogCMD());
    	event.registerServerCommand(new ReloadCfgCMD());
    	
    	if (!ThreadCheckUpdate.hasRunServer())
    	{
    		new ThreadCheckUpdate(true).start();
    	}
    	
    	FileHelper.readUserData();
    	
    	logger.logInfo("Starting server-side EMC mapping.");
    	
    	RecipeMapper.map();
    	EMCMapper.map();
    	
    	logger.logInfo("Registered "+EMCMapper.emc.size()+" EMC values.");
    }
    
    @Mod.EventHandler
    public void serverQuit(FMLServerStoppedEvent event)
    {
    	PlayerChecksEvent.clearLists();
    	logger.logInfo("Cleared player check-lists: server stopping.");
    	
    	EMCMapper.clearMap();
    	proxy.clearAllKnowledge();
    	proxy.clearAllBagData();
    	
    	logger.logInfo("Completed server-stop actions.");
    }
}
