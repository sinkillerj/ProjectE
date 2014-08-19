package moze_intel.proxies;

import moze_intel.events.FovChangeEvent;
import moze_intel.events.KeyPressEvent;
import moze_intel.events.ToolTipEvent;
import moze_intel.events.TransmutationRenderingEvent;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.entity.LavaProjectile;
import moze_intel.gameObjs.entity.LensProjectile;
import moze_intel.gameObjs.entity.LootBall;
import moze_intel.gameObjs.entity.MobRandomizer;
import moze_intel.gameObjs.entity.NovaCataclysmPrimed;
import moze_intel.gameObjs.entity.NovaCatalystPrimed;
import moze_intel.gameObjs.entity.WaterProjectile;
import moze_intel.gameObjs.tiles.AlchChestTile;
import moze_intel.gameObjs.tiles.CondenserTile;
import moze_intel.rendering.ChestItemRenderer;
import moze_intel.rendering.ChestRenderer;
import moze_intel.rendering.CondenserItemRenderer;
import moze_intel.rendering.CondenserRenderer;
import moze_intel.rendering.NovaCataclysmRenderer;
import moze_intel.rendering.NovaCatalystRenderer;
import moze_intel.utils.KeyBinds;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy
{	
	public void registerKeyBinds()
	{
		for (int i = 0; i < KeyBinds.array.length; i++)
		{
			ClientRegistry.registerKeyBinding(KeyBinds.array[i]);
		}
	}

	@Override
	public void registerRenderers() 
	{
		//Items
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ObjHandler.alchChest), new ChestItemRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ObjHandler.condenser), new CondenserItemRenderer());
		
		//Blocks
		ClientRegistry.bindTileEntitySpecialRenderer(AlchChestTile.class, new ChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserTile.class, new CondenserRenderer());
		
		//Entities
		RenderingRegistry.registerEntityRenderingHandler(WaterProjectile.class, new RenderSnowball(ObjHandler.waterOrb));
		RenderingRegistry.registerEntityRenderingHandler(LavaProjectile.class, new RenderSnowball(ObjHandler.lavaOrb));
		RenderingRegistry.registerEntityRenderingHandler(LootBall.class, new RenderSnowball(ObjHandler.lootBall));
		RenderingRegistry.registerEntityRenderingHandler(MobRandomizer.class, new RenderSnowball(ObjHandler.mobRandomizer));
		RenderingRegistry.registerEntityRenderingHandler(LensProjectile.class, new RenderSnowball(ObjHandler.lensExplosive));
		RenderingRegistry.registerEntityRenderingHandler(NovaCatalystPrimed.class, new NovaCatalystRenderer());
		RenderingRegistry.registerEntityRenderingHandler(NovaCataclysmPrimed.class, new NovaCataclysmRenderer());
	}
	
	@Override
	public void registerClientOnlyEvents() 
	{
		MinecraftForge.EVENT_BUS.register(new FovChangeEvent());
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
		MinecraftForge.EVENT_BUS.register(new TransmutationRenderingEvent());
		FMLCommonHandler.instance().bus().register(new KeyPressEvent());
	}
}

