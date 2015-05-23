package moze_intel.projecte.proxies;

import moze_intel.projecte.events.FovChangeEvent;
import moze_intel.projecte.events.KeyPressEvent;
import moze_intel.projecte.events.PlayerRender;
import moze_intel.projecte.events.ToolTipEvent;
import moze_intel.projecte.events.TransmutationRenderingEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.FuelBlock;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import moze_intel.projecte.gameObjs.blocks.NovaCataclysm;
import moze_intel.projecte.gameObjs.blocks.NovaCatalyst;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.CondenserMK2Renderer;
import moze_intel.projecte.rendering.CondenserRenderer;
import moze_intel.projecte.rendering.NovaCataclysmRenderer;
import moze_intel.projecte.rendering.NovaCatalystRenderer;
import moze_intel.projecte.utils.KeyHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
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

		registerItem(ObjHandler.waterOrb);
		registerItem(ObjHandler.lavaOrb);
		registerItem(ObjHandler.lootBall);
		registerItem(ObjHandler.mobRandomizer);
		registerItem(ObjHandler.lensExplosive);

		registerCovalenceDust();
		registerBags();
		registerFuels();
		registerMatter();
		registerKlein();

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

		registerBlock(ObjHandler.rmFurnaceOff);
		registerBlock(ObjHandler.dmFurnaceOff);
		registerBlock(ObjHandler.novaCatalyst);
		registerBlock(ObjHandler.novaCataclysm);
		registerBlock(ObjHandler.alchChest);
		registerBlock(ObjHandler.condenser);
		registerBlock(ObjHandler.condenserMk2);
		registerBlock(ObjHandler.transmuteStone);
		registerBlock(ObjHandler.confuseTorch);
		registerBlock(ObjHandler.energyCollector);
		registerBlock(ObjHandler.collectorMK2);
		registerBlock(ObjHandler.collectorMK3);
		registerBlock(ObjHandler.relay);
		registerBlock(ObjHandler.relayMK2);
		registerBlock(ObjHandler.relayMK3);
		registerBlock(ObjHandler.dmPedestal);
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

