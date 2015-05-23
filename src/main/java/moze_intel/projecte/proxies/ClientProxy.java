package moze_intel.projecte.proxies;

import moze_intel.projecte.events.FovChangeEvent;
import moze_intel.projecte.events.KeyPressEvent;
import moze_intel.projecte.events.PlayerRender;
import moze_intel.projecte.events.ToolTipEvent;
import moze_intel.projecte.events.TransmutationRenderingEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.NovaCataclysm;
import moze_intel.projecte.gameObjs.blocks.NovaCatalyst;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.CondenserMK2Renderer;
import moze_intel.projecte.rendering.CondenserRenderer;
import moze_intel.projecte.rendering.NovaCataclysmRenderer;
import moze_intel.projecte.rendering.NovaCatalystRenderer;
import moze_intel.projecte.utils.KeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ClientProxy extends CommonProxy
{	
	public void registerKeyBinds()
	{
		for (int i = 0; i < KeyHelper.array.length; i++)
		{
			ClientRegistry.registerKeyBinding(KeyHelper.array[i]);
		}
	}

	@Override
	public void registerModels()
	{
		// Blocks with special needs
		ModelLoader.setCustomStateMapper(
				ObjHandler.novaCatalyst,
				(new StateMap.Builder()).addPropertiesToIgnore(NovaCatalyst.EXPLODE).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.novaCataclysm,
				(new StateMap.Builder()).addPropertiesToIgnore(NovaCataclysm.EXPLODE).build()
		);

		registerDefaultItemModel(ObjHandler.lootBall);
		registerDefaultItemModel(ObjHandler.lavaOrb);
		registerDefaultItemModel(ObjHandler.waterOrb);
		registerDefaultItemModel(ObjHandler.mobRandomizer);
		registerDefaultItemModel(ObjHandler.lensExplosive);

		registerDefaultItemModel(ObjHandler.philosStone);
		registerDefaultItemModel(ObjHandler.repairTalisman);

		registerDefaultItemModel(ObjHandler.dmPick);
		registerDefaultItemModel(ObjHandler.dmAxe);
		registerDefaultItemModel(ObjHandler.dmShovel);
		registerDefaultItemModel(ObjHandler.dmSword);
		registerDefaultItemModel(ObjHandler.dmHoe);
		registerDefaultItemModel(ObjHandler.dmShears);
		registerDefaultItemModel(ObjHandler.dmHammer);

		registerDefaultItemModel(ObjHandler.dmHelmet);
		registerDefaultItemModel(ObjHandler.dmChest);
		registerDefaultItemModel(ObjHandler.dmLegs);
		registerDefaultItemModel(ObjHandler.dmFeet);

		registerDefaultItemModel(ObjHandler.rmPick);
		registerDefaultItemModel(ObjHandler.rmAxe);
		registerDefaultItemModel(ObjHandler.rmShovel);
		registerDefaultItemModel(ObjHandler.rmSword);
		registerDefaultItemModel(ObjHandler.rmHoe);
		registerDefaultItemModel(ObjHandler.rmShears);
		registerDefaultItemModel(ObjHandler.rmHammer);
		registerDefaultItemModel(ObjHandler.rmKatar);
		registerDefaultItemModel(ObjHandler.rmStar);

		registerDefaultItemModel(ObjHandler.rmHelmet);
		registerDefaultItemModel(ObjHandler.rmChest);
		registerDefaultItemModel(ObjHandler.rmLegs);
		registerDefaultItemModel(ObjHandler.rmFeet);

		registerDefaultItemModel(ObjHandler.gemHelmet);
		registerDefaultItemModel(ObjHandler.gemChest);
		registerDefaultItemModel(ObjHandler.gemLegs);
		registerDefaultItemModel(ObjHandler.gemFeet);
	}

	@Override
	public void registerRenderers() 
	{
		// Tile Entity
		ClientRegistry.bindTileEntitySpecialRenderer(AlchChestTile.class, new ChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserTile.class, new CondenserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserMK2Tile.class, new CondenserMK2Renderer());

		//Entities
		Minecraft mc = FMLClientHandler.instance().getClient();
		RenderingRegistry.registerEntityRenderingHandler(EntityWaterProjectile.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.waterOrb, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityLavaProjectile.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.lavaOrb, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobRandomizer.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.mobRandomizer, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityLensProjectile.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.lensExplosive, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCatalystPrimed.class, new NovaCatalystRenderer(mc.getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCataclysmPrimed.class, new NovaCataclysmRenderer(mc.getRenderManager()));
	}

	private void registerDefaultItemModel(Item i)
	{
		String name = GameRegistry.findUniqueIdentifierFor(i).name;
		ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation("projecte:" + name, "inventory"));
	}

	@Override
	public void registerClientOnlyEvents() 
	{
		MinecraftForge.EVENT_BUS.register(new FovChangeEvent());
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
		MinecraftForge.EVENT_BUS.register(new TransmutationRenderingEvent());
		FMLCommonHandler.instance().bus().register(new KeyPressEvent());

		PlayerRender pr = new PlayerRender();
		MinecraftForge.EVENT_BUS.register(pr);
		FMLCommonHandler.instance().bus().register(pr);
	}
}

