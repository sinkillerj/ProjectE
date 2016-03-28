package moze_intel.projecte.proxies;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.events.KeyPressEvent;
import moze_intel.projecte.events.PlayerRender;
import moze_intel.projecte.events.ToolTipEvent;
import moze_intel.projecte.events.TransmutationRenderingEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.*;
import moze_intel.projecte.gameObjs.entity.*;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.manual.ManualPageHandler;
import moze_intel.projecte.rendering.*;
import moze_intel.projecte.utils.ClientKeyHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
				(new StateMap.Builder()).ignore(AlchemicalChest.FACING).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.condenser,
				(new StateMap.Builder()).ignore(Condenser.FACING).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.condenserMk2,
				(new StateMap.Builder()).ignore(CondenserMK2.FACING).build()
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
		registerItem(ObjHandler.lootBall);
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
		String name = GameData.getBlockRegistry().getNameForObject(b).toString();
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(name, "inventory"));
	}

	private void registerItem(Item i)
	{
		String name = GameData.getItemRegistry().getNameForObject(i).toString();
		ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(name, "inventory"));
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
		for (FuelBlock.EnumFuelType e : FuelBlock.EnumFuelType.values())
		{
			ModelLoader.setCustomModelResourceLocation(ObjHandler.fuels, e.ordinal(), new ModelResourceLocation("projecte:" + e.getName(), "inventory"));

			String name = GameData.getBlockRegistry().getNameForObject(ObjHandler.fuelBlock).toString();
			ModelLoader.registerItemVariants(Item.getItemFromBlock(ObjHandler.fuelBlock), new ModelResourceLocation(name, "fueltype=" + e.getName()));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ObjHandler.fuelBlock), e.ordinal(), new ModelResourceLocation(name, "fueltype=" + e.getName()));
		}
	}

	private void registerMatter()
	{
		for (MatterBlock.EnumMatterType m : MatterBlock.EnumMatterType.values())
		{
			ModelLoader.setCustomModelResourceLocation(ObjHandler.matter, m.ordinal(), new ModelResourceLocation("projecte:" + m.getName(), "inventory"));

			String name = GameData.getBlockRegistry().getNameForObject(ObjHandler.matterBlock).toString();
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

		// Arcana needs its own mess because it uses NBT to store Active state instead of meta
		String[] names = { "projecte:arcana_zero_off", "projecte:arcana_zero_on", "projecte:arcana_ignition_off", "projecte:arcana_ignition_on",
				"projecte:arcana_harv_off", "projecte:arcana_harv_on", "projecte:arcana_swrg_off", "projecte:arcana_swrg_on" };
		for (String name : names)
			ModelLoader.registerItemVariants(ObjHandler.arcana, new ModelResourceLocation(name, "inventory"));

		ModelLoader.setCustomMeshDefinition(ObjHandler.arcana, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				String modelName;
				boolean active = stack.getTagCompound() != null && stack.getTagCompound().getBoolean("Active");

				switch (stack.getItemDamage())
				{
					case 0: modelName = active ? "arcana_zero_on" : "arcana_zero_off"; break;
					case 1: modelName = active ? "arcana_ignition_on" : "arcana_ignition_off"; break;
					case 2: modelName = active ? "arcana_harv_on" : "arcana_harv_off"; break;
					case 3: modelName = active ? "arcana_swrg_on" : "arcana_swrg_off"; break;
					default: modelName = "";
				}
				return new ModelResourceLocation("projecte:" + modelName, "inventory");
			}
		});
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
		Minecraft mc = FMLClientHandler.instance().getClient();
		RenderingRegistry.registerEntityRenderingHandler(EntityWaterProjectile.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.waterOrb, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityLavaProjectile.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.lavaOrb, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobRandomizer.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.mobRandomizer, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityLensProjectile.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.lensExplosive, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityLootBall.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.lootBall, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCatalystPrimed.class, new NovaCatalystRenderer(mc.getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCataclysmPrimed.class, new NovaCataclysmRenderer(mc.getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFireProjectile.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.fireProjectile, mc.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySWRGProjectile.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.windProjectile, mc.getRenderItem()));

		Map<String, RenderPlayer> skinMap = mc.getRenderManager().getSkinMap();
		RenderPlayer render = skinMap.get("default");
		render.addLayer(new LayerModelYue(render));
		render = skinMap.get("slim");
		render.addLayer(new LayerModelYue(render));
	}

	@Override
	public void registerClientOnlyEvents() 
	{
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
		MinecraftForge.EVENT_BUS.register(new TransmutationRenderingEvent());
		MinecraftForge.EVENT_BUS.register(new KeyPressEvent());

		PlayerRender pr = new PlayerRender();
		MinecraftForge.EVENT_BUS.register(pr);
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

