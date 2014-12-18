package sinkillerj.projecte.proxies;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import sinkillerj.projecte.events.FovChangeEvent;
import sinkillerj.projecte.events.KeyPressEvent;
import sinkillerj.projecte.events.ToolTipEvent;
import sinkillerj.projecte.events.TransmutationRenderingEvent;
import sinkillerj.projecte.gameObjs.ObjHandler;
import sinkillerj.projecte.gameObjs.entity.*;
import sinkillerj.projecte.gameObjs.tiles.AlchChestTile;
import sinkillerj.projecte.gameObjs.tiles.CondenserMK2Tile;
import sinkillerj.projecte.gameObjs.tiles.CondenserTile;
import sinkillerj.projecte.rendering.*;
import sinkillerj.projecte.utils.KeyBinds;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

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
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ObjHandler.condenserMk2), new CondenserMK2ItemRenderer());
		
		//Blocks
		ClientRegistry.bindTileEntitySpecialRenderer(AlchChestTile.class, new ChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserTile.class, new CondenserRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CondenserMK2Tile.class, new CondenserMK2Renderer());
		
		//Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityWaterProjectile.class, new RenderSnowball(ObjHandler.waterOrb));
		RenderingRegistry.registerEntityRenderingHandler(EntityLavaProjectile.class, new RenderSnowball(ObjHandler.lavaOrb));
		RenderingRegistry.registerEntityRenderingHandler(EntityLootBall.class, new RenderSnowball(ObjHandler.lootBall));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobRandomizer.class, new RenderSnowball(ObjHandler.mobRandomizer));
		RenderingRegistry.registerEntityRenderingHandler(EntityLensProjectile.class, new RenderSnowball(ObjHandler.lensExplosive));
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCatalystPrimed.class, new NovaCatalystRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCataclysmPrimed.class, new NovaCataclysmRenderer());
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

