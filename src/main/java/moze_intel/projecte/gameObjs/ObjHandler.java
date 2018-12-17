package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.blocks.AlchemicalChest;
import moze_intel.projecte.gameObjs.blocks.Collector;
import moze_intel.projecte.gameObjs.blocks.Condenser;
import moze_intel.projecte.gameObjs.blocks.CondenserMK2;
import moze_intel.projecte.gameObjs.blocks.FuelBlock;
import moze_intel.projecte.gameObjs.blocks.InterdictionTorch;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.blocks.NovaCataclysm;
import moze_intel.projecte.gameObjs.blocks.NovaCatalyst;
import moze_intel.projecte.gameObjs.blocks.Pedestal;
import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.gameObjs.blocks.TransmutationStone;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessKleinStar;
import moze_intel.projecte.gameObjs.customRecipes.RecipesCovalenceRepair;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.items.AlchemicalFuel;
import moze_intel.projecte.gameObjs.items.CataliticLens;
import moze_intel.projecte.gameObjs.items.CovalenceDust;
import moze_intel.projecte.gameObjs.items.DestructionCatalyst;
import moze_intel.projecte.gameObjs.items.DiviningRod;
import moze_intel.projecte.gameObjs.items.EvertideAmulet;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.gameObjs.items.HyperkineticLens;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.items.Matter;
import moze_intel.projecte.gameObjs.items.MercurialEye;
import moze_intel.projecte.gameObjs.items.PEManual;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.gameObjs.items.RepairTalisman;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.gameObjs.items.Tome;
import moze_intel.projecte.gameObjs.items.TransmutationTablet;
import moze_intel.projecte.gameObjs.items.VolcaniteAmulet;
import moze_intel.projecte.gameObjs.items.armor.DMArmor;
import moze_intel.projecte.gameObjs.items.armor.GemChest;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.gameObjs.items.armor.GemHelmet;
import moze_intel.projecte.gameObjs.items.armor.GemLegs;
import moze_intel.projecte.gameObjs.items.armor.RMArmor;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemAlchemyChestBlock;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemCollectorBlock;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemCondenserBlock;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemDMFurnaceBlock;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemFuelBlock;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemMatterBlock;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemRMFurnaceBlock;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemRelayBlock;
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemTransmutationBlock;
import moze_intel.projecte.gameObjs.items.rings.Arcana;
import moze_intel.projecte.gameObjs.items.rings.ArchangelSmite;
import moze_intel.projecte.gameObjs.items.rings.BlackHoleBand;
import moze_intel.projecte.gameObjs.items.rings.BodyStone;
import moze_intel.projecte.gameObjs.items.rings.HarvestGoddess;
import moze_intel.projecte.gameObjs.items.rings.Ignition;
import moze_intel.projecte.gameObjs.items.rings.LifeStone;
import moze_intel.projecte.gameObjs.items.rings.MindStone;
import moze_intel.projecte.gameObjs.items.rings.SWRG;
import moze_intel.projecte.gameObjs.items.rings.SoulStone;
import moze_intel.projecte.gameObjs.items.rings.VoidRing;
import moze_intel.projecte.gameObjs.items.rings.Zero;
import moze_intel.projecte.gameObjs.items.tools.DarkAxe;
import moze_intel.projecte.gameObjs.items.tools.DarkHammer;
import moze_intel.projecte.gameObjs.items.tools.DarkHoe;
import moze_intel.projecte.gameObjs.items.tools.DarkPick;
import moze_intel.projecte.gameObjs.items.tools.DarkShears;
import moze_intel.projecte.gameObjs.items.tools.DarkShovel;
import moze_intel.projecte.gameObjs.items.tools.DarkSword;
import moze_intel.projecte.gameObjs.items.tools.RedAxe;
import moze_intel.projecte.gameObjs.items.tools.RedHammer;
import moze_intel.projecte.gameObjs.items.tools.RedHoe;
import moze_intel.projecte.gameObjs.items.tools.RedKatar;
import moze_intel.projecte.gameObjs.items.tools.RedPick;
import moze_intel.projecte.gameObjs.items.tools.RedShears;
import moze_intel.projecte.gameObjs.items.tools.RedShovel;
import moze_intel.projecte.gameObjs.items.tools.RedStar;
import moze_intel.projecte.gameObjs.items.tools.RedSword;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK3Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK2Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

@Mod.EventBusSubscriber(modid = PECore.MODID)
public class ObjHandler
{
	public static final CreativeTabs cTab = new CreativeTab();
	public static final Block alchChest = new AlchemicalChest();
	public static final Block interdictionTorch = new InterdictionTorch();
	public static final Block transmuteStone = new TransmutationStone();
	public static final Block condenser = new Condenser();
	public static final Block condenserMk2 = new CondenserMK2();
	public static final Block rmFurnaceOff = new MatterFurnace(false, true);
	public static final Block rmFurnaceOn = new MatterFurnace(true, true);
	public static final Block dmFurnaceOff = new MatterFurnace(false, false);
	public static final Block dmFurnaceOn = new MatterFurnace(true, false);
	public static final Block dmPedestal = new Pedestal();
	public static final Block matterBlock = new MatterBlock();
	public static final Block fuelBlock = new FuelBlock();
	public static final Block collectorMK1 = new Collector(1);
	public static final Block collectorMK2 = new Collector(2);
	public static final Block collectorMK3 = new Collector(3);
	public static final Block relay = new Relay(1);
	public static final Block relayMK2 = new Relay(2);
	public static final Block relayMK3 = new Relay(3);
	public static final Block novaCatalyst = new NovaCatalyst();
	public static final Block novaCataclysm = new NovaCataclysm();

	public static final Item philosStone = new PhilosophersStone();
	public static final Item alchBag = new AlchemicalBag();
	public static final Item repairTalisman = new RepairTalisman();
	public static final Item kleinStars = new KleinStar();
	public static final Item fuels = new AlchemicalFuel();
	public static final Item covalence = new CovalenceDust();
	public static final Item matter = new Matter();

	public static final Item dmPick = new DarkPick();
	public static final Item dmAxe = new DarkAxe();
	public static final Item dmShovel = new DarkShovel();
	public static final Item dmSword = new DarkSword();
	public static final Item dmHoe = new DarkHoe();
	public static final Item dmShears = new DarkShears();
	public static final Item dmHammer = new DarkHammer();

	public static final Item rmPick = new RedPick();
	public static final Item rmAxe = new RedAxe();
	public static final Item rmShovel = new RedShovel();
	public static final Item rmSword = new RedSword();
	public static final Item rmHoe = new RedHoe();
	public static final Item rmShears = new RedShears();
	public static final Item rmHammer = new RedHammer();
	public static final Item rmKatar = new RedKatar();
	public static final Item rmStar = new RedStar();

	public static final Item dmHelmet = new DMArmor(EntityEquipmentSlot.HEAD);
	public static final Item dmChest = new DMArmor(EntityEquipmentSlot.CHEST);
	public static final Item dmLegs = new DMArmor(EntityEquipmentSlot.LEGS);
	public static final Item dmFeet = new DMArmor(EntityEquipmentSlot.FEET);

	public static final Item rmHelmet = new RMArmor(EntityEquipmentSlot.HEAD);
	public static final Item rmChest = new RMArmor(EntityEquipmentSlot.CHEST);
	public static final Item rmLegs = new RMArmor(EntityEquipmentSlot.LEGS);
	public static final Item rmFeet = new RMArmor(EntityEquipmentSlot.FEET);

	public static final Item gemHelmet = new GemHelmet();
	public static final Item gemChest = new GemChest();
	public static final Item gemLegs = new GemLegs();
	public static final Item gemFeet = new GemFeet();

	public static final Item ironBand = new ItemPE().setTranslationKey("ring_iron_band");
	public static final Item blackHole = new BlackHoleBand();
	public static final Item angelSmite = new ArchangelSmite();
	public static final Item harvestGod = new HarvestGoddess();
	public static final Item ignition = new Ignition();
	public static final Item zero = new Zero();
	public static final Item swrg = new SWRG();
	public static final Item timeWatch = new TimeWatch();
	public static final Item everTide = new EvertideAmulet();
	public static final Item volcanite = new VolcaniteAmulet();
	public static final Item eternalDensity = new GemEternalDensity();
	public static final Item dRod1 = new DiviningRod(new String[] { "3x3x3" }).setTranslationKey("divining_rod_1");
	public static final Item dRod2 = new DiviningRod(new String[]{ "3x3x3", "16x3x3" }).setTranslationKey("divining_rod_2");
	public static final Item dRod3 = new DiviningRod(new String[] { "3x3x3", "16x3x3", "64x3x3" }).setTranslationKey("divining_rod_3");
	public static final Item mercEye = new MercurialEye();
	public static final Item voidRing = new VoidRing();
	public static final Item arcana = new Arcana();

	public static final Item dCatalyst = new DestructionCatalyst();
	public static final Item hyperLens = new HyperkineticLens();
	public static final Item cataliticLens = new CataliticLens();

	public static final Item bodyStone = new BodyStone();
	public static final Item soulStone = new SoulStone();
	public static final Item mindStone = new MindStone();
	public static final Item lifeStone = new LifeStone();

	public static final Item tome = new Tome();

	public static final Item waterOrb = new Item().setTranslationKey("pe_water_orb");
	public static final Item lavaOrb = new Item().setTranslationKey("pe_lava_orb");
	public static final Item mobRandomizer = new Item().setTranslationKey("pe_randomizer");
	public static final Item lensExplosive = new Item().setTranslationKey("pe_lens_explosive");
	public static final Item fireProjectile = new Item().setTranslationKey("pe_fire_projectile");
	public static final Item windProjectile = new Item().setTranslationKey("pe_wind_projectile");
	public static final Item transmutationTablet = new TransmutationTablet();
	public static final Item manual = new PEManual();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> evt)
	{
		IForgeRegistry<Block> r = evt.getRegistry();
		registerObj(r, alchChest, "alchemical_chest");
		registerObj(r, collectorMK1, "collector_mk1");
		registerObj(r, collectorMK2, "collector_mk2");
		registerObj(r, collectorMK3, "collector_mk3");
		registerObj(r, condenser, "condenser_mk1");
		registerObj(r, condenserMk2, "condenser_mk2");
		registerObj(r, dmFurnaceOff, "dm_furnace");
		registerObj(r, dmFurnaceOn, "dm_furnace_lit");
		registerObj(r, dmPedestal, "dm_pedestal");
		registerObj(r, fuelBlock, "fuel_block");
		registerObj(r, interdictionTorch, "interdiction_torch");
		registerObj(r, matterBlock, "matter_block");
		registerObj(r, novaCatalyst, "nova_catalyst");
		registerObj(r, novaCataclysm, "nova_cataclysm");
		registerObj(r, relay, "relay_mk1");
		registerObj(r, relayMK2, "relay_mk2");
		registerObj(r, relayMK3, "relay_mk3");
		registerObj(r, rmFurnaceOff, "rm_furnace");
		registerObj(r, rmFurnaceOn, "rm_furnace_lit");
		registerObj(r, transmuteStone, "transmutation_table");
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> evt)
	{
		IForgeRegistry<Item> r = evt.getRegistry();
		registerObj(r, new ItemAlchemyChestBlock(alchChest), alchChest.getRegistryName());
		registerObj(r, new ItemCollectorBlock(collectorMK1), collectorMK1.getRegistryName());
		registerObj(r, new ItemCollectorBlock(collectorMK2), collectorMK2.getRegistryName());
		registerObj(r, new ItemCollectorBlock(collectorMK3), collectorMK3.getRegistryName());
		registerObj(r, new ItemCondenserBlock(condenser), condenser.getRegistryName());
		registerObj(r, new ItemBlock(condenserMk2), condenserMk2.getRegistryName());
		registerObj(r, new ItemDMFurnaceBlock(dmFurnaceOff), dmFurnaceOff.getRegistryName());
		registerObj(r, new ItemBlock(dmPedestal), dmPedestal.getRegistryName());
		registerObj(r, new ItemFuelBlock(fuelBlock), fuelBlock.getRegistryName());
		registerObj(r, new ItemBlock(interdictionTorch), interdictionTorch.getRegistryName());
		registerObj(r, new ItemMatterBlock(matterBlock), matterBlock.getRegistryName());
		registerObj(r, new ItemBlock(novaCatalyst), novaCatalyst.getRegistryName());
		registerObj(r, new ItemBlock(novaCataclysm), novaCataclysm.getRegistryName());
		registerObj(r, new ItemRelayBlock(relay), relay.getRegistryName());
		registerObj(r, new ItemRelayBlock(relayMK2), relayMK2.getRegistryName());
		registerObj(r, new ItemRelayBlock(relayMK3), relayMK3.getRegistryName());
		registerObj(r, new ItemRMFurnaceBlock(rmFurnaceOff), rmFurnaceOff.getRegistryName());
		registerObj(r, new ItemTransmutationBlock(transmuteStone), transmuteStone.getRegistryName());

		registerObj(r, philosStone, philosStone.getTranslationKey());
		registerObj(r, alchBag, alchBag.getTranslationKey());
		registerObj(r, repairTalisman, repairTalisman.getTranslationKey());
		registerObj(r, kleinStars, kleinStars.getTranslationKey());
		registerObj(r, fuels, fuels.getTranslationKey());
		registerObj(r, covalence, covalence.getTranslationKey());
		registerObj(r, matter, matter.getTranslationKey());

		registerObj(r, dmPick, dmPick.getTranslationKey());
		registerObj(r, dmAxe, dmAxe.getTranslationKey());
		registerObj(r, dmShovel, dmShovel.getTranslationKey());
		registerObj(r, dmSword, dmSword.getTranslationKey());
		registerObj(r, dmHoe, dmHoe.getTranslationKey());
		registerObj(r, dmShears, dmShears.getTranslationKey());
		registerObj(r, dmHammer, dmHammer.getTranslationKey());

		registerObj(r, rmPick, rmPick.getTranslationKey());
		registerObj(r, rmAxe, rmAxe.getTranslationKey());
		registerObj(r, rmShovel, rmShovel.getTranslationKey());
		registerObj(r, rmSword, rmSword.getTranslationKey());
		registerObj(r, rmHoe, rmHoe.getTranslationKey());
		registerObj(r, rmShears, rmShears.getTranslationKey());
		registerObj(r, rmHammer, rmHammer.getTranslationKey());
		registerObj(r, rmKatar, rmKatar.getTranslationKey());
		registerObj(r, rmStar, rmStar.getTranslationKey());

		registerObj(r, dmHelmet, dmHelmet.getTranslationKey());
		registerObj(r, dmChest, dmChest.getTranslationKey());
		registerObj(r, dmLegs, dmLegs.getTranslationKey());
		registerObj(r, dmFeet, dmFeet.getTranslationKey());

		registerObj(r, rmHelmet, rmHelmet.getTranslationKey());
		registerObj(r, rmChest, rmChest.getTranslationKey());
		registerObj(r, rmLegs, rmLegs.getTranslationKey());
		registerObj(r, rmFeet, rmFeet.getTranslationKey());

		registerObj(r, gemHelmet, gemHelmet.getTranslationKey());
		registerObj(r, gemChest, gemChest.getTranslationKey());
		registerObj(r, gemLegs, gemLegs.getTranslationKey());
		registerObj(r, gemFeet, gemFeet.getTranslationKey());

		registerObj(r, ironBand, ironBand.getTranslationKey());
		registerObj(r, blackHole, blackHole.getTranslationKey());
		registerObj(r, angelSmite, angelSmite.getTranslationKey());
		registerObj(r, harvestGod, harvestGod.getTranslationKey());
		registerObj(r, ignition, ignition.getTranslationKey());
		registerObj(r, zero, zero.getTranslationKey());
		registerObj(r, swrg, swrg.getTranslationKey());
		registerObj(r, timeWatch, timeWatch.getTranslationKey());
		registerObj(r, eternalDensity, eternalDensity.getTranslationKey());
		registerObj(r, dRod1, dRod1.getTranslationKey());
		registerObj(r, dRod2, dRod2.getTranslationKey());
		registerObj(r, dRod3, dRod3.getTranslationKey());
		registerObj(r, mercEye, mercEye.getTranslationKey());
		registerObj(r, voidRing, voidRing.getTranslationKey());
		registerObj(r, arcana, arcana.getTranslationKey());

		registerObj(r, bodyStone, bodyStone.getTranslationKey());
		registerObj(r, soulStone, soulStone.getTranslationKey());
		registerObj(r, mindStone, mindStone.getTranslationKey());
		registerObj(r, lifeStone, lifeStone.getTranslationKey());

		registerObj(r, everTide, everTide.getTranslationKey());
		registerObj(r, volcanite, volcanite.getTranslationKey());

		registerObj(r, waterOrb, waterOrb.getTranslationKey());
		registerObj(r, lavaOrb, lavaOrb.getTranslationKey());
		registerObj(r, mobRandomizer, mobRandomizer.getTranslationKey());
		registerObj(r, lensExplosive, lensExplosive.getTranslationKey());
		registerObj(r, fireProjectile, fireProjectile.getTranslationKey());
		registerObj(r, windProjectile, windProjectile.getTranslationKey());

		registerObj(r, dCatalyst, dCatalyst.getTranslationKey());
		registerObj(r, hyperLens, hyperLens.getTranslationKey());
		registerObj(r, cataliticLens, cataliticLens.getTranslationKey());

		registerObj(r, tome, tome.getTranslationKey());
		registerObj(r, transmutationTablet, transmutationTablet.getTranslationKey());
		registerObj(r, manual, manual.getTranslationKey());
	}

	public static void register()
	{
		//Tile Entities
		GameRegistry.registerTileEntity(AlchChestTile.class, PECore.MODID + ":alchemical_chest");
		GameRegistry.registerTileEntity(InterdictionTile.class, PECore.MODID + ":interdiction_torch");
		GameRegistry.registerTileEntity(CondenserTile.class, PECore.MODID + ":condenser");
		GameRegistry.registerTileEntity(CondenserMK2Tile.class, PECore.MODID + ":condenser_mk2");
		GameRegistry.registerTileEntity(RMFurnaceTile.class, PECore.MODID + ":rm_furnace");
		GameRegistry.registerTileEntity(DMFurnaceTile.class, PECore.MODID + ":dm_furnace");
		GameRegistry.registerTileEntity(CollectorMK1Tile.class, PECore.MODID + ":collector_mk1");
		GameRegistry.registerTileEntity(CollectorMK2Tile.class, PECore.MODID + ":collector_mk2");
		GameRegistry.registerTileEntity(CollectorMK3Tile.class, PECore.MODID + ":collector_mk3");
		GameRegistry.registerTileEntity(RelayMK1Tile.class, PECore.MODID + ":relay_mk1");
		GameRegistry.registerTileEntity(RelayMK2Tile.class, PECore.MODID + ":relay_mk2");
		GameRegistry.registerTileEntity(RelayMK3Tile.class, PECore.MODID + ":relay_mk3");
		GameRegistry.registerTileEntity(DMPedestalTile.class, PECore.MODID + ":dm_pedestal");

		//Entities
		EntityRegistry.registerModEntity(new ResourceLocation(PECore.MODID, "water_projectile"), EntityWaterProjectile.class, "WaterProjectile", 1, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(PECore.MODID, "lava_projectile"), EntityLavaProjectile.class, "LavaProjectile", 2, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(PECore.MODID, "mob_randomizer"), EntityMobRandomizer.class, "MobRandomizer", 4, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(PECore.MODID, "lens_projectile"), EntityLensProjectile.class, "LensProjectile", 5, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(PECore.MODID, "nova_catalyst_primed"), EntityNovaCatalystPrimed.class, "NovaCatalystPrimed", 6, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(PECore.MODID, "nova_cataclysm_primed"), EntityNovaCataclysmPrimed.class, "NovaCataclysmPrimed", 7, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(PECore.MODID, "homing_arrow"), EntityHomingArrow.class, "HomingArrow", 8, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(PECore.MODID, "fire_projectile"), EntityFireProjectile.class, "FireProjectile", 9, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(PECore.MODID, "swrg_projectile"), EntitySWRGProjectile.class, "LightningProjectile", 10, PECore.instance, 256, 10, true);

		GameRegistry.registerFuelHandler(new FuelHandler());
	}

	@SubscribeEvent
	public static void addRecipes(RegistryEvent.Register<IRecipe> evt)
	{
		//Klein Stars
		for (int i = 1; i < 6; i++)
		{
			ItemStack input = new ItemStack(kleinStars, 1, i - 1);
			ItemStack output = new ItemStack(kleinStars, 1, i);
			RecipeShapelessKleinStar recipe = new RecipeShapelessKleinStar(PECore.MODID + ":klein", output, toIngredients(input, input, input, input));
			recipe.setRegistryName(PECore.MODID, String.format("klein_%d_to_%d", i - 1, i));
			evt.getRegistry().register(recipe);
		}

		evt.getRegistry().register(new RecipesCovalenceRepair().setRegistryName(PECore.MODID, "covalence_repair"));

		// RecipeSorter.register("Covalence Repair Recipes", RecipesCovalenceRepair.class, Category.SHAPELESS, "before:minecraft:shaped");
		// RecipeSorter.register("", RecipeShapedKleinStar.class, Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
		// RecipeSorter.register("", RecipeShapelessHidden.class, Category.SHAPELESS, "before:minecraft:shaped");

		registerPhiloStoneSmelting(evt.getRegistry());
	}
	
	private static <V extends IForgeRegistryEntry<V>> void registerObj(IForgeRegistry<V> registry, IForgeRegistryEntry<V> o, String name)
	{
		registerObj(registry, o, new ResourceLocation(PECore.MODID, name));
	}

	private static <V extends IForgeRegistryEntry<V>> void registerObj(IForgeRegistry<V> registry, IForgeRegistryEntry<V> o, ResourceLocation name)
	{
		registry.register(o.setRegistryName(name));
	}

	private static NonNullList<Ingredient> toIngredients(ItemStack... stacks) {
		NonNullList<Ingredient> ingr = NonNullList.create();
		for (ItemStack stack : stacks) {
			ingr.add(Ingredient.fromStacks(stack));
		}
		return ingr;
	}

	private static void registerPhiloStoneSmelting(IForgeRegistry<IRecipe> registry)
	{
		for (Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet())
		{
			if (entry.getKey().isEmpty() || entry.getValue().isEmpty())
			{
				continue;
			}

			ItemStack input = entry.getKey();
			ItemStack output = entry.getValue().copy();
			output.setCount(output.getCount() * 7);

			String inputName = input.getItem().getRegistryName().toString().replace(':', '_')+ "_" + input.getItemDamage();
			ResourceLocation recipeName = new ResourceLocation(PECore.MODID, "philstone_smelt_" + inputName);
			registry.register(new RecipeShapelessHidden("", output,
									toIngredients(new ItemStack(philosStone), input, input, input, input, input, input, input, new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE)))
								.setRegistryName(recipeName));
		}
		// RecipeSorter.register("Philosopher's Smelting Recipes", RecipeShapelessHidden.class, Category.SHAPELESS, "before:minecraft:shaped");
	}

	public static class FuelHandler implements IFuelHandler
	{
		@Override
		public int getBurnTime(ItemStack fuel)
		{
			if (fuel.getItem() == fuels)
			{
				switch (fuel.getItemDamage())
				{
					case 0:
						return Constants.ALCH_BURN_TIME;
					case 1:
						return Constants.MOBIUS_BURN_TIME;
					case 2:
						return Constants.AETERNALIS_BURN_TIME;
				}
			} else if (fuel.getItem() == Item.getItemFromBlock(fuelBlock))
			{
				switch (fuel.getItemDamage())
				{
					case 0:
						return Constants.ALCH_BURN_TIME * 9;
					case 1:
						return Constants.MOBIUS_BURN_TIME * 9;
					case 2:
						return Constants.AETERNALIS_BURN_TIME * 9;
				}
			}

			return 0;
		}
	}
}
