package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
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
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapedKleinStar;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.Map.Entry;

public class ObjHandler
{
	public static final CreativeTabs cTab = new CreativeTab();
	public static final Block alchChest = new AlchemicalChest();
	public static final Block confuseTorch = new InterdictionTorch();
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
	public static final Block energyCollector = new Collector(1);
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

	public static final Item ironBand = new ItemPE().setUnlocalizedName("ring_iron_band");
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
	public static final Item dRod1 = new DiviningRod(new String[] { "3x3x3" }).setUnlocalizedName("divining_rod_1");
	public static final Item dRod2 = new DiviningRod(new String[]{ "3x3x3", "16x3x3" }).setUnlocalizedName("divining_rod_2");
	public static final Item dRod3 = new DiviningRod(new String[] { "3x3x3", "16x3x3", "64x3x3" }).setUnlocalizedName("divining_rod_3");
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

	public static final Item waterOrb = new Item().setUnlocalizedName("pe_water_orb");
	public static final Item lavaOrb = new Item().setUnlocalizedName("pe_lava_orb");
	public static final Item mobRandomizer = new Item().setUnlocalizedName("pe_randomizer");
	public static final Item lensExplosive = new Item().setUnlocalizedName("pe_lens_explosive");
	public static final Item fireProjectile = new Item().setUnlocalizedName("pe_fire_projectile");
	public static final Item windProjectile = new Item().setUnlocalizedName("pe_wind_projectile");
	public static final Item transmutationTablet = new TransmutationTablet();
	public static final Item manual = new PEManual();

	public static void register()
	{
		// Blocks without special ItemBlock
		registerBlockWithItem(confuseTorch, "interdiction_torch");
		registerBlockWithItem(condenserMk2, "condenser_mk2");
		registerBlockWithItem(dmPedestal, "dm_pedestal");
		registerBlockWithItem(novaCatalyst, "nova_catalyst");
		registerBlockWithItem(novaCataclysm, "nova_cataclysm");

		// Blocks without any item form
		registerObj(rmFurnaceOn, "rm_furnace_lit");
		registerObj(dmFurnaceOn, "dm_furnace_lit");

		// Blocks with ItemBlock
		registerBlockWithItem(alchChest, new ItemAlchemyChestBlock(alchChest), "alchemical_chest");
		registerBlockWithItem(transmuteStone, new ItemTransmutationBlock(transmuteStone), "transmutation_table");
		registerBlockWithItem(condenser, new ItemCondenserBlock(condenser), "condenser_mk1");
		registerBlockWithItem(rmFurnaceOff, new ItemRMFurnaceBlock(rmFurnaceOff), "rm_furnace");
		registerBlockWithItem(dmFurnaceOff, new ItemDMFurnaceBlock(dmFurnaceOff), "dm_furnace");
		registerBlockWithItem(matterBlock, new ItemMatterBlock(matterBlock), "matter_block");
		registerBlockWithItem(fuelBlock, new ItemFuelBlock(fuelBlock), "fuel_block");
		registerBlockWithItem(energyCollector, new ItemCollectorBlock(energyCollector), "collector_mk1");
		registerBlockWithItem(collectorMK2, new ItemCollectorBlock(collectorMK2), "collector_mk2");
		registerBlockWithItem(collectorMK3, new ItemCollectorBlock(collectorMK3), "collector_mk3");
		registerBlockWithItem(relay, new ItemRelayBlock(relay), "relay_mk1");
		registerBlockWithItem(relayMK2, new ItemRelayBlock(relayMK2), "relay_mk2");
		registerBlockWithItem(relayMK3, new ItemRelayBlock(relayMK3), "relay_mk3");

		//Items
		registerObj(philosStone, philosStone.getUnlocalizedName());
		registerObj(alchBag, alchBag.getUnlocalizedName());
		registerObj(repairTalisman, repairTalisman.getUnlocalizedName());
		registerObj(kleinStars, kleinStars.getUnlocalizedName());
		registerObj(fuels, fuels.getUnlocalizedName());
		registerObj(covalence, covalence.getUnlocalizedName());
		registerObj(matter, matter.getUnlocalizedName());

		registerObj(dmPick, dmPick.getUnlocalizedName());
		registerObj(dmAxe, dmAxe.getUnlocalizedName());
		registerObj(dmShovel, dmShovel.getUnlocalizedName());
		registerObj(dmSword, dmSword.getUnlocalizedName());
		registerObj(dmHoe, dmHoe.getUnlocalizedName());
		registerObj(dmShears, dmShears.getUnlocalizedName());
		registerObj(dmHammer, dmHammer.getUnlocalizedName());

		registerObj(rmPick, rmPick.getUnlocalizedName());
		registerObj(rmAxe, rmAxe.getUnlocalizedName());
		registerObj(rmShovel, rmShovel.getUnlocalizedName());
		registerObj(rmSword, rmSword.getUnlocalizedName());
		registerObj(rmHoe, rmHoe.getUnlocalizedName());
		registerObj(rmShears, rmShears.getUnlocalizedName());
		registerObj(rmHammer, rmHammer.getUnlocalizedName());
		registerObj(rmKatar, rmKatar.getUnlocalizedName());
		registerObj(rmStar, rmStar.getUnlocalizedName());

		registerObj(dmHelmet, dmHelmet.getUnlocalizedName());
		registerObj(dmChest, dmChest.getUnlocalizedName());
		registerObj(dmLegs, dmLegs.getUnlocalizedName());
		registerObj(dmFeet, dmFeet.getUnlocalizedName());

		registerObj(rmHelmet, rmHelmet.getUnlocalizedName());
		registerObj(rmChest, rmChest.getUnlocalizedName());
		registerObj(rmLegs, rmLegs.getUnlocalizedName());
		registerObj(rmFeet, rmFeet.getUnlocalizedName());

		registerObj(gemHelmet, gemHelmet.getUnlocalizedName());
		registerObj(gemChest, gemChest.getUnlocalizedName());
		registerObj(gemLegs, gemLegs.getUnlocalizedName());
		registerObj(gemFeet, gemFeet.getUnlocalizedName());

		registerObj(ironBand, ironBand.getUnlocalizedName());
		registerObj(blackHole, blackHole.getUnlocalizedName());
		registerObj(angelSmite, angelSmite.getUnlocalizedName());
		registerObj(harvestGod, harvestGod.getUnlocalizedName());
		registerObj(ignition, ignition.getUnlocalizedName());
		registerObj(zero, zero.getUnlocalizedName());
		registerObj(swrg, swrg.getUnlocalizedName());
		registerObj(timeWatch, timeWatch.getUnlocalizedName());
		registerObj(eternalDensity, eternalDensity.getUnlocalizedName());
		registerObj(dRod1, dRod1.getUnlocalizedName());
		registerObj(dRod2, dRod2.getUnlocalizedName());
		registerObj(dRod3, dRod3.getUnlocalizedName());
		registerObj(mercEye, mercEye.getUnlocalizedName());
		registerObj(voidRing, voidRing.getUnlocalizedName());
		registerObj(arcana, arcana.getUnlocalizedName());

		registerObj(bodyStone, bodyStone.getUnlocalizedName());
		registerObj(soulStone, soulStone.getUnlocalizedName());
		registerObj(mindStone, mindStone.getUnlocalizedName());
		registerObj(lifeStone, lifeStone.getUnlocalizedName());

		registerObj(everTide, everTide.getUnlocalizedName());
		registerObj(volcanite, volcanite.getUnlocalizedName());

		registerObj(waterOrb, waterOrb.getUnlocalizedName());
		registerObj(lavaOrb, lavaOrb.getUnlocalizedName());
		registerObj(mobRandomizer, mobRandomizer.getUnlocalizedName());
		registerObj(lensExplosive, lensExplosive.getUnlocalizedName());
		registerObj(fireProjectile, fireProjectile.getUnlocalizedName());
		registerObj(windProjectile, windProjectile.getUnlocalizedName());

		registerObj(dCatalyst, dCatalyst.getUnlocalizedName());
		registerObj(hyperLens, hyperLens.getUnlocalizedName());
		registerObj(cataliticLens, cataliticLens.getUnlocalizedName());

		registerObj(tome, tome.getUnlocalizedName());
		registerObj(transmutationTablet, transmutationTablet.getUnlocalizedName());
		registerObj(manual, manual.getUnlocalizedName());

		//Tile Entities
		GameRegistry.registerTileEntityWithAlternatives(AlchChestTile.class, "AlchChestTile", "Alchemical Chest Tile");
		GameRegistry.registerTileEntityWithAlternatives(InterdictionTile.class, "InterdictionTile", "Interdiction Torch Tile");
		GameRegistry.registerTileEntityWithAlternatives(CondenserTile.class, "CondenserTile", "Condenser Tile");
		GameRegistry.registerTileEntityWithAlternatives(CondenserMK2Tile.class, "CondenserMK2Tile", "Condenser MK2 Tile");
		GameRegistry.registerTileEntityWithAlternatives(RMFurnaceTile.class, "RMFurnaceTile", "RM Furnace Tile");
		GameRegistry.registerTileEntityWithAlternatives(DMFurnaceTile.class, "DMFurnaceTile", "DM Furnace Tile");
		GameRegistry.registerTileEntityWithAlternatives(CollectorMK1Tile.class, "CollectorMK1Tile", "Energy Collector MK1 Tile");
		GameRegistry.registerTileEntityWithAlternatives(CollectorMK2Tile.class, "CollectorMK2Tile", "Energy Collector MK2 Tile");
		GameRegistry.registerTileEntityWithAlternatives(CollectorMK3Tile.class, "CollectorMK3Tile", "Energy Collector MK3 Tile");
		GameRegistry.registerTileEntityWithAlternatives(RelayMK1Tile.class, "RelayMK1Tile", "AM Relay MK1 Tile");
		GameRegistry.registerTileEntityWithAlternatives(RelayMK2Tile.class, "RelayMK2Tile", "AM Relay MK2 Tile");
		GameRegistry.registerTileEntityWithAlternatives(RelayMK3Tile.class, "RelayMK3Tile", "AM Relay MK3 Tile");
		GameRegistry.registerTileEntityWithAlternatives(DMPedestalTile.class, "DMPedestalTile", "DM Pedestal Tile");

		//Entities
		EntityRegistry.registerModEntity(EntityWaterProjectile.class, "WaterProjectile", 1, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(EntityLavaProjectile.class, "LavaProjectile", 2, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(EntityMobRandomizer.class, "MobRandomizer", 4, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(EntityLensProjectile.class, "LensProjectile", 5, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(EntityNovaCatalystPrimed.class, "NovaCatalystPrimed", 6, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(EntityNovaCataclysmPrimed.class, "NovaCataclysmPrimed", 7, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(EntityHomingArrow.class, "HomingArrow", 8, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(EntityFireProjectile.class, "FireProjectile", 9, PECore.instance, 256, 10, true);
		EntityRegistry.registerModEntity(EntitySWRGProjectile.class, "LightningProjectile", 10, PECore.instance, 256, 10, true);
	}

	public static void addRecipes()
	{
		String diamondReplacement = "gemDiamond";
		String diamondBlockReplacement = "blockDiamond";

		if (ProjectEConfig.altCraftingMat)
		{
			diamondReplacement = "netherStar";
			diamondBlockReplacement = "netherStar";
		}

		//Shaped Recipes
		//Philos Stone
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(philosStone), "RGR", "GDG", "RGR", 'R', "dustRedstone", 'G', "dustGlowstone", 'D', diamondReplacement));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(philosStone), "GRG", "RDR", "GRG", 'R', "dustRedstone", 'G', "dustGlowstone", 'D', diamondReplacement));

		//Interdiction torch
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(confuseTorch, 2), "RDR", "DPD", "GGG", 'R', Blocks.REDSTONE_TORCH, 'G', "dustGlowstone", 'D', "gemDiamond", 'P', philosStone));

		//Repair Talisman
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(repairTalisman), "LMH", "SPS", "HML", 'P', "paper", 'S', "string", 'L', new ItemStack(covalence, 1, 0), 'M', new ItemStack(covalence, 1, 1), 'H', new ItemStack(covalence, 1, 2)));

		//Klein Star Ein
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(kleinStars, 1, 0), "MMM", "MDM", "MMM", 'M', new ItemStack(fuels, 1, 1), 'D', "gemDiamond"));

		//Matter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(matter, 1, 0), "AAA", "ADA", "AAA", 'D', "blockDiamond", 'A', new ItemStack(fuels, 1, 2)));
		GameRegistry.addRecipe(new ItemStack(matter, 1, 1), "AAA", "DDD", "AAA", 'D', matter, 'A', new ItemStack(fuels, 1, 2));
		GameRegistry.addRecipe(new ItemStack(matter, 1, 1), "ADA", "ADA", "ADA", 'D', matter, 'A', new ItemStack(fuels, 1, 2));

		//Alchemical Chest
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(alchChest), "LMH", "SDS", "ICI", 'D', diamondReplacement, 'L', new ItemStack(covalence, 1, 0), 'M', new ItemStack(covalence, 1, 1), 'H', new ItemStack(covalence, 1, 2), 'S', "stone", 'I', "ingotIron", 'C', "chestWood"));

		//Alchemical Bags
		for (int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new ItemStack(alchBag, 1, i), "CCC", "WAW", "WWW", 'C', new ItemStack(covalence, 1, 2), 'A', alchChest, 'W', new ItemStack(Blocks.WOOL, 1, i));
		}

		//Condenser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(condenser), "ODO", "DCD", "ODO", 'D', "gemDiamond", 'O', "obsidian", 'C', new ItemStack(alchChest)));

		//Condenser MK2
		GameRegistry.addRecipe(new ItemStack(condenserMk2), "RDR", "DCD", "RDR", 'D', new ItemStack(matterBlock, 1, 0), 'R', new ItemStack(matterBlock, 1, 1), 'C', condenser);

		//Transmutation Table
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(transmuteStone), "OSO", "SPS", "OSO", 'S', "stone", 'O', "obsidian", 'P', philosStone));

		//Matter Blocks
		GameRegistry.addRecipe(new ItemStack(matterBlock, 1, 0), "DD", "DD", 'D', matter);
		GameRegistry.addRecipe(new ItemStack(matterBlock, 1, 1), "DD", "DD", 'D', new ItemStack(matter, 1, 1));

		//Matter Furnaces
		GameRegistry.addRecipe(new ItemStack(dmFurnaceOff), "DDD", "DFD", "DDD", 'D', new ItemStack(matterBlock, 1, 0), 'F', Blocks.FURNACE);
		GameRegistry.addRecipe(new ItemStack(rmFurnaceOff), "XRX", "RFR", 'R', new ItemStack(matterBlock, 1, 1), 'F', dmFurnaceOff);

		// DM Pedestal
		GameRegistry.addRecipe(new ItemStack(dmPedestal), "RDR", "RDR", "DDD", 'R', new ItemStack(matter, 1, 1), 'D', new ItemStack(matterBlock, 1, 0));

		//Collectors
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(energyCollector), "GTG", "GDG", "GFG", 'G', "glowstone", 'F', Blocks.FURNACE, 'D', diamondBlockReplacement, 'T', "blockGlassColorless"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(collectorMK2), "GDG", "GCG", "GGG", 'G', "glowstone", 'C', energyCollector, 'D', matter));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(collectorMK3), "GRG", "GCG", "GGG", 'G', "glowstone", 'C', collectorMK2, 'R', new ItemStack(matter, 1, 1)));

		//AM Relays
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(relay), "OSO", "ODO", "OOO", 'S', "blockGlassColorless", 'D', "blockDiamond", 'O', "obsidian"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(relayMK2), "ODO", "OAO", "OOO", 'A', relay, 'D', matter, 'O', "obsidian"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(relayMK3), "ORO", "OAO", "OOO", 'A', relayMK2, 'R', new ItemStack(matter, 1, 1), 'O', "obsidian"));

		//DM Tools
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dmPick), "MMM", "XDX", "XDX", 'D', "gemDiamond", 'M', matter));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dmAxe), "MMX", "MDX", "XDX", 'D', "gemDiamond", 'M', matter));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dmShovel), "XMX", "XDX", "XDX", 'D', "gemDiamond", 'M', matter));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dmSword), "XMX", "XMX", "XDX", 'D', "gemDiamond", 'M', matter));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dmHoe), "MMX", "XDX", "XDX", 'D', "gemDiamond", 'M', matter));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dmShears), "XM", "DX", 'D', "gemDiamond", 'M', matter));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dmHammer), "MDM", "XDX", "XDX", 'D', "gemDiamond", 'M', matter));

		//RM Tools
		GameRegistry.addRecipe(new ItemStack(rmPick), "RRR", "XPX", "XMX", 'R', new ItemStack(matter, 1, 1), 'P', dmPick, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(rmAxe), "RRX", "RAX", "XMX", 'R', new ItemStack(matter, 1, 1), 'A', dmAxe, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(rmShovel), "XRX", "XSX", "XMX", 'R', new ItemStack(matter, 1, 1), 'S', dmShovel, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(rmSword), "XRX", "XRX", "XSX", 'R', new ItemStack(matter, 1, 1), 'S', dmSword);
		GameRegistry.addRecipe(new ItemStack(rmHoe), "RRX", "XHX", "XMX", 'R', new ItemStack(matter, 1, 1), 'H', dmHoe, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(rmShears), "XR", "SX", 'R', new ItemStack(matter, 1, 1), 'S', dmShears);
		GameRegistry.addRecipe(new ItemStack(rmHammer), "RMR", "XHX", "XMX", 'R', new ItemStack(matter, 1, 1), 'H', dmHammer, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(rmKatar), "123", "4RR", "RRR", '1', rmShears, '2', rmAxe, '3', rmSword, '4', rmHoe, 'R', new ItemStack(matter, 1, 1));
		GameRegistry.addRecipe(new ItemStack(rmStar), "123", "RRR", "RRR", '1', rmHammer, '2', rmPick, '3', rmShovel, 'R', new ItemStack(matter, 1, 1));

		//Armor
		GameRegistry.addRecipe(new ItemStack(dmHelmet), "MMM", "MXM", 'M', matter);
		GameRegistry.addRecipe(new ItemStack(dmChest), "MXM", "MMM", "MMM", 'M', matter);
		GameRegistry.addRecipe(new ItemStack(dmLegs), "MMM", "MXM", "MXM", 'M', matter);
		GameRegistry.addRecipe(new ItemStack(dmFeet), "MXM", "MXM", 'M', matter);

		GameRegistry.addRecipe(new ItemStack(rmHelmet), "MMM", "MDM", 'M', new ItemStack(matter, 1, 1), 'D', dmHelmet);
		GameRegistry.addRecipe(new ItemStack(rmChest), "MDM", "MMM", "MMM", 'M', new ItemStack(matter, 1, 1), 'D', dmChest);
		GameRegistry.addRecipe(new ItemStack(rmLegs), "MMM", "MDM", "MXM", 'M', new ItemStack(matter, 1, 1), 'D', dmLegs);
		GameRegistry.addRecipe(new ItemStack(rmFeet), "MDM", "MXM", 'M', new ItemStack(matter, 1, 1), 'D', dmFeet);

		//Rings
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ironBand), "III", "ILI", "III", 'I', "ingotIron", 'L', Items.LAVA_BUCKET));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ironBand), "III", "ILI", "III", 'I', "ingotIron", 'L', volcanite));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(harvestGod), "SFS", "DID", "SFS", 'I', ironBand, 'S', "treeSapling", 'F', Blocks.RED_FLOWER, 'F', Blocks.RED_FLOWER, 'D', matter));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(swrg), "DFD", "FIF", "DFD", 'I', ironBand, 'F', "feather", 'D', matter));
		GameRegistry.addRecipe(new ItemStack(ignition), "FMF", "DID", "FMF", 'I', ironBand, 'F', new ItemStack(Items.FLINT_AND_STEEL, 1, OreDictionary.WILDCARD_VALUE), 'D', matter, 'M', new ItemStack(fuels, 1, 1));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bodyStone), "SSS", "RLR", "SSS", 'R', new ItemStack(matter, 1, 1), 'S', Items.SUGAR, 'L', "gemLapis"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(soulStone), "GGG", "RLR", "GGG", 'R', new ItemStack(matter, 1, 1), 'G', "dustGlowstone", 'L', "gemLapis"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(mindStone), "BBB", "RLR", "BBB", 'R', new ItemStack(matter, 1, 1), 'B', Items.BOOK, 'L', "gemLapis"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blackHole), "SSS", "DID", "SSS", 'I', ironBand, 'S', "string", 'D', matter));
		GameRegistry.addRecipe(new ItemStack(everTide), "WWW", "DDD", "WWW", 'W', Items.WATER_BUCKET, 'D', matter);
		GameRegistry.addRecipe(new ItemStack(volcanite), "LLL", "DDD", "LLL", 'L', Items.LAVA_BUCKET, 'D', matter);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(eternalDensity), "DOD", "MDM", "DOD", 'D', "gemDiamond", 'O', "obsidian", 'M', matter));
		GameRegistry.addRecipe(new ItemStack(zero), "SBS", "MIM", "SBS", 'S', Blocks.SNOW, 'B', Items.SNOWBALL, 'M', matter, 'I', ironBand);
		GameRegistry.addShapelessRecipe(new ItemStack(voidRing), blackHole, eternalDensity, new ItemStack(matter, 1, 1), new ItemStack(matter, 1, 1));
		GameRegistry.addRecipe(new ItemStack(arcana), "ZIH", "SMM", "MMM", 'Z', zero, 'I', ignition, 'H', harvestGod, 'S', swrg, 'M', new ItemStack(matter, 1, 1));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(angelSmite), "BFB", "MIM", "BFB", 'B', Items.BOW, 'F', "feather", 'M', matter, 'I', ironBand));

		//Watch of flowing time
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(timeWatch), "DOD", "GCG", "DOD", 'D', matter, 'O', "obsidian", 'G', "glowstone", 'C', Items.CLOCK));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(timeWatch), "DGD", "OCO", "DGD", 'D', matter, 'O', "obsidian", 'G', "glowstone", 'C', Items.CLOCK));

		//Divining rods
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dRod1), "DDD", "DSD", "DDD", 'D', covalence, 'S', "stickWood"));
		GameRegistry.addRecipe(new ItemStack(dRod2), "DDD", "DSD", "DDD", 'D', new ItemStack(covalence, 1, 1), 'S', dRod1);
		GameRegistry.addRecipe(new ItemStack(dRod3), "DDD", "DSD", "DDD", 'D', new ItemStack(covalence, 1, 2), 'S', dRod2);

		//Explosive items
		GameRegistry.addRecipe(new ItemStack(dCatalyst), "NMN", "MFM", "NMN", 'N', novaCatalyst, 'M', new ItemStack(fuels, 1, 1), 'F', new ItemStack(Items.FLINT_AND_STEEL, 1, OreDictionary.WILDCARD_VALUE));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(hyperLens), "DDD", "MNM", "DDD", 'N', novaCatalyst, 'M', matter, 'D', "gemDiamond"));
		GameRegistry.addRecipe(new ItemStack(cataliticLens), "MMM", "HMD", "MMM", 'M', matter, 'H', hyperLens, 'D', dCatalyst);
		GameRegistry.addRecipe(new ItemStack(cataliticLens), "MMM", "DMH", "MMM", 'M', matter, 'H', hyperLens, 'D', dCatalyst);

		//Fuel Blocks
		GameRegistry.addRecipe(new ItemStack(fuelBlock, 1, 0), "FFF", "FFF", "FFF", 'F', fuels);
		GameRegistry.addRecipe(new ItemStack(fuelBlock, 1, 1), "FFF", "FFF", "FFF", 'F', new ItemStack(fuels, 1, 1));
		GameRegistry.addRecipe(new ItemStack(fuelBlock, 1, 2), "FFF", "FFF", "FFF", 'F', new ItemStack(fuels, 1, 2));

		//Tome
		if (ProjectEConfig.craftableTome)
		{
			GameRegistry.addRecipe(new ItemStack(tome), "HML", "KBK", "LMH", 'L', new ItemStack(covalence, 1, 0), 'M', new ItemStack(covalence, 1, 1), 'H', new ItemStack(covalence, 1, 2), 'B', Items.BOOK, 'K', new ItemStack(kleinStars, 1, 5));
		}

		//Manual
		//GameRegistry.addShapelessRecipe(new ItemStack(manual, 1, 0), Items.BOOK, new ItemStack(covalence, 1, 0));
		//GameRegistry.addShapelessRecipe(new ItemStack(manual, 1, 0), Items.BOOK, new ItemStack(covalence, 1, 1));
		//GameRegistry.addShapelessRecipe(new ItemStack(manual, 1, 0), Items.BOOK, new ItemStack(covalence, 1, 2));

		//TransmutationTablet
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(transmutationTablet), "DSD", "STS", "DSD", 'D', new ItemStack(matterBlock, 1, 0), 'S', "stone", 'T', transmuteStone));

		//Mercurial Eye
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(mercEye), "OBO", "BRB", "BDB", 'O', "obsidian", 'B', Blocks.BRICK_BLOCK, 'R', new ItemStack(matter, 1, 1), 'D', "gemDiamond"));

		//Shapeless Recipes
		//Philos Stone exchanges
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.ENDER_PEARL), philosStone, "ingotIron", "ingotIron", "ingotIron", "ingotIron"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.IRON_INGOT, 8), philosStone, "ingotGold"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.GOLD_INGOT), philosStone, "ingotIron", "ingotIron", "ingotIron", "ingotIron", "ingotIron", "ingotIron", "ingotIron", "ingotIron"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.DIAMOND), philosStone, "ingotGold", "ingotGold", "ingotGold", "ingotGold"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.GOLD_INGOT, 4), philosStone, "gemDiamond"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.EMERALD), philosStone, "gemDiamond", "gemDiamond"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.DIAMOND, 2), philosStone, "gemEmerald"));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 1, 0), philosStone, Items.COAL, Items.COAL, Items.COAL, Items.COAL);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.COAL, 4), philosStone, new ItemStack(fuels, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 1, 1), philosStone, new ItemStack(fuels, 1, 0), new ItemStack(fuels, 1, 0), new ItemStack(fuels, 1, 0), new ItemStack(fuels, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 4, 0), philosStone, new ItemStack(fuels, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 1, 2), philosStone, new ItemStack(fuels, 1, 1), new ItemStack(fuels, 1, 1), new ItemStack(fuels, 1, 1), new ItemStack(fuels, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 4, 1), philosStone, new ItemStack(fuels, 1, 2));

		//Covalence dust
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(covalence, 40, 0), "cobblestone", "cobblestone", "cobblestone", "cobblestone", "cobblestone", "cobblestone", "cobblestone", "cobblestone", new ItemStack(Items.COAL, 1, 1)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(covalence, 40, 1), "ingotIron", "dustRedstone"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(covalence, 40, 2), "gemDiamond", Items.COAL));

		//Klein Stars
		for (int i = 1; i < 6; i++)
		{
			ItemStack input = new ItemStack(kleinStars, 1, i - 1);
			ItemStack output = new ItemStack(kleinStars, 1, i);
			GameRegistry.addRecipe(new RecipeShapelessHidden(output, input, input, input, input));
		}

		//Other items
		GameRegistry.addShapelessRecipe(new ItemStack(novaCatalyst, 2), Blocks.TNT, new ItemStack(fuels, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(novaCataclysm, 2), novaCatalyst, new ItemStack(fuels, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(lifeStone), bodyStone, soulStone);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.ICE), new ItemStack(zero, 1, OreDictionary.WILDCARD_VALUE), Items.WATER_BUCKET);
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.LAVA_BUCKET), volcanite, Items.BUCKET, "dustRedstone"));

		GameRegistry.addShapelessRecipe(new ItemStack(gemHelmet), rmHelmet, new ItemStack(kleinStars, 1, 5), everTide, soulStone);
		GameRegistry.addShapelessRecipe(new ItemStack(gemChest), rmChest, new ItemStack(kleinStars, 1, 5), volcanite, bodyStone);
		GameRegistry.addShapelessRecipe(new ItemStack(gemLegs), rmLegs, new ItemStack(kleinStars, 1, 5), blackHole, timeWatch);
		GameRegistry.addShapelessRecipe(new ItemStack(gemFeet), rmFeet, new ItemStack(kleinStars, 1, 5), swrg, swrg);

		GameRegistry.addShapelessRecipe(new ItemStack(matter, 4, 0), matterBlock);
		GameRegistry.addShapelessRecipe(new ItemStack(matter, 4, 1), new ItemStack(matterBlock, 1, 1));

		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 9, 0), new ItemStack(fuelBlock, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 9, 1), new ItemStack(fuelBlock, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 9, 2), new ItemStack(fuelBlock, 1, 2));

		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.ICE), new ItemStack(arcana, 1, OreDictionary.WILDCARD_VALUE), Items.WATER_BUCKET);
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.GRASS), new ItemStack(arcana, 1, OreDictionary.WILDCARD_VALUE), "dirt"));

		// Taken from OreDictionary class
		String[] dyes =
		{
			"Black",
			"Red",
			"Green",
			"Brown",
			"Blue",
			"Purple",
			"Cyan",
			"LightGray",
			"Gray",
			"Pink",
			"Lime",
			"Yellow",
			"LightBlue",
			"Magenta",
			"Orange",
			"White"
		};

		for (int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(alchBag, 1, i), new ItemStack(alchBag, 1, OreDictionary.WILDCARD_VALUE), "dye" + dyes[15 - i]));
		}

		GameRegistry.addRecipe(new RecipesCovalenceRepair());
		RecipeSorter.register("Covalence Repair Recipes", RecipesCovalenceRepair.class, Category.SHAPELESS, "before:minecraft:shaped");
		RecipeSorter.register("", RecipeShapedKleinStar.class, Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
		RecipeSorter.register("", RecipeShapelessHidden.class, Category.SHAPELESS, "before:minecraft:shaped");

		//Fuel Values
		GameRegistry.registerFuelHandler(new FuelHandler());
	}
	
	private static void registerObj(IForgeRegistryEntry<?> o, String name)
	{
		GameRegistry.register(o, new ResourceLocation(PECore.MODID, name));
	}

	private static void registerBlockWithItem(Block b, String name)
	{
		registerObj(b, name);
		registerObj(new ItemBlock(b), name);
	}

	private static void registerBlockWithItem(Block b, Item i, String name)
	{
		registerObj(b, name);
		registerObj(i, name);
	}

	/**
	 * Philosopher's stone smelting recipes, EE3 style
	 */
	@SuppressWarnings("unchecked")
	public static void registerPhiloStoneSmelting()
	{
		for (Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet())
		{
			if (entry.getKey() == null || entry.getValue() == null)
			{
				continue;
			}

			ItemStack input = entry.getKey();
			ItemStack output = entry.getValue().copy();
			output.stackSize *= 7;

			GameRegistry.addRecipe(new RecipeShapelessHidden(output, philosStone, input, input, input, input, input, input, input, new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE)));

		}
		RecipeSorter.register("Philosopher's Smelting Recipes", RecipeShapelessHidden.class, Category.SHAPELESS, "before:minecraft:shaped");
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
