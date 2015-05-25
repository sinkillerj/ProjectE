package moze_intel.projecte.proxies;

import com.google.common.collect.Sets;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import moze_intel.projecte.events.KeyPressEvent;
import moze_intel.projecte.events.PlayerRender;
import moze_intel.projecte.events.ToolTipEvent;
import moze_intel.projecte.events.TransmutationRenderingEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.rendering.ChestItemRenderer;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.CondenserItemRenderer;
import moze_intel.projecte.rendering.CondenserMK2ItemRenderer;
import moze_intel.projecte.rendering.CondenserMK2Renderer;
import moze_intel.projecte.rendering.CondenserRenderer;
import moze_intel.projecte.rendering.NovaCataclysmRenderer;
import moze_intel.projecte.rendering.NovaCatalystRenderer;
import moze_intel.projecte.rendering.PedestalItemRenderer;
import moze_intel.projecte.rendering.PedestalRenderer;
import moze_intel.projecte.utils.ClientKeyHelper;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerKeyBinds()
	{
		ClientKeyHelper.registerMCBindings();
	}

	@Override
	public void registerRenderers() 
	{
		//Items
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ObjHandler.alchChest), new ChestItemRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ObjHandler.condenser), new CondenserItemRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ObjHandler.condenserMk2), new CondenserMK2ItemRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ObjHandler.dmPedestal), new PedestalItemRenderer());

		//Blocks
		ClientRegistry.bindTileEntitySpecialRenderer(AlchChestTile.class, new ChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserTile.class, new CondenserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserMK2Tile.class, new CondenserMK2Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(DMPedestalTile.class, new PedestalRenderer());
		
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
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
		MinecraftForge.EVENT_BUS.register(new TransmutationRenderingEvent());
		FMLCommonHandler.instance().bus().register(new KeyPressEvent());

		PlayerRender pr = new PlayerRender();
		MinecraftForge.EVENT_BUS.register(pr);
		FMLCommonHandler.instance().bus().register(pr);
	}
}

