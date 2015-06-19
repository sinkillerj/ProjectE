package moze_intel.projecte.proxies;

import moze_intel.projecte.events.KeyPressEvent;
import moze_intel.projecte.events.PlayerRender;
import moze_intel.projecte.events.ToolTipEvent;
import moze_intel.projecte.events.TransmutationRenderingEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.AlchemicalChest;
import moze_intel.projecte.gameObjs.blocks.Condenser;
import moze_intel.projecte.gameObjs.blocks.CondenserMK2;
import moze_intel.projecte.gameObjs.blocks.FuelBlock;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import moze_intel.projecte.gameObjs.blocks.NovaCataclysm;
import moze_intel.projecte.gameObjs.blocks.NovaCatalyst;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLightningProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.playerData.AlchBagProps;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.playerData.TransmutationProps;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.CondenserMK2Renderer;
import moze_intel.projecte.rendering.CondenserRenderer;
import moze_intel.projecte.rendering.NovaCataclysmRenderer;
import moze_intel.projecte.rendering.NovaCatalystRenderer;
import moze_intel.projecte.utils.ClientKeyHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ClientProxy extends CommonProxy
{
	// These three following methods are here to prevent a strange crash in the dedicated server whenever packets are received
	// and the wrapped methods are called directly.

	@Override
	public void clearClientKnowledge()
	{
		Transmutation.clearKnowledge(FMLClientHandler.instance().getClientPlayerEntity());
	}

	@Override
	public TransmutationProps getClientTransmutationProps()
	{
		return TransmutationProps.getDataFor(FMLClientHandler.instance().getClientPlayerEntity());
	}

	@Override
	public AlchBagProps getClientBagProps()
	{
		return AlchBagProps.getDataFor(FMLClientHandler.instance().getClientPlayerEntity());
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
				(new StateMap.Builder()).addPropertiesToIgnore(NovaCatalyst.EXPLODE).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.novaCataclysm,
				(new StateMap.Builder()).addPropertiesToIgnore(NovaCataclysm.EXPLODE).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.alchChest,
				(new StateMap.Builder()).addPropertiesToIgnore(AlchemicalChest.FACING).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.condenser,
				(new StateMap.Builder()).addPropertiesToIgnore(Condenser.FACING).build()
		);

		ModelLoader.setCustomStateMapper(
				ObjHandler.condenserMk2,
				(new StateMap.Builder()).addPropertiesToIgnore(CondenserMK2.FACING).build()
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
		String name = GameRegistry.findUniqueIdentifierFor(b).name;
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation("projecte:" + name, "inventory"));
	}

	private void registerItem(Item i)
	{
		String name = GameRegistry.findUniqueIdentifierFor(i).name;
		ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation("projecte:" + name, "inventory"));
	}

	private void registerCovalenceDust()
	{
		ModelLoader.addVariantName(ObjHandler.covalence, "projecte:covalence_low", "projecte:covalence_medium", "projecte:covalence_high");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.covalence, 0, new ModelResourceLocation("projecte:covalence_low", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.covalence, 1, new ModelResourceLocation("projecte:covalence_medium", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.covalence, 2, new ModelResourceLocation("projecte:covalence_high", "inventory"));
	}

	private void registerBags()
	{
		for (EnumDyeColor e : EnumDyeColor.values())
		{
			ModelLoader.addVariantName(ObjHandler.alchBag, "projecte:bags/alchbag_" + e.getName());
			ModelLoader.setCustomModelResourceLocation(ObjHandler.alchBag, e.getMetadata(), new ModelResourceLocation("projecte:bags/alchbag_" + e.getName(), "inventory"));
		}
	}

	private void registerFuels()
	{
		for (FuelBlock.EnumFuelType e : FuelBlock.EnumFuelType.values())
		{
			ModelLoader.addVariantName(ObjHandler.fuels, "projecte:" + e.getName());
			ModelLoader.setCustomModelResourceLocation(ObjHandler.fuels, e.ordinal(), new ModelResourceLocation("projecte:" + e.getName(), "inventory"));

			ModelLoader.addVariantName(Item.getItemFromBlock(ObjHandler.fuelBlock), "projecte:" + e.getName() + "_block");
			int meta = ObjHandler.fuelBlock.getMetaFromState(ObjHandler.fuelBlock.getDefaultState().withProperty(FuelBlock.FUEL_PROP, e));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ObjHandler.fuelBlock), meta, new ModelResourceLocation("projecte:" + e.getName() + "_block", "inventory"));
		}
	}

	private void registerMatter()
	{
		for (MatterBlock.EnumMatterType m : MatterBlock.EnumMatterType.values())
		{
			ModelLoader.addVariantName(ObjHandler.matter, "projecte:" + m.getName());
			ModelLoader.setCustomModelResourceLocation(ObjHandler.matter, m.ordinal(), new ModelResourceLocation("projecte:" + m.getName(), "inventory"));

			ModelLoader.addVariantName(Item.getItemFromBlock(ObjHandler.matterBlock), "projecte:" + m.getName() + "_block");
			int meta = ObjHandler.matterBlock.getMetaFromState(ObjHandler.matterBlock.getDefaultState().withProperty(MatterBlock.TIER_PROP, m));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ObjHandler.matterBlock), meta, new ModelResourceLocation("projecte:" + m.getName() + "_block", "inventory"));
		}
	}

	private void registerKlein()
	{
		for (KleinStar.EnumKleinTier e : KleinStar.EnumKleinTier.values())
		{
			ModelLoader.addVariantName(ObjHandler.kleinStars, "projecte:stars/klein_star_" + e.name);
			ModelLoader.setCustomModelResourceLocation(ObjHandler.kleinStars, e.ordinal(), new ModelResourceLocation("projecte:stars/klein_star_" + e.name, "inventory"));
		}
	}

	private void registerPowerStones()
	{
		ModelLoader.addVariantName(ObjHandler.bodyStone, "projecte:body_stone_off", "projecte:body_stone_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.bodyStone, 0, new ModelResourceLocation("projecte:body_stone_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.bodyStone, 1, new ModelResourceLocation("projecte:body_stone_on", "inventory"));

		ModelLoader.addVariantName(ObjHandler.soulStone, "projecte:soul_stone_off", "projecte:soul_stone_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.soulStone, 0, new ModelResourceLocation("projecte:soul_stone_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.soulStone, 1, new ModelResourceLocation("projecte:soul_stone_on", "inventory"));

		ModelLoader.addVariantName(ObjHandler.mindStone, "projecte:mind_stone_off", "projecte:mind_stone_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.mindStone, 0, new ModelResourceLocation("projecte:mind_stone_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.mindStone, 1, new ModelResourceLocation("projecte:mind_stone_on", "inventory"));

		ModelLoader.addVariantName(ObjHandler.lifeStone, "projecte:life_stone_off", "projecte:life_stone_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.lifeStone, 0, new ModelResourceLocation("projecte:life_stone_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.lifeStone, 1, new ModelResourceLocation("projecte:life_stone_on", "inventory"));
	}

	private void registerPowerItems()
	{
		ModelLoader.addVariantName(ObjHandler.blackHole, "projecte:bhb_off", "projecte:bhb_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.blackHole, 0, new ModelResourceLocation("projecte:bhb_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.blackHole, 1, new ModelResourceLocation("projecte:bhb_on", "inventory"));

		ModelLoader.addVariantName(ObjHandler.harvestGod, "projecte:harvgod_off", "projecte:harvgod_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.harvestGod, 0, new ModelResourceLocation("projecte:harvgod_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.harvestGod, 1, new ModelResourceLocation("projecte:harvgod_on", "inventory"));

		ModelLoader.addVariantName(ObjHandler.eternalDensity, "projecte:goed_off", "projecte:goed_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.eternalDensity, 0, new ModelResourceLocation("projecte:goed_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.eternalDensity, 1, new ModelResourceLocation("projecte:goed_on", "inventory"));

		ModelLoader.addVariantName(ObjHandler.timeWatch, "projecte:timewatch_off", "projecte:timewatch_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.timeWatch, 0, new ModelResourceLocation("projecte:timewatch_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.timeWatch, 1, new ModelResourceLocation("projecte:timewatch_on", "inventory"));

		ModelLoader.addVariantName(ObjHandler.ignition, "projecte:ignition_off", "projecte:ignition_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.ignition, 0, new ModelResourceLocation("projecte:ignition_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.ignition, 1, new ModelResourceLocation("projecte:ignition_on", "inventory"));

		ModelLoader.addVariantName(ObjHandler.zero, "projecte:zero_off", "projecte:zero_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.zero, 0, new ModelResourceLocation("projecte:zero_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.zero, 1, new ModelResourceLocation("projecte:zero_on", "inventory"));

		ModelLoader.addVariantName(ObjHandler.swrg, "projecte:swrg_off", "projecte:swrg_fly", "projecte:swrg_repel", "projecte:swrg_both");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.swrg, 0, new ModelResourceLocation("projecte:swrg_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.swrg, 1, new ModelResourceLocation("projecte:swrg_fly", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.swrg, 2, new ModelResourceLocation("projecte:swrg_repel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.swrg, 3, new ModelResourceLocation("projecte:swrg_both", "inventory"));

		ModelLoader.addVariantName(ObjHandler.voidRing, "projecte:voidring_off", "projecte:voidring_on");
		ModelLoader.setCustomModelResourceLocation(ObjHandler.voidRing, 0, new ModelResourceLocation("projecte:voidring_off", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ObjHandler.voidRing, 1, new ModelResourceLocation("projecte:voidring_on", "inventory"));

		// Arcana needs its own mess because it uses NBT to store Active state instead of meta
		ModelLoader.addVariantName(ObjHandler.arcana, "projecte:arcana_zero_off", "projecte:arcana_zero_on", "projecte:arcana_ignition_off", "projecte:arcana_ignition_on",
				"projecte:arcana_harv_off", "projecte:arcana_harv_on", "projecte:arcana_swrg_off", "projecte:arcana_swrg_on");
		ModelLoader.setCustomMeshDefinition(ObjHandler.arcana, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
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
		RenderingRegistry.registerEntityRenderingHandler(EntityLightningProjectile.class, new RenderSnowball(mc.getRenderManager(), ObjHandler.windProjectile, mc.getRenderItem()));
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

