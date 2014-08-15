package moze_intel;

import moze_intel.EMC.RecipeMapper;
import moze_intel.events.ConnectionHandler;
import moze_intel.events.PlayerChecksEvent;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.network.packets.ClientSyncPKT;
import moze_intel.network.packets.KeyPressPKT;
import moze_intel.network.packets.ParticlePKT;
import moze_intel.network.packets.SetFlyPKT;
import moze_intel.network.packets.StepHeightPKT;
import moze_intel.network.packets.SwingItemPKT;
import moze_intel.network.packets.ClientSyncPKT.ClientSyncHandler;
import moze_intel.proxies.CommonProxy;
import moze_intel.utils.Constants;
import moze_intel.utils.FileHelper;
import moze_intel.utils.GuiHandler;
import moze_intel.utils.KnowledgeHandler;
import moze_intel.utils.MozeLogger;
import moze_intel.utils.Utils;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
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
    public static final String VERSION = "Alpha 0.1a";
    public static final MozeLogger logger = new MozeLogger();
    
    @Instance(MODID)
	public static MozeCore instance;
    
    @SidedProxy(clientSide="moze_intel.proxies.ClientProxy", serverSide="moze_intel.proxies.CommonProxy")
	public static CommonProxy proxy;
    
    public static SimpleNetworkWrapper pktHandler;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	pktHandler = NetworkRegistry.INSTANCE.newSimpleChannel("projecte");
    	pktHandler.registerMessage(ClientSyncHandler.class, ClientSyncPKT.class, 0, Side.CLIENT);
    	pktHandler.registerMessage(KeyPressPKT.class, KeyPressPKT.class, 1, Side.SERVER);
    	pktHandler.registerMessage(ParticlePKT.class, ParticlePKT.class, 2, Side.CLIENT);
    	pktHandler.registerMessage(SwingItemPKT.class, SwingItemPKT.class, 3, Side.CLIENT);
    	pktHandler.registerMessage(StepHeightPKT.class, StepHeightPKT.class, 4, Side.CLIENT);
    	pktHandler.registerMessage(SetFlyPKT.class, SetFlyPKT.class, 5, Side.CLIENT);
    	
    	Constants.init();
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(MozeCore.instance, new GuiHandler());
    	MinecraftForge.EVENT_BUS.register(new moze_intel.events.ItemPickupEvent());
    	
    	FMLCommonHandler.instance().bus().register(new PlayerChecksEvent());
    	FMLCommonHandler.instance().bus().register(new ConnectionHandler());
    	
    	proxy.RegisterClientOnlyEvents();
    	
    	ObjHandler.Register();
    	ObjHandler.AddRecipes();
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	proxy.RegisterKeyBinds();
    	proxy.RegisterRenderers();
    	Utils.init();
    	new ThreadCheckPEUpdate().start();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	System.out.println("SERVER: PRE-INIT emc mapping.");
    	
    	RecipeMapper.map();
    	moze_intel.EMC.EMCMapper.map();
    }
    
    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event)
    {
    	FileHelper.getWorldDir();
    	KnowledgeHandler.load();
    	logger.logInfo("Loading player transmutation knowledge.");
    }
    
    @Mod.EventHandler
    public void serverQuit(FMLServerStoppedEvent event)
    {
    	PlayerChecksEvent.clearLists();
    	logger.logInfo("Cleared player check-lists: server stopping.");
    	
    	KnowledgeHandler.save();
    	
    	while (FileHelper.saving)
    	{
    		//postpone quitting
    	}
    	
    	logger.logInfo("Saved player transmutation knowledge.");
    }
}
