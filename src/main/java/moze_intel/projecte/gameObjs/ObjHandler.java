package moze_intel.projecte.gameObjs;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
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
import moze_intel.projecte.gameObjs.customRecipes.RecipeAlchemyBag;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapedKleinStar;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import moze_intel.projecte.gameObjs.customRecipes.RecipesCovalenceRepair;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
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
import moze_intel.projecte.gameObjs.items.DiviningRodHigh;
import moze_intel.projecte.gameObjs.items.DiviningRodLow;
import moze_intel.projecte.gameObjs.items.DiviningRodMedium;
import moze_intel.projecte.gameObjs.items.EvertideAmulet;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.gameObjs.items.HyperkineticLens;
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
import moze_intel.projecte.gameObjs.items.itemEntities.FireProjectile;
import moze_intel.projecte.gameObjs.items.itemEntities.LavaOrb;
import moze_intel.projecte.gameObjs.items.itemEntities.LensExplosive;
import moze_intel.projecte.gameObjs.items.itemEntities.LightningProjectile;
import moze_intel.projecte.gameObjs.items.itemEntities.LootBallItem;
import moze_intel.projecte.gameObjs.items.itemEntities.RandomizerProjectile;
import moze_intel.projecte.gameObjs.items.itemEntities.WaterOrb;
import moze_intel.projecte.gameObjs.items.rings.Arcana;
import moze_intel.projecte.gameObjs.items.rings.ArchangelSmite;
import moze_intel.projecte.gameObjs.items.rings.BlackHoleBand;
import moze_intel.projecte.gameObjs.items.rings.BodyStone;
import moze_intel.projecte.gameObjs.items.rings.HarvestGoddess;
import moze_intel.projecte.gameObjs.items.rings.Ignition;
import moze_intel.projecte.gameObjs.items.rings.IronBand;
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
import moze_intel.projecte.utils.EnumArmorType;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashMap;
import java.util.Map.Entry;

public class ObjHandler
{
	public static final CreativeTabs cTab = new CreativeTab();
	public static Block alchChest = new AlchemicalChest();
	public static Block confuseTorch = new InterdictionTorch();
	public static Block transmuteStone = new TransmutationStone();
	public static Block condenser = new Condenser();
	public static Block condenserMk2 = new CondenserMK2();
	public static Block rmFurnaceOff = new MatterFurnace(false, true);
	public static Block rmFurnaceOn = new MatterFurnace(true, true);
	public static Block dmFurnaceOff = new MatterFurnace(false, false);
	public static Block dmFurnaceOn = new MatterFurnace(true, false);
	public static Block dmPedestal = new Pedestal();
	public static Block matterBlock = new MatterBlock();
	public static Block fuelBlock = new FuelBlock();
	public static Block energyCollector = new Collector(1);
	public static Block collectorMK2 = new Collector(2);
	public static Block collectorMK3 = new Collector(3);
	public static Block relay = new Relay(1);
	public static Block relayMK2 = new Relay(2);
	public static Block relayMK3 = new Relay(3);
	public static Block novaCatalyst = new NovaCatalyst();
	public static Block novaCataclysm = new NovaCataclysm();

	public static Item philosStone = new PhilosophersStone();
	public static Item alchBag = new AlchemicalBag();
	public static Item repairTalisman = new RepairTalisman();
	public static Item kleinStars = new KleinStar();
	public static Item fuels = new AlchemicalFuel();
	public static Item covalence = new CovalenceDust();
	public static Item matter = new Matter();

	public static Item dmPick = new DarkPick();
	public static Item dmAxe = new DarkAxe();
	public static Item dmShovel = new DarkShovel();
	public static Item dmSword = new DarkSword();
	public static Item dmHoe = new DarkHoe();
	public static Item dmShears = new DarkShears();
	public static Item dmHammer = new DarkHammer();

	public static Item rmPick = new RedPick();
	public static Item rmAxe = new RedAxe();
	public static Item rmShovel = new RedShovel();
	public static Item rmSword = new RedSword();
	public static Item rmHoe = new RedHoe();
	public static Item rmShears = new RedShears();
	public static Item rmHammer = new RedHammer();
	public static Item rmKatar = new RedKatar();
	public static Item rmStar = new RedStar();

	public static Item dmHelmet = new DMArmor(EnumArmorType.HEAD);
	public static Item dmChest = new DMArmor(EnumArmorType.CHEST);
	public static Item dmLegs = new DMArmor(EnumArmorType.LEGS);
	public static Item dmFeet = new DMArmor(EnumArmorType.FEET);

	public static Item rmHelmet = new RMArmor(EnumArmorType.HEAD);
	public static Item rmChest = new RMArmor(EnumArmorType.CHEST);
	public static Item rmLegs = new RMArmor(EnumArmorType.LEGS);
	public static Item rmFeet = new RMArmor(EnumArmorType.FEET);

	public static Item gemHelmet = new GemHelmet();
	public static Item gemChest = new GemChest();
	public static Item gemLegs = new GemLegs();
	public static Item gemFeet = new GemFeet();

	public static Item ironBand = new IronBand();
	public static Item blackHole = new BlackHoleBand();
	public static Item angelSmite = new ArchangelSmite();
	public static Item harvestGod = new HarvestGoddess();
	public static Item ignition = new Ignition();
	public static Item zero = new Zero();
	public static Item swrg = new SWRG();
	public static Item timeWatch = new TimeWatch();
	public static Item everTide = new EvertideAmulet();
	public static Item volcanite = new VolcaniteAmulet();
	public static Item eternalDensity = new GemEternalDensity();
	public static Item dRod1 = new DiviningRodLow();
	public static Item dRod2 = new DiviningRodMedium();
	public static Item dRod3 = new DiviningRodHigh();
	public static Item mercEye = new MercurialEye();
	public static Item voidRing = new VoidRing();
	public static Item arcana = new Arcana();

	public static Item dCatalyst = new DestructionCatalyst();
	public static Item hyperLens = new HyperkineticLens();
	public static Item cataliticLens = new CataliticLens();

	public static Item bodyStone = new BodyStone();
	public static Item soulStone = new SoulStone();
	public static Item mindStone = new MindStone();
	public static Item lifeStone = new LifeStone();

	public static Item tome = new Tome();

	public static Item waterOrb = new WaterOrb();
	public static Item lavaOrb = new LavaOrb();
	public static Item lootBall = new LootBallItem();
	public static Item mobRandomizer = new RandomizerProjectile();
	public static Item lensExplosive = new LensExplosive();
	public static Item fireProjectile = new FireProjectile();
	public static Item windProjectile = new LightningProjectile();
	public static Item transmutationTablet = new TransmutationTablet();
	public static Item manual = new PEManual();

	public static void register()
	{
		// Blocks without ItemBlock
		GameRegistry.registerBlock(confuseTorch, "interdiction_torch");
		GameRegistry.registerBlock(condenserMk2, "condenser_mk2");
		GameRegistry.registerBlock(rmFurnaceOn, "rm_furnace_lit");
		GameRegistry.registerBlock(dmFurnaceOn, "dm_furnace_lit");
		GameRegistry.registerBlock(dmPedestal, "dm_pedestal");
		GameRegistry.registerBlock(novaCatalyst, "nova_catalyst");
		GameRegistry.registerBlock(novaCataclysm, "nova_cataclysm");

		// Blocks with ItemBlock
		GameRegistry.registerBlock(alchChest, ItemAlchemyChestBlock.class, "alchemical_chest");
		GameRegistry.registerBlock(transmuteStone, ItemTransmutationBlock.class, "transmutation_table");
		GameRegistry.registerBlock(condenser, ItemCondenserBlock.class, "condenser_mk1");
		GameRegistry.registerBlock(rmFurnaceOff, ItemRMFurnaceBlock.class, "rm_furnace");
		GameRegistry.registerBlock(dmFurnaceOff, ItemDMFurnaceBlock.class, "dm_furnace");
		GameRegistry.registerBlock(matterBlock, ItemMatterBlock.class, "matter_block");
		GameRegistry.registerBlock(fuelBlock, ItemFuelBlock.class, "fuel_block");
		GameRegistry.registerBlock(energyCollector, ItemCollectorBlock.class, "collector_mk1");
		GameRegistry.registerBlock(collectorMK2, ItemCollectorBlock.class, "collector_mk2");
		GameRegistry.registerBlock(collectorMK3, ItemCollectorBlock.class, "collector_mk3");
		GameRegistry.registerBlock(relay, ItemRelayBlock.class, "relay_mk1");
		GameRegistry.registerBlock(relayMK2, ItemRelayBlock.class, "relay_mk2");
		GameRegistry.registerBlock(relayMK3, ItemRelayBlock.class, "relay_mk3");

		//Items
		GameRegistry.registerItem(philosStone, philosStone.getUnlocalizedName());
		GameRegistry.registerItem(alchBag, alchBag.getUnlocalizedName());
		GameRegistry.registerItem(repairTalisman, repairTalisman.getUnlocalizedName());
		GameRegistry.registerItem(kleinStars, kleinStars.getUnlocalizedName());
		GameRegistry.registerItem(fuels, fuels.getUnlocalizedName());
		GameRegistry.registerItem(covalence, covalence.getUnlocalizedName());
		GameRegistry.registerItem(matter, matter.getUnlocalizedName());

		GameRegistry.registerItem(dmPick, dmPick.getUnlocalizedName());
		GameRegistry.registerItem(dmAxe, dmAxe.getUnlocalizedName());
		GameRegistry.registerItem(dmShovel, dmShovel.getUnlocalizedName());
		GameRegistry.registerItem(dmSword, dmSword.getUnlocalizedName());
		GameRegistry.registerItem(dmHoe, dmHoe.getUnlocalizedName());
		GameRegistry.registerItem(dmShears, dmShears.getUnlocalizedName());
		GameRegistry.registerItem(dmHammer, dmHammer.getUnlocalizedName());

		GameRegistry.registerItem(rmPick, rmPick.getUnlocalizedName());
		GameRegistry.registerItem(rmAxe, rmAxe.getUnlocalizedName());
		GameRegistry.registerItem(rmShovel, rmShovel.getUnlocalizedName());
		GameRegistry.registerItem(rmSword, rmSword.getUnlocalizedName());
		GameRegistry.registerItem(rmHoe, rmHoe.getUnlocalizedName());
		GameRegistry.registerItem(rmShears, rmShears.getUnlocalizedName());
		GameRegistry.registerItem(rmHammer, rmHammer.getUnlocalizedName());
		GameRegistry.registerItem(rmKatar, rmKatar.getUnlocalizedName());
		GameRegistry.registerItem(rmStar, rmStar.getUnlocalizedName());

		GameRegistry.registerItem(dmHelmet, dmHelmet.getUnlocalizedName());
		GameRegistry.registerItem(dmChest, dmChest.getUnlocalizedName());
		GameRegistry.registerItem(dmLegs, dmLegs.getUnlocalizedName());
		GameRegistry.registerItem(dmFeet, dmFeet.getUnlocalizedName());

		GameRegistry.registerItem(rmHelmet, rmHelmet.getUnlocalizedName());
		GameRegistry.registerItem(rmChest, rmChest.getUnlocalizedName());
		GameRegistry.registerItem(rmLegs, rmLegs.getUnlocalizedName());
		GameRegistry.registerItem(rmFeet, rmFeet.getUnlocalizedName());

		GameRegistry.registerItem(gemHelmet, gemHelmet.getUnlocalizedName());
		GameRegistry.registerItem(gemChest, gemChest.getUnlocalizedName());
		GameRegistry.registerItem(gemLegs, gemLegs.getUnlocalizedName());
		GameRegistry.registerItem(gemFeet, gemFeet.getUnlocalizedName());

		GameRegistry.registerItem(ironBand, ironBand.getUnlocalizedName());
		GameRegistry.registerItem(blackHole, blackHole.getUnlocalizedName());
		GameRegistry.registerItem(angelSmite, angelSmite.getUnlocalizedName());
		GameRegistry.registerItem(harvestGod, harvestGod.getUnlocalizedName());
		GameRegistry.registerItem(ignition, ignition.getUnlocalizedName());
		GameRegistry.registerItem(zero, zero.getUnlocalizedName());
		GameRegistry.registerItem(swrg, swrg.getUnlocalizedName());
		GameRegistry.registerItem(timeWatch, timeWatch.getUnlocalizedName());
		GameRegistry.registerItem(eternalDensity, eternalDensity.getUnlocalizedName());
		GameRegistry.registerItem(dRod1, dRod1.getUnlocalizedName());
		GameRegistry.registerItem(dRod2, dRod2.getUnlocalizedName());
		GameRegistry.registerItem(dRod3, dRod3.getUnlocalizedName());
		GameRegistry.registerItem(mercEye, mercEye.getUnlocalizedName());
		GameRegistry.registerItem(voidRing, voidRing.getUnlocalizedName());
		GameRegistry.registerItem(arcana, arcana.getUnlocalizedName());

		GameRegistry.registerItem(bodyStone, bodyStone.getUnlocalizedName());
		GameRegistry.registerItem(soulStone, soulStone.getUnlocalizedName());
		GameRegistry.registerItem(mindStone, mindStone.getUnlocalizedName());
		GameRegistry.registerItem(lifeStone, lifeStone.getUnlocalizedName());

		GameRegistry.registerItem(everTide, everTide.getUnlocalizedName());
		GameRegistry.registerItem(volcanite, volcanite.getUnlocalizedName());

		GameRegistry.registerItem(waterOrb, waterOrb.getUnlocalizedName());
		GameRegistry.registerItem(lavaOrb, lavaOrb.getUnlocalizedName());
		GameRegistry.registerItem(lootBall, lootBall.getUnlocalizedName());
		GameRegistry.registerItem(mobRandomizer, mobRandomizer.getUnlocalizedName());
		GameRegistry.registerItem(lensExplosive, lensExplosive.getUnlocalizedName());
		GameRegistry.registerItem(fireProjectile, fireProjectile.getUnlocalizedName());
		GameRegistry.registerItem(windProjectile, windProjectile.getUnlocalizedName());

		GameRegistry.registerItem(dCatalyst, dCatalyst.getUnlocalizedName());
		GameRegistry.registerItem(hyperLens, hyperLens.getUnlocalizedName());
		GameRegistry.registerItem(cataliticLens, cataliticLens.getUnlocalizedName());

		GameRegistry.registerItem(tome, tome.getUnlocalizedName());
		GameRegistry.registerItem(transmutationTablet, transmutationTablet.getUnlocalizedName());
		GameRegistry.registerItem(manual, manual.getUnlocalizedName());

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
		EntityRegistry.registerModEntity(EntityLootBall.class, "LootBall", 3, PECore.instance, 64, 10, true);
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
		ItemStack diamondReplacement = new ItemStack(Items.diamond);
		ItemStack diamondBlockReplacement = new ItemStack(Blocks.diamond_block);

		if (ProjectEConfig.altCraftingMat)
		{
			diamondReplacement = new ItemStack(Items.nether_star);
			diamondBlockReplacement = new ItemStack(Items.nether_star);
		}

		//Shaped Recipes
		//Philos Stone
		GameRegistry.addRecipe(new ItemStack(philosStone), "RGR", "GDG", "RGR", 'R', Items.redstone, 'G', Items.glowstone_dust, 'D', diamondReplacement);

		GameRegistry.addRecipe(new ItemStack(philosStone), "GRG", "RDR", "GRG", 'R', Items.redstone, 'G', Items.glowstone_dust, 'D', diamondReplacement);

		//Interdiction torch
		GameRegistry.addRecipe(new ItemStack(confuseTorch, 2), "RDR", "DPD", "GGG", 'R', Blocks.redstone_torch, 'G', Items.glowstone_dust, 'D', Items.diamond, 'P', philosStone);

		//Repair Talisman
		GameRegistry.addRecipe(new ItemStack(repairTalisman), "LMH", "SPS", "HML", 'P', Items.paper, 'S', Items.string, 'L', new ItemStack(covalence, 1, 0), 'M', new ItemStack(covalence, 1, 1), 'H', new ItemStack(covalence, 1, 2));

		//Klein Star Ein
		GameRegistry.addRecipe(new ItemStack(kleinStars, 1, 0), "MMM", "MDM", "MMM", 'M', new ItemStack(fuels, 1, 1), 'D', Items.diamond);

		//Matter
		GameRegistry.addRecipe(new ItemStack(matter, 1, 0), "AAA", "ADA", "AAA", 'D', Blocks.diamond_block, 'A', new ItemStack(fuels, 1, 2));
		GameRegistry.addRecipe(new ItemStack(matter, 1, 1), "AAA", "DDD", "AAA", 'D', matter, 'A', new ItemStack(fuels, 1, 2));
		GameRegistry.addRecipe(new ItemStack(matter, 1, 1), "ADA", "ADA", "ADA", 'D', matter, 'A', new ItemStack(fuels, 1, 2));

		//Alchemical Chest
		GameRegistry.addRecipe(new ItemStack(alchChest), "LMH", "SDS", "ICI", 'D', diamondReplacement, 'L', new ItemStack(covalence, 1, 0), 'M', new ItemStack(covalence, 1, 1), 'H', new ItemStack(covalence, 1, 2), 'S', Blocks.stone, 'I', Items.iron_ingot, 'C', Blocks.chest);

		//Alchemical Bags
		for (int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new ItemStack(alchBag, 1, i), "CCC", "WAW", "WWW", 'C', new ItemStack(covalence, 1, 2), 'A', alchChest, 'W', new ItemStack(Blocks.wool, 1, i));
		}

		//Condenser
		GameRegistry.addRecipe(new ItemStack(condenser), "ODO", "DCD", "ODO", 'D', Items.diamond, 'O', new ItemStack(Blocks.obsidian), 'C', new ItemStack(alchChest));

		//Condenser MK2
		GameRegistry.addRecipe(new ItemStack(condenserMk2), "RDR", "DCD", "RDR", 'D', new ItemStack(matterBlock, 1, 0), 'R', new ItemStack(matterBlock, 1, 1), 'C', condenser);

		//Transmutation Table
		GameRegistry.addRecipe(new ItemStack(transmuteStone), "OSO", "SPS", "OSO", 'S', Blocks.stone, 'O', Blocks.obsidian, 'P', philosStone);

		//Matter Blocks
		GameRegistry.addRecipe(new ItemStack(matterBlock, 1, 0), "DD", "DD", 'D', matter);
		GameRegistry.addRecipe(new ItemStack(matterBlock, 1, 1), "DD", "DD", 'D', new ItemStack(matter, 1, 1));

		//Matter Furnaces
		GameRegistry.addRecipe(new ItemStack(dmFurnaceOff), "DDD", "DFD", "DDD", 'D', new ItemStack(matterBlock, 1, 0), 'F', Blocks.furnace);
		GameRegistry.addRecipe(new ItemStack(rmFurnaceOff), "XRX", "RFR", 'R', new ItemStack(matterBlock, 1, 1), 'F', dmFurnaceOff);

		// DM Pedestal
		GameRegistry.addRecipe(new ItemStack(dmPedestal), "RDR", "RDR", "DDD", 'R', new ItemStack(matter, 1, 1), 'D', new ItemStack(matterBlock, 1, 0));

		//Collectors
		GameRegistry.addRecipe(new ItemStack(energyCollector), "GTG", "GDG", "GFG", 'G', Blocks.glowstone, 'F', Blocks.furnace, 'D', diamondBlockReplacement, 'T', Blocks.glass);
		GameRegistry.addRecipe(new ItemStack(collectorMK2), "GDG", "GCG", "GGG", 'G', Blocks.glowstone, 'C', energyCollector, 'D', matter);
		GameRegistry.addRecipe(new ItemStack(collectorMK3), "GRG", "GCG", "GGG", 'G', Blocks.glowstone, 'C', collectorMK2, 'R', new ItemStack(matter, 1, 1));

		//AM Relays
		GameRegistry.addRecipe(new ItemStack(relay), "OSO", "ODO", "OOO", 'S', Blocks.glass, 'D', Blocks.diamond_block, 'O', Blocks.obsidian);
		GameRegistry.addRecipe(new ItemStack(relayMK2), "ODO", "OAO", "OOO", 'A', relay, 'D', matter, 'O', Blocks.obsidian);
		GameRegistry.addRecipe(new ItemStack(relayMK3), "ORO", "OAO", "OOO", 'A', relayMK2, 'R', new ItemStack(matter, 1, 1), 'O', Blocks.obsidian);

		//DM Tools
		GameRegistry.addRecipe(new ItemStack(dmPick), "MMM", "XDX", "XDX", 'D', Items.diamond, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(dmAxe), "MMX", "MDX", "XDX", 'D', Items.diamond, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(dmShovel), "XMX", "XDX", "XDX", 'D', Items.diamond, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(dmSword), "XMX", "XMX", "XDX", 'D', Items.diamond, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(dmHoe), "MMX", "XDX", "XDX", 'D', Items.diamond, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(dmShears), "XM", "DX", 'D', Items.diamond, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(dmHammer), "MDM", "XDX", "XDX", 'D', Items.diamond, 'M', matter);

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
		GameRegistry.addRecipe(new ItemStack(ironBand), "III", "ILI", "III", 'I', Items.iron_ingot, 'L', Items.lava_bucket);
		GameRegistry.addRecipe(new ItemStack(ironBand), "III", "ILI", "III", 'I', Items.iron_ingot, 'L', volcanite);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(harvestGod), "SFS", "DID", "SFS", 'I', ironBand, 'S', "treeSapling", 'F', Blocks.red_flower, 'F', Blocks.red_flower, 'D', matter));
		GameRegistry.addRecipe(new ItemStack(swrg), "DFD", "FIF", "DFD", 'I', ironBand, 'F', Items.feather, 'D', matter);
		GameRegistry.addRecipe(new ItemStack(ignition), "FMF", "DID", "FMF", 'I', ironBand, 'F', new ItemStack(Items.flint_and_steel, 1, OreDictionary.WILDCARD_VALUE), 'D', matter, 'M', new ItemStack(fuels, 1, 1));
		GameRegistry.addRecipe(new ItemStack(bodyStone), "SSS", "RLR", "SSS", 'R', new ItemStack(matter, 1, 1), 'S', Items.sugar, 'L', new ItemStack(Items.dye, 1, 4));
		GameRegistry.addRecipe(new ItemStack(soulStone), "GGG", "RLR", "GGG", 'R', new ItemStack(matter, 1, 1), 'G', Items.glowstone_dust, 'L', new ItemStack(Items.dye, 1, 4));
		GameRegistry.addRecipe(new ItemStack(mindStone), "BBB", "RLR", "BBB", 'R', new ItemStack(matter, 1, 1), 'B', Items.book, 'L', new ItemStack(Items.dye, 1, 4));
		GameRegistry.addRecipe(new ItemStack(blackHole), "SSS", "DID", "SSS", 'I', ironBand, 'S', Items.string, 'D', matter);
		GameRegistry.addRecipe(new ItemStack(everTide), "WWW", "DDD", "WWW", 'W', Items.water_bucket, 'D', matter);
		GameRegistry.addRecipe(new ItemStack(volcanite), "LLL", "DDD", "LLL", 'L', Items.lava_bucket, 'D', matter);
		GameRegistry.addRecipe(new ItemStack(eternalDensity), "DOD", "MDM", "DOD", 'D', Items.diamond, 'O', Blocks.obsidian, 'M', matter);
		GameRegistry.addRecipe(new ItemStack(zero), "SBS", "MIM", "SBS", 'S', Blocks.snow, 'B', Items.snowball, 'M', matter, 'I', ironBand);
		GameRegistry.addShapelessRecipe(new ItemStack(voidRing), blackHole, eternalDensity, new ItemStack(matter, 1, 1), new ItemStack(matter, 1, 1));
		GameRegistry.addRecipe(new ItemStack(arcana), "ZIH", "SMM", "MMM", 'Z', zero, 'I', ignition, 'H', harvestGod, 'S', swrg, 'M', new ItemStack(matter, 1, 1));
		GameRegistry.addRecipe(new ItemStack(angelSmite), "BFB", "MIM", "BFB", 'B', Items.bow, 'F', Items.feather, 'M', matter, 'I', ironBand);

		//Watch of flowing time
		GameRegistry.addRecipe(new ItemStack(timeWatch), "DOD", "GCG", "DOD", 'D', matter, 'O', Blocks.obsidian, 'G', Blocks.glowstone, 'C', Items.clock);
		GameRegistry.addRecipe(new ItemStack(timeWatch), "DGD", "OCO", "DGD", 'D', matter, 'O', Blocks.obsidian, 'G', Blocks.glowstone, 'C', Items.clock);

		//Divining rods
		GameRegistry.addRecipe(new ItemStack(dRod1), "DDD", "DSD", "DDD", 'D', covalence, 'S', Items.stick);
		GameRegistry.addRecipe(new ItemStack(dRod2), "DDD", "DSD", "DDD", 'D', new ItemStack(covalence, 1, 1), 'S', dRod1);
		GameRegistry.addRecipe(new ItemStack(dRod3), "DDD", "DSD", "DDD", 'D', new ItemStack(covalence, 1, 2), 'S', dRod2);

		//Explosive items
		GameRegistry.addRecipe(new ItemStack(dCatalyst), "NMN", "MFM", "NMN", 'N', novaCatalyst, 'M', new ItemStack(fuels, 1, 1), 'F', new ItemStack(Items.flint_and_steel, 1, OreDictionary.WILDCARD_VALUE));
		GameRegistry.addRecipe(new ItemStack(hyperLens), "DDD", "MNM", "DDD", 'N', novaCatalyst, 'M', matter, 'D', Items.diamond);
		GameRegistry.addRecipe(new ItemStack(cataliticLens), "MMM", "HMD", "MMM", 'M', matter, 'H', hyperLens, 'D', dCatalyst);
		GameRegistry.addRecipe(new ItemStack(cataliticLens), "MMM", "DMH", "MMM", 'M', matter, 'H', hyperLens, 'D', dCatalyst);

		//Fuel Blocks
		GameRegistry.addRecipe(new ItemStack(fuelBlock, 1, 0), "FFF", "FFF", "FFF", 'F', fuels);
		GameRegistry.addRecipe(new ItemStack(fuelBlock, 1, 1), "FFF", "FFF", "FFF", 'F', new ItemStack(fuels, 1, 1));
		GameRegistry.addRecipe(new ItemStack(fuelBlock, 1, 2), "FFF", "FFF", "FFF", 'F', new ItemStack(fuels, 1, 2));

		//Tome
		if (ProjectEConfig.craftableTome)
		{
			GameRegistry.addRecipe(new ItemStack(tome), "HML", "KBK", "LMH", 'L', new ItemStack(covalence, 1, 0), 'M', new ItemStack(covalence, 1, 1), 'H', new ItemStack(covalence, 1, 2), 'B', Items.book, 'K', new ItemStack(kleinStars, 1, 5));
		}

		//Manual
		//GameRegistry.addShapelessRecipe(new ItemStack(manual, 1, 0), Items.book, new ItemStack(covalence, 1, 0));
		//GameRegistry.addShapelessRecipe(new ItemStack(manual, 1, 0), Items.book, new ItemStack(covalence, 1, 1));
		//GameRegistry.addShapelessRecipe(new ItemStack(manual, 1, 0), Items.book, new ItemStack(covalence, 1, 2));

		//TransmutationTablet
		GameRegistry.addRecipe(new ItemStack(transmutationTablet), "DSD", "STS", "DSD", 'D', new ItemStack(matterBlock, 1, 0), 'S', Blocks.stone, 'T', transmuteStone);

		//Mercurial Eye
		GameRegistry.addRecipe(new ItemStack(mercEye), "OBO", "BRB", "BDB", 'O', Blocks.obsidian, 'B', Blocks.brick_block, 'R', new ItemStack(matter, 1, 1), 'D', Items.diamond);

		//Shapeless Recipes
		//Philos Stone exchanges
		GameRegistry.addShapelessRecipe(new ItemStack(Items.ender_pearl), philosStone, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.iron_ingot, 8), philosStone, Items.gold_ingot);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.gold_ingot), philosStone, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond), philosStone, Items.gold_ingot, Items.gold_ingot, Items.gold_ingot, Items.gold_ingot);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.gold_ingot, 4), philosStone, Items.diamond);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.emerald), philosStone, Items.diamond, Items.diamond);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond, 2), philosStone, Items.emerald);
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 1, 0), philosStone, Items.coal, Items.coal, Items.coal, Items.coal);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.coal, 4), philosStone, new ItemStack(fuels, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 1, 1), philosStone, new ItemStack(fuels, 1, 0), new ItemStack(fuels, 1, 0), new ItemStack(fuels, 1, 0), new ItemStack(fuels, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 4, 0), philosStone, new ItemStack(fuels, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 1, 2), philosStone, new ItemStack(fuels, 1, 1), new ItemStack(fuels, 1, 1), new ItemStack(fuels, 1, 1), new ItemStack(fuels, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 4, 1), philosStone, new ItemStack(fuels, 1, 2));

		//Covalence dust
		GameRegistry.addShapelessRecipe(new ItemStack(covalence, 40, 0), Blocks.cobblestone, Blocks.cobblestone, Blocks.cobblestone, Blocks.cobblestone, Blocks.cobblestone, Blocks.cobblestone, Blocks.cobblestone, Blocks.cobblestone, new ItemStack(Items.coal, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(covalence, 40, 1), Items.iron_ingot, Items.redstone);
		GameRegistry.addShapelessRecipe(new ItemStack(covalence, 40, 2), Items.diamond, Items.coal);

		//Klein Stars
		for (int i = 1; i < 6; i++)
		{
			ItemStack input = new ItemStack(kleinStars, 1, i - 1);
			ItemStack output = new ItemStack(kleinStars, 1, i);
			GameRegistry.addRecipe(new RecipeShapelessHidden(output, input, input, input, input));
		}

		//Other items
		GameRegistry.addShapelessRecipe(new ItemStack(novaCatalyst, 2), Blocks.tnt, new ItemStack(fuels, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(novaCataclysm, 2), novaCatalyst, new ItemStack(fuels, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(lifeStone), bodyStone, soulStone);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.ice), new ItemStack(zero, 1, OreDictionary.WILDCARD_VALUE), Items.water_bucket);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.lava_bucket), volcanite, Items.bucket, Items.redstone);

		GameRegistry.addShapelessRecipe(new ItemStack(gemHelmet), rmHelmet, new ItemStack(kleinStars, 1, 5), everTide, soulStone);
		GameRegistry.addShapelessRecipe(new ItemStack(gemChest), rmChest, new ItemStack(kleinStars, 1, 5), volcanite, bodyStone);
		GameRegistry.addShapelessRecipe(new ItemStack(gemLegs), rmLegs, new ItemStack(kleinStars, 1, 5), blackHole, timeWatch);
		GameRegistry.addShapelessRecipe(new ItemStack(gemFeet), rmFeet, new ItemStack(kleinStars, 1, 5), swrg, swrg);

		GameRegistry.addShapelessRecipe(new ItemStack(matter, 4, 0), matterBlock);
		GameRegistry.addShapelessRecipe(new ItemStack(matter, 4, 1), new ItemStack(matterBlock, 1, 1));

		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 9, 0), new ItemStack(fuelBlock, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 9, 1), new ItemStack(fuelBlock, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(fuels, 9, 2), new ItemStack(fuelBlock, 1, 2));

		// need a recipe for each arcana mode, there's probably a better way to do this
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.ice), new ItemStack(arcana, 1, 0), Items.water_bucket);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.ice), new ItemStack(arcana, 1, 1), Items.water_bucket);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.ice), new ItemStack(arcana, 1, 2), Items.water_bucket);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.ice), new ItemStack(arcana, 1, 3), Items.water_bucket);

		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.grass), new ItemStack(arcana, 1, 0), Blocks.dirt);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.grass), new ItemStack(arcana, 1, 1), Blocks.dirt);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.grass), new ItemStack(arcana, 1, 2), Blocks.dirt);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.grass), new ItemStack(arcana, 1, 3), Blocks.dirt);

		//Custom Recipe managment
		for(int i = 1; i <= 15; i++){
			GameRegistry.addRecipe(new RecipeAlchemyBag(new ItemStack(alchBag, 1, 15-i), new ItemStack(alchBag, 1, 0), new ItemStack(Items.dye, 1, i)));
			GameRegistry.addRecipe(new RecipeAlchemyBag(new ItemStack(alchBag, 1, 0), new ItemStack(alchBag, 1, i), new ItemStack(Items.dye, 1, 15)));
		}
		GameRegistry.addRecipe(new RecipesCovalenceRepair());
		RecipeSorter.register("Alchemical Bags Recipes", RecipeAlchemyBag.class, Category.SHAPELESS, "before:minecraft:shaped");
		RecipeSorter.register("Covalence Repair Recipes", RecipesCovalenceRepair.class, Category.SHAPELESS, "before:minecraft:shaped");
		RecipeSorter.register("", RecipeShapedKleinStar.class, Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
		RecipeSorter.register("", RecipeShapelessHidden.class, Category.SHAPELESS, "before:minecraft:shaped");

		//Fuel Values
		GameRegistry.registerFuelHandler(new FuelHandler());
	}

	/**
	 * Philosopher's stone smelting recipes, EE3 style
	 */
	public static void registerPhiloStoneSmelting()
	{

		for (Entry<ItemStack, ItemStack> entry : (((HashMap<ItemStack, ItemStack>) FurnaceRecipes.smelting().getSmeltingList()).entrySet()))
		{
			if (entry.getKey() == null || entry.getValue() == null)
			{
				continue;
			}

			ItemStack input = entry.getKey();
			ItemStack output = entry.getValue().copy();
			output.stackSize *= 7;

			GameRegistry.addRecipe(new RecipeShapelessHidden(output, philosStone, input, input, input, input, input, input, input, new ItemStack(Items.coal, 1, OreDictionary.WILDCARD_VALUE)));

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
						return Constants.AETERNALIS_BUR_TIME;
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
						return Constants.AETERNALIS_BUR_TIME * 9;
				}
			}

			return 0;
		}
	}
}
