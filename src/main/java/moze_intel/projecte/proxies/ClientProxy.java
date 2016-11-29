package moze_intel.projecte.proxies;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.api.state.enums.EnumFuelType;
import moze_intel.projecte.api.state.enums.EnumMatterType;
import moze_intel.projecte.events.TransmutationRenderingEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.NovaCataclysm;
import moze_intel.projecte.gameObjs.blocks.NovaCatalyst;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.manual.ManualPageHandler;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.CondenserMK2Renderer;
import moze_intel.projecte.rendering.CondenserRenderer;
import moze_intel.projecte.rendering.LayerYue;
import moze_intel.projecte.rendering.NovaCataclysmRenderer;
import moze_intel.projecte.rendering.NovaCatalystRenderer;
import moze_intel.projecte.rendering.PedestalRenderer;
import moze_intel.projecte.utils.ClientKeyHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.entity.RenderTippedArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Map;

public class ClientProxy implements IProxy
{
	// These three following methods are here to prevent a strange crash in the dedicated server whenever packets are received
	// and the wrapped methods are called directly.

	@Override
	public void clearClientKnowledge()
	{
		FMLClientHandler.instance().getClientPlayerEntity().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).clearKnowledge();
	}

	@Override
	public IKnowledgeProvider getClientTransmutationProps()
	{
		return FMLClientHandler.instance().getClientPlayerEntity().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null);
	}

	@Override
	public IAlchBagProvider getClientBagProps()
	{
		return FMLClientHandler.instance().getClientPlayerEntity().getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null);
	}

	@Override
	public void registerKeyBinds()
	{
		ClientKeyHelper.registerMCBindings();
	}

	@Override
	public void registerModels()
	{
		// Blocks with special needs
		ModelLoader.setCustomStateMapper(
				ObjHandler.novaCatalyst,
				(new StateMap.Builder()).ignore(NovaCatalyst.EXPLODE).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.novaCataclysm,
				(new StateMap.Builder()).ignore(NovaCataclysm.EXPLODE).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.alchChest,
				(new StateMap.Builder()).ignore(PEStateProps.FACING).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.condenser,
				(new StateMap.Builder()).ignore(PEStateProps.FACING).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.condenserMk2,
				(new StateMap.Builder()).ignore(PEStateProps.FACING).build()
		);

		// Items that have different properties or textures per meta value.
		registerCovalenceDust();
		registerBags();
		registerFuels();
		registerMatter();
		registerKlein();
		registerPowerStones();
		registerPowerItems();

		// Normal items that have no variants / meta values. The json models are named "item.pe_<name>" because we register items with unlocal name.
		// Which was a dumb decision made by somebody way back when. Oh well.
		registerItem(ObjHandler.waterOrb);
		registerItem(ObjHandler.lavaOrb);
		registerItem(ObjHandler.mobRandomizer);
		registerItem(ObjHandler.lensExplosive);
		registerItem(ObjHandler.windProjectile);
		registerItem(ObjHandler.fireProjectile);

		registerItem(ObjHandler.philosStone);
		registerItem(ObjHandler.repairTalisman);
		registerItem(ObjHandler.ironBand);
		registerItem(ObjHandler.dCatalyst);
		registerItem(ObjHandler.hyperLens);
		registerItem(ObjHandler.cataliticLens);
		registerItem(ObjHandler.tome);
		registerItem(ObjHandler.transmutationTablet);
		registerItem(ObjHandler.everTide);
		registerItem(ObjHandler.volcanite);
		registerItem(ObjHandler.dRod1);
		registerItem(ObjHandler.dRod2);
		registerItem(ObjHandler.dRod3);
		registerItem(ObjHandler.angelSmite);
		ModelLoader.setCustomModelResourceLocation(ObjHandler.angelSmite, 1, new ModelResourceLocation(ObjHandler.angelSmite.getRegistryName(), "inventory"));
		registerItem(ObjHandler.mercEye);

		registerItem(ObjHandler.dmPick);
		registerItem(ObjHandler.dmAxe);
		registerItem(ObjHandler.dmShovel);
		registerItem(ObjHandler.dmSword);
		registerItem(ObjHandler.dmHoe);
		registerItem(ObjHandler.dmShears);
		registerItem(ObjHandler.dmHammer);

		registerItem(ObjHandler.dmHelmet);
		registerItem(ObjHandler.dmChest);
		registerItem(ObjHandler.dmLegs);
		registerItem(ObjHandler.dmFeet);

		registerItem(ObjHandler.rmPick);
		registerItem(ObjHandler.rmAxe);
		registerItem(ObjHandler.rmShovel);
		registerItem(ObjHandler.rmSword);
		registerItem(ObjHandler.rmHoe);
		registerItem(ObjHandler.rmShears);
		registerItem(ObjHandler.rmHammer);
		registerItem(ObjHandler.rmKatar);
		registerItem(ObjHandler.rmStar);

		registerItem(ObjHandler.rmHelmet);
		registerItem(ObjHandler.rmChest);
		registerItem(ObjHandler.rmLegs);
		registerItem(ObjHandler.rmFeet);

		registerItem(ObjHandler.gemHelmet);
		registerItem(ObjHandler.gemChest);
		registerItem(ObjHandler.gemLegs);
		registerItem(ObjHandler.gemFeet);

		registerItem(ObjHandler.manual);

		// Item models for blocks
		registerBlock(ObjHandler.alchChest);
		registerBlock(ObjHandler.collectorMK2);
		registerBlock(ObjHandler.collectorMK3);
		registerBlock(ObjHandler.condenser);
		registerBlock(ObjHandler.condenserMk2);
		registerBlock(ObjHandler.confuseTorch);
		registerBlock(ObjHandler.dmFurnaceOff);
		registerBlock(ObjHandler.dmPedestal);
		registerBlock(ObjHandler.energyCollector);
		registerBlock(ObjHandler.novaCatalyst);
		registerBlock(ObjHandler.novaCataclysm);
		registerBlock(ObjHandler.relay);
		registerBlock(ObjHandler.relayMK2);
		registerBlock(ObjHandler.relayMK3);
		registerBlock(ObjHandler.rmFurnaceOff);
		registerBlock(ObjHandler.transmuteStone);
	}

	private void registerBlock(Block b)
	{
		String name = ForgeRegistries.BLOCKS.getKey(b).toString();
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(name, "inventory"));
	}

	private void registerItem(Item i)
	{
		registerItem(i, 0);
	}

	private void registerItem(Item i, int meta)
	{
		String name = ForgeRegistries.ITEMS.getKey(i).toString();
		ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(name, "inventory"));
	}

	private void registerCovalenceDust()
	{
		ModelLoader.setCustomModelResourceLocation(ObjHandler.covalence, 0, new ModelResourceLocation("projecte:covalence_low", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.covalence, 1, new ModelResourceLocation("projecte:covalence_medium", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.covalence, 2, new ModelResourceLocation("projecte:covalence_high", "inventory"));
	}

	private void registerBags()
	{
		for (EnumDyeColor e : EnumDyeColor.values())
		{
			ModelLoader.setCustomModelResourceLocation(ObjHandler.alchBag, e.getMetadata(), new ModelResourceLocation("projecte:bags/alchbag_" + e.getName(), "inventory"));
		}
	}

	private void registerFuels()
	{
		for (EnumFuelType e : EnumFuelType.values())
		{
			ModelLoader.setCustomModelResourceLocation(ObjHandler.fuels, e.ordinal(), new ModelResourceLocation("projecte:" + e.getName(), "inventory"));

			String name = ForgeRegistries.BLOCKS.getKey(ObjHandler.fuelBlock).toString();
			ModelLoader.registerItemVariants(Item.getItemFromBlock(ObjHandler.fuelBlock), new ModelResourceLocation(name, "fueltype=" + e.getName()));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ObjHandler.fuelBlock), e.ordinal(), new ModelResourceLocation(name, "fueltype=" + e.getName()));
		}
	}

	private void registerMatter()
	{
		for (EnumMatterType m : EnumMatterType.values())
		{
			ModelLoader.setCustomModelResourceLocation(ObjHandler.matter, m.ordinal(), new ModelResourceLocation("projecte:" + m.getName(), "inventory"));

			String name = ForgeRegistries.BLOCKS.getKey(ObjHandler.matterBlock).toString();
			ModelLoader.registerItemVariants(Item.getItemFromBlock(ObjHandler.matterBlock), new ModelResourceLocation(name, "tier=" + m.getName()));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ObjHandler.matterBlock), m.ordinal(), new ModelResourceLocation(name, "tier=" + m.getName()));
		}
	}

	private void registerKlein()
	{
		for (KleinStar.EnumKleinTier e : KleinStar.EnumKleinTier.values())
		{
			ModelLoader.setCustomModelResourceLocation(ObjHandler.kleinStars, e.ordinal(), new ModelResourceLocation("projecte:stars/klein_star_" + e.name, "inventory"));
		}
	}

	private void registerPowerStones()
	{
		ModelLoader.setCustomModelResourceLocation(ObjHandler.bodyStone, 0, new ModelResourceLocation("projecte:body_stone_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.bodyStone, 1, new ModelResourceLocation("projecte:body_stone_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.soulStone, 0, new ModelResourceLocation("projecte:soul_stone_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.soulStone, 1, new ModelResourceLocation("projecte:soul_stone_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.mindStone, 0, new ModelResourceLocation("projecte:mind_stone_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.mindStone, 1, new ModelResourceLocation("projecte:mind_stone_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.lifeStone, 0, new ModelResourceLocation("projecte:life_stone_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.lifeStone, 1, new ModelResourceLocation("projecte:life_stone_on", "inventory"));
	}

	private void registerPowerItems()
	{
		ModelLoader.setCustomModelResourceLocation(ObjHandler.blackHole, 0, new ModelResourceLocation("projecte:bhb_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.blackHole, 1, new ModelResourceLocation("projecte:bhb_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.harvestGod, 0, new ModelResourceLocation("projecte:harvgod_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.harvestGod, 1, new ModelResourceLocation("projecte:harvgod_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.eternalDensity, 0, new ModelResourceLocation("projecte:goed_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.eternalDensity, 1, new ModelResourceLocation("projecte:goed_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.timeWatch, 0, new ModelResourceLocation("projecte:timewatch_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.timeWatch, 1, new ModelResourceLocation("projecte:timewatch_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.ignition, 0, new ModelResourceLocation("projecte:ignition_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.ignition, 1, new ModelResourceLocation("projecte:ignition_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.zero, 0, new ModelResourceLocation("projecte:zero_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.zero, 1, new ModelResourceLocation("projecte:zero_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.swrg, 0, new ModelResourceLocation("projecte:swrg_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.swrg, 1, new ModelResourceLocation("projecte:swrg_fly", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.swrg, 2, new ModelResourceLocation("projecte:swrg_repel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.swrg, 3, new ModelResourceLocation("projecte:swrg_both", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.voidRing, 0, new ModelResourceLocation("projecte:voidring_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.voidRing, 1, new ModelResourceLocation("projecte:voidring_on", "inventory"));

		ModelLoader.setCustomModelResourceLocation(ObjHandler.arcana, 0, new ModelResourceLocation("projecte:arcana_zero_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.arcana, 1, new ModelResourceLocation("projecte:arcana_ignition_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.arcana, 2, new ModelResourceLocation("projecte:arcana_harv_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.arcana, 3, new ModelResourceLocation("projecte:arcana_swrg_off", "inventory"));
	}

	@Override
	public void registerRenderers()
	{
		// Tile Entity
		ClientRegistry.bindTileEntitySpecialRenderer(AlchChestTile.class, new ChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserTile.class, new CondenserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserMK2Tile.class, new CondenserMK2Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(DMPedestalTile.class, new PedestalRenderer());

		//Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityWaterProjectile.class, createRenderFactoryForSnowball(ObjHandler.waterOrb));
		RenderingRegistry.registerEntityRenderingHandler(EntityLavaProjectile.class, createRenderFactoryForSnowball(ObjHandler.lavaOrb));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobRandomizer.class, createRenderFactoryForSnowball(ObjHandler.mobRandomizer));
		RenderingRegistry.registerEntityRenderingHandler(EntityLensProjectile.class, createRenderFactoryForSnowball(ObjHandler.lensExplosive));
		RenderingRegistry.registerEntityRenderingHandler(EntityFireProjectile.class, createRenderFactoryForSnowball(ObjHandler.fireProjectile));
		RenderingRegistry.registerEntityRenderingHandler(EntitySWRGProjectile.class, createRenderFactoryForSnowball(ObjHandler.windProjectile));
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCatalystPrimed.class, NovaCatalystRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCataclysmPrimed.class, NovaCataclysmRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityHomingArrow.class, RenderTippedArrow::new);
	}

	@Override
	public void registerLayerRenderers()
	{
		Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
		RenderPlayer render = skinMap.get("default");
		render.addLayer(new LayerYue(render));
		render = skinMap.get("slim");
		render.addLayer(new LayerYue(render));
	}

	private static <T extends Entity> IRenderFactory<T> createRenderFactoryForSnowball(final Item itemToRender)
	{
		return manager -> new RenderSnowball<>(manager, itemToRender, Minecraft.getMinecraft().getRenderItem());
	}

	@Override
	public void registerClientOnlyEvents() 
	{
		MinecraftForge.EVENT_BUS.register(new TransmutationRenderingEvent());
	}

	@Override
	public void initializeManual()
	{
		ManualPageHandler.init();
	}

	@Override
	public EntityPlayer getClientPlayer()
	{
		return FMLClientHandler.instance().getClientPlayerEntity();
	}

	@Override
	public boolean isJumpPressed()
	{
		return FMLClientHandler.instance().getClient().gameSettings.keyBindJump.isKeyDown();
	}
}

