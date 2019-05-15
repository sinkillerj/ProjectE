package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.blocks.*;
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
import moze_intel.projecte.gameObjs.items.DestructionCatalyst;
import moze_intel.projecte.gameObjs.items.DiviningRod;
import moze_intel.projecte.gameObjs.items.EvertideAmulet;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.gameObjs.items.HyperkineticLens;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.items.MercurialEye;
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
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemFuelBlock;
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
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Map.Entry;

@Mod.EventBusSubscriber(modid = PECore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ObjHandler
{
	private static final ItemGroup cTab = new ItemGroup(PECore.MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(philosStone);
		}
	};
	public static final IRecipeSerializer<RecipesCovalenceRepair> COVALENCE_REPAIR_RECIPE_SERIALIZER = RecipeSerializers.register(new RecipeSerializers.SimpleSerializer<>(PECore.MODID + ":covalence_repair", RecipesCovalenceRepair::new));
	public static final IRecipeSerializer<RecipeShapelessKleinStar> KLEIN_RECIPE_SERIALIZER = RecipeSerializers.register(new RecipeShapelessKleinStar.Serializer());
	public static final IRecipeSerializer<RecipeShapelessHidden> SHAPELESS_HIDDEN_SERIALIZER = RecipeSerializers.register(new RecipeShapelessHidden.Serializer());
	public static final Block alchChest = new AlchemicalChest(Block.Properties.create(Material.ROCK).hardnessAndResistance(10, 6000000)).setRegistryName(PECore.MODID, "alchemical_chest");
	public static final Block interdictionTorch = new InterdictionTorch(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0).lightValue(14).tickRandomly()).setRegistryName(PECore.MODID, "interdiction_torch");
	public static final Block interdictionTorchWall = new InterdictionTorchWall(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0).lightValue(14).tickRandomly()).setRegistryName(PECore.MODID, "wall_interdiction_torch");
	public static final Block transmuteStone = new TransmutationStone(Block.Properties.create(Material.ROCK).hardnessAndResistance(10)).setRegistryName(PECore.MODID, "transmutation_table");
	public static final Block condenser = new Condenser(Block.Properties.create(Material.ROCK).hardnessAndResistance(10, 6000000)).setRegistryName(PECore.MODID, "condenser_mk1");
	public static final Block condenserMk2 = new CondenserMK2(Block.Properties.create(Material.ROCK).hardnessAndResistance(10, 6000000)).setRegistryName(PECore.MODID, "condenser_mk2");
	public static final Block rmFurnaceOff = new MatterFurnace(Block.Properties.create(Material.ROCK).hardnessAndResistance(1000000F).lightValue(14), EnumMatterType.DARK_MATTER).setRegistryName(PECore.MODID, "dm_furnace");
	public static final Block dmFurnaceOff = new MatterFurnace(Block.Properties.create(Material.ROCK).hardnessAndResistance(2000000F).lightValue(14), EnumMatterType.RED_MATTER).setRegistryName(PECore.MODID, "rm_furnace");
	public static final Block dmPedestal = new Pedestal(Block.Properties.create(Material.ROCK).hardnessAndResistance(1).lightValue(12)).setRegistryName(PECore.MODID, "dm_pedestal");
	public static final Block dmBlock = new MatterBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(1000000), EnumMatterType.DARK_MATTER).setRegistryName(PECore.MODID, "dark_matter_block");
	public static final Block rmBlock = new MatterBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(1000000), EnumMatterType.RED_MATTER).setRegistryName(PECore.MODID, "red_matter_block");
	public static final Block alchemicalCoalBlock = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.5F)).setRegistryName(PECore.MODID, "alchemical_coal_block");
	public static final Block mobiusFuelBlock = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.5F)).setRegistryName(PECore.MODID, "mobius_fuel_block");
	public static final Block aeternalisFuelBlock = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.5F)).setRegistryName(PECore.MODID, "aeternalis_fuel_block");
	public static final Block collectorMK1 = new Collector(1, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).lightValue(7)).setRegistryName(PECore.MODID, "collector_mk1");
	public static final Block collectorMK2 = new Collector(2, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).lightValue(11)).setRegistryName(PECore.MODID, "collector_mk2");
	public static final Block collectorMK3 = new Collector(3, Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).lightValue(15)).setRegistryName(PECore.MODID, "collector_mk3");
	public static final Block relay = new Relay(1, Block.Properties.create(Material.ROCK).hardnessAndResistance(10).lightValue(7)).setRegistryName(PECore.MODID, "relay_mk1");
	public static final Block relayMK2 = new Relay(2, Block.Properties.create(Material.ROCK).hardnessAndResistance(10).lightValue(11)).setRegistryName(PECore.MODID, "relay_mk2");
	public static final Block relayMK3 = new Relay(3, Block.Properties.create(Material.ROCK).hardnessAndResistance(10).lightValue(15)).setRegistryName(PECore.MODID, "relay_mk3");
	public static final Block novaCatalyst = new NovaCatalyst(Block.Properties.create(Material.TNT).hardnessAndResistance(0)).setRegistryName(PECore.MODID, "nova_catalyst");
	public static final Block novaCataclysm = new NovaCataclysm(Block.Properties.create(Material.TNT).hardnessAndResistance(0)).setRegistryName(PECore.MODID, "nova_cataclysm");

	public static final Item philosStone = new PhilosophersStone(ibNoStack()).setRegistryName(PECore.MODID, "philosophers_stone");
	public static final Item alchBagWhite = new AlchemicalBag(ibNoStack(), EnumDyeColor.WHITE).setRegistryName(PECore.MODID, "white_alchemical_bag");
	public static final Item alchBagOrange = new AlchemicalBag(ibNoStack(), EnumDyeColor.ORANGE).setRegistryName(PECore.MODID, "orange_alchemical_bag");
	public static final Item alchBagMagenta = new AlchemicalBag(ibNoStack(), EnumDyeColor.MAGENTA).setRegistryName(PECore.MODID, "magenta_alchemical_bag");
	public static final Item alchBagLightBlue = new AlchemicalBag(ibNoStack(), EnumDyeColor.LIGHT_BLUE).setRegistryName(PECore.MODID, "light_blue_alchemical_bag");
	public static final Item alchBagYellow = new AlchemicalBag(ibNoStack(), EnumDyeColor.YELLOW).setRegistryName(PECore.MODID, "yellow_alchemical_bag");
	public static final Item alchBagLime = new AlchemicalBag(ibNoStack(), EnumDyeColor.LIME).setRegistryName(PECore.MODID, "lime_alchemical_bag");
	public static final Item alchBagPink = new AlchemicalBag(ibNoStack(), EnumDyeColor.PINK).setRegistryName(PECore.MODID, "pink_alchemical_bag");
	public static final Item alchBagGray = new AlchemicalBag(ibNoStack(), EnumDyeColor.GRAY).setRegistryName(PECore.MODID, "gray_alchemical_bag");
	public static final Item alchBagLightGray = new AlchemicalBag(ibNoStack(), EnumDyeColor.LIGHT_GRAY).setRegistryName(PECore.MODID, "light_gray_alchemical_bag");
	public static final Item alchBagCyan = new AlchemicalBag(ibNoStack(), EnumDyeColor.CYAN).setRegistryName(PECore.MODID, "cyan_alchemical_bag");
	public static final Item alchBagPurple = new AlchemicalBag(ibNoStack(), EnumDyeColor.PURPLE).setRegistryName(PECore.MODID, "purple_alchemical_bag");
	public static final Item alchBagBlue = new AlchemicalBag(ibNoStack(), EnumDyeColor.BLUE).setRegistryName(PECore.MODID, "blue_alchemical_bag");
	public static final Item alchBagBrown = new AlchemicalBag(ibNoStack(), EnumDyeColor.BROWN).setRegistryName(PECore.MODID, "brown_alchemical_bag");
	public static final Item alchBagGreen = new AlchemicalBag(ibNoStack(), EnumDyeColor.GREEN).setRegistryName(PECore.MODID, "green_alchemical_bag");
	public static final Item alchBagRed = new AlchemicalBag(ibNoStack(), EnumDyeColor.RED).setRegistryName(PECore.MODID, "red_alchemical_bag");
	public static final Item alchBagBlack = new AlchemicalBag(ibNoStack(), EnumDyeColor.BLACK).setRegistryName(PECore.MODID, "black_alchemical_bag");
	public static final Item repairTalisman = new RepairTalisman(ibNoStack()).setRegistryName(PECore.MODID, "repair_talisman");
	public static final Item kleinStarEin = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.EIN).setRegistryName(PECore.MODID, "klein_star_ein");
	public static final Item kleinStarZwei = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.ZWEI).setRegistryName(PECore.MODID, "klein_star_zwei");
	public static final Item kleinStarDrei = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.DREI).setRegistryName(PECore.MODID, "klein_star_drei");
	public static final Item kleinStarVier = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.VIER).setRegistryName(PECore.MODID, "klein_star_vier");
	public static final Item kleinStarSphere = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.SPHERE).setRegistryName(PECore.MODID, "klein_star_sphere");
	public static final Item kleinStarOmega = new KleinStar(ibNoStack().rarity(EnumRarity.EPIC), KleinStar.EnumKleinTier.OMEGA).setRegistryName(PECore.MODID, "klein_star_omega");
	public static final Item alchemicalCoal = new AlchemicalFuel(ib(), EnumFuelType.ALCHEMICAL_COAL).setRegistryName(PECore.MODID, "alchemical_coal");
	public static final Item mobiusFuel = new AlchemicalFuel(ib(), EnumFuelType.MOBIUS_FUEL).setRegistryName(PECore.MODID, "mobius_fuel");
	public static final Item aeternalisFuel = new AlchemicalFuel(ib().rarity(EnumRarity.RARE), EnumFuelType.AETERNALIS_FUEL).setRegistryName(PECore.MODID, "aeternalis_fuel");
	public static final Item covalenceDustLow = new Item(ib()).setRegistryName(PECore.MODID, "low_covalence_dust");
	public static final Item covalenceDustMedium = new Item(ib()).setRegistryName(PECore.MODID, "medium_covalence_dust");
	public static final Item covalenceDustHigh = new Item(ib()).setRegistryName(PECore.MODID, "high_covalence_dust");
	public static final Item darkMatter = new Item(ib()).setRegistryName(PECore.MODID, "dark_matter");
	public static final Item redMatter = new Item(ib()).setRegistryName(PECore.MODID, "red_matter");

	public static final Item dmPick = new DarkPick(ibNoStack().addToolType(ToolType.PICKAXE, 4)).setRegistryName(PECore.MODID, "dm_pick");
	public static final Item dmAxe = new DarkAxe(ibNoStack().addToolType(ToolType.AXE, 4)).setRegistryName(PECore.MODID, "dm_axe");
	public static final Item dmShovel = new DarkShovel(ibNoStack().addToolType(ToolType.SHOVEL, 4)).setRegistryName(PECore.MODID, "dm_shovel");
	public static final Item dmSword = new DarkSword(ibNoStack()).setRegistryName(PECore.MODID, "dm_sword");
	public static final Item dmHoe = new DarkHoe(ibNoStack().addToolType(ToolType.get("hoe"), 4)).setRegistryName(PECore.MODID, "dm_hoe");
	public static final Item dmShears = new DarkShears(ibNoStack().addToolType(ToolType.get("shears"), 4)).setRegistryName(PECore.MODID, "dm_shears");
	public static final Item dmHammer = new DarkHammer(ibNoStack()
			.addToolType(ToolType.PICKAXE, 4)
			.addToolType(ToolType.get("hammer"), 4)
			.addToolType(ToolType.get("chisel"), 4))
			.setRegistryName(PECore.MODID, "dm_hammer");

	public static final Item rmPick = new RedPick(ibNoStack().addToolType(ToolType.PICKAXE, 5)).setRegistryName(PECore.MODID, "rm_pick");
	public static final Item rmAxe = new RedAxe(ibNoStack().addToolType(ToolType.AXE, 5)).setRegistryName(PECore.MODID, "rm_axe");
	public static final Item rmShovel = new RedShovel(ibNoStack().addToolType(ToolType.SHOVEL, 5)).setRegistryName(PECore.MODID, "rm_shovel");
	public static final Item rmSword = new RedSword(ibNoStack()).setRegistryName(PECore.MODID, "rm_sword");
	public static final Item rmHoe = new RedHoe(ibNoStack().addToolType(ToolType.get("hoe"), 5)).setRegistryName(PECore.MODID, "rm_hoe");
	public static final Item rmShears = new RedShears(ibNoStack().addToolType(ToolType.get("shears"), 5)).setRegistryName(PECore.MODID, "rm_shears");
	public static final Item rmHammer = new RedHammer(ibNoStack()
			.addToolType(ToolType.PICKAXE, 5)
			.addToolType(ToolType.get("hammer"), 5)
			.addToolType(ToolType.get("chisel"), 5))
			.setRegistryName(PECore.MODID, "rm_hammer");
	public static final Item rmKatar = new RedKatar(ibNoStack()
			.addToolType(ToolType.AXE, 5)
			.addToolType(ToolType.get("katar"), 5)
			.addToolType(ToolType.get("shears"), 5))
			.setRegistryName(PECore.MODID, "rm_katar");
	public static final Item rmStar = new RedStar(ibNoStack()
			.addToolType(ToolType.get("morning_star"), 5)
			.addToolType(ToolType.PICKAXE, 5)
			.addToolType(ToolType.SHOVEL, 5)
			.addToolType(ToolType.AXE, 5))
			.setRegistryName(PECore.MODID, "rm_morning_star");

	public static final Item dmHelmet = new DMArmor(EntityEquipmentSlot.HEAD, ibNoStack()).setRegistryName(PECore.MODID, "dm_helmet");
	public static final Item dmChest = new DMArmor(EntityEquipmentSlot.CHEST, ibNoStack()).setRegistryName(PECore.MODID, "dm_chestplate");
	public static final Item dmLegs = new DMArmor(EntityEquipmentSlot.LEGS, ibNoStack()).setRegistryName(PECore.MODID, "dm_leggings");
	public static final Item dmFeet = new DMArmor(EntityEquipmentSlot.FEET, ibNoStack()).setRegistryName(PECore.MODID, "dm_boots");

	public static final Item rmHelmet = new RMArmor(EntityEquipmentSlot.HEAD, ibNoStack()).setRegistryName(PECore.MODID, "rm_helmet");
	public static final Item rmChest = new RMArmor(EntityEquipmentSlot.CHEST, ibNoStack()).setRegistryName(PECore.MODID, "rm_chestplate");
	public static final Item rmLegs = new RMArmor(EntityEquipmentSlot.LEGS, ibNoStack()).setRegistryName(PECore.MODID, "rm_leggings");
	public static final Item rmFeet = new RMArmor(EntityEquipmentSlot.FEET, ibNoStack()).setRegistryName(PECore.MODID, "rm_boots");

	public static final Item gemHelmet = new GemHelmet(ibNoStack()).setRegistryName(PECore.MODID, "gem_helmet");
	public static final Item gemChest = new GemChest(ibNoStack()).setRegistryName(PECore.MODID, "gem_chestplate");
	public static final Item gemLegs = new GemLegs(ibNoStack()).setRegistryName(PECore.MODID, "gem_leggings");
	public static final Item gemFeet = new GemFeet(ibNoStack()).setRegistryName(PECore.MODID, "gem_boots");

	public static final Item ironBand = new Item(ib()).setRegistryName(PECore.MODID, "iron_band");
	public static final Item blackHole = new BlackHoleBand(ibNoStack()).setRegistryName(PECore.MODID, "black_hole_band");
	public static final Item angelSmite = new ArchangelSmite(ibNoStack()).setRegistryName(PECore.MODID, "archangel_smite");
	public static final Item harvestGod = new HarvestGoddess(ibNoStack()).setRegistryName(PECore.MODID, "harvest_goddess_band");
	public static final Item ignition = new Ignition(ibNoStack()).setRegistryName(PECore.MODID, "ignition_ring");
	public static final Item zero = new Zero(ibNoStack()).setRegistryName(PECore.MODID, "zero_ring");
	public static final Item swrg = new SWRG(ibNoStack()).setRegistryName(PECore.MODID, "swiftwolf_rending_gale");
	public static final Item timeWatch = new TimeWatch(ibNoStack()).setRegistryName(PECore.MODID, "watch_of_flowing_time");
	public static final Item everTide = new EvertideAmulet(ibNoStack()).setRegistryName(PECore.MODID, "evertide_amulet");
	public static final Item volcanite = new VolcaniteAmulet(ibNoStack()).setRegistryName(PECore.MODID, "volcanite_amulet");
	public static final Item eternalDensity = new GemEternalDensity(ibNoStack()).setRegistryName(PECore.MODID, "gem_of_eternal_density");
	public static final Item dRod1 = new DiviningRod(ibNoStack(), new String[] { "3x3x3" }).setRegistryName(PECore.MODID, "divining_rod_1");
	public static final Item dRod2 = new DiviningRod(ibNoStack(), new String[]{ "3x3x3", "16x3x3" }).setRegistryName(PECore.MODID, "divining_rod_2");
	public static final Item dRod3 = new DiviningRod(ibNoStack(), new String[] { "3x3x3", "16x3x3", "64x3x3" }).setRegistryName(PECore.MODID, "divining_rod_3");
	public static final Item mercEye = new MercurialEye(ibNoStack()).setRegistryName(PECore.MODID, "mercurial_eye");
	public static final Item voidRing = new VoidRing(ibNoStack()).setRegistryName(PECore.MODID, "void_ring");
	public static final Item arcana = new Arcana(ibNoStack().rarity(EnumRarity.RARE)).setRegistryName(PECore.MODID, "arcana_ring");

	public static final Item dCatalyst = new DestructionCatalyst(ibNoStack()).setRegistryName(PECore.MODID, "destruction_catalyst");
	public static final Item hyperLens = new HyperkineticLens(ibNoStack()).setRegistryName(PECore.MODID, "hyperkinetic_lens");
	public static final Item cataliticLens = new CataliticLens(ibNoStack()).setRegistryName(PECore.MODID, "catalytic_lens");

	public static final Item bodyStone = new BodyStone(ibNoStack()).setRegistryName(PECore.MODID, "body_stone");
	public static final Item soulStone = new SoulStone(ibNoStack()).setRegistryName(PECore.MODID, "soul_stone");
	public static final Item mindStone = new MindStone(ibNoStack()).setRegistryName(PECore.MODID, "mind_stone");
	public static final Item lifeStone = new LifeStone(ibNoStack()).setRegistryName(PECore.MODID, "life_stone");

	public static final Item tome = new Tome(ibNoStack().rarity(EnumRarity.EPIC)).setRegistryName(PECore.MODID, "tome");

	// TODO 1.13 get rid of these
	public static final Item waterOrb = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "water_orb");
	public static final Item lavaOrb = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "lava_orb");
	public static final Item mobRandomizer = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "randomizer");
	public static final Item lensExplosive = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "lens_explosive");
	public static final Item fireProjectile = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "fire_projectile");
	public static final Item windProjectile = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "wind_projectile");
	public static final Item transmutationTablet = new TransmutationTablet(ibNoStack()).setRegistryName(PECore.MODID, "transmutation_tablet");

	public static final EntityType<?> FIRE_PROJECTILE = EntityType.Builder
			.create(EntityFireProjectile.class, EntityFireProjectile::new).tracker(256, 10, false)
			.build("") .setRegistryName(PECore.MODID, "fire_projectile");
	public static final EntityType<?> HOMING_ARROW = EntityType.Builder
			.create(EntityHomingArrow.class, EntityHomingArrow::new) // use vanilla entitytracker settings
			.build("") .setRegistryName(PECore.MODID, "homing_arrow");
	public static final EntityType<?> LAVA_PROJECTILE = EntityType.Builder
			.create(EntityLavaProjectile.class, EntityLavaProjectile::new).tracker(256, 10, false)
			.build("") .setRegistryName(PECore.MODID, "lava_projectile");
	public static final EntityType<?> LENS_PROJECTILE = EntityType.Builder
			.create(EntityLensProjectile.class, EntityLensProjectile::new).tracker(256, 10, false)
			.build("") .setRegistryName(PECore.MODID, "lens_projectile");
	public static final EntityType<?> MOB_RANDOMIZER = EntityType.Builder
			.create(EntityMobRandomizer.class, EntityMobRandomizer::new).tracker(256, 10, false)
			.build("") .setRegistryName(PECore.MODID, "mob_randomizer");
	public static final EntityType<?> NOVA_CATALYST_PRIMED = EntityType.Builder
			.create(EntityNovaCatalystPrimed.class, EntityNovaCatalystPrimed::new) // use vanilla tracking
			.build("") .setRegistryName(PECore.MODID, "nova_catalyst_primed");
	public static final EntityType<?> NOVA_CATACLYSM_PRIMED = EntityType.Builder
			.create(EntityNovaCataclysmPrimed.class, EntityNovaCataclysmPrimed::new) // use vanilla tracking
			.build("") .setRegistryName(PECore.MODID, "nova_cataclysm_primed");
	public static final EntityType<?> SWRG_PROJECTILE = EntityType.Builder
			.create(EntitySWRGProjectile.class, EntitySWRGProjectile::new).tracker(256, 10, false)
			.build("") .setRegistryName(PECore.MODID, "swrg_projectile");
	public static final EntityType<?> WATER_PROJECTILE = EntityType.Builder
			.create(EntityWaterProjectile.class, EntityWaterProjectile::new).tracker(256, 10, false)
			.build("") .setRegistryName(PECore.MODID, "water_projectile");

	public static final TileEntityType<?> ALCH_CHEST_TILE = TileEntityType.Builder.create(AlchChestTile::new).build(null).setRegistryName(PECore.MODID, "alchemical_chest");
	public static final TileEntityType<?> COLLECTOR_MK1_TILE = TileEntityType.Builder.create(CollectorMK1Tile::new).build(null).setRegistryName(PECore.MODID, "collector_mk1");
	public static final TileEntityType<?> COLLECTOR_MK2_TILE = TileEntityType.Builder.create(CollectorMK2Tile::new).build(null).setRegistryName(PECore.MODID, "collector_mk2");
	public static final TileEntityType<?> COLLECTOR_MK3_TILE = TileEntityType.Builder.create(CollectorMK3Tile::new).build(null).setRegistryName(PECore.MODID, "collector_mk3");
	public static final TileEntityType<?> CONDENSER_TILE = TileEntityType.Builder.create(CondenserTile::new).build(null).setRegistryName(PECore.MODID, "condenser");
	public static final TileEntityType<?> CONDENSER_MK2_TILE = TileEntityType.Builder.create(CondenserMK2Tile::new).build(null).setRegistryName(PECore.MODID, "condenser_mk2");
	public static final TileEntityType<?> RELAY_MK1_TILE = TileEntityType.Builder.create(RelayMK1Tile::new).build(null).setRegistryName(PECore.MODID, "relay_mk1");
	public static final TileEntityType<?> RELAY_MK2_TILE = TileEntityType.Builder.create(RelayMK2Tile::new).build(null).setRegistryName(PECore.MODID, "relay_mk2");
	public static final TileEntityType<?> RELAY_MK3_TILE = TileEntityType.Builder.create(RelayMK3Tile::new).build(null).setRegistryName(PECore.MODID, "relay_mk3");
	public static final TileEntityType<?> DM_FURNACE_TILE = TileEntityType.Builder.create(DMFurnaceTile::new).build(null).setRegistryName(PECore.MODID, "dm_furnace");
	public static final TileEntityType<?> RM_FURNACE_TILE = TileEntityType.Builder.create(RMFurnaceTile::new).build(null).setRegistryName(PECore.MODID, "rm_furnace");
	public static final TileEntityType<?> INTERDICTION_TORCH_TILE = TileEntityType.Builder.create(InterdictionTile::new).build(null).setRegistryName(PECore.MODID, "interdiction_torch");
	public static final TileEntityType<?> DM_PEDESTAL_TILE = TileEntityType.Builder.create(DMPedestalTile::new).build(null).setRegistryName(PECore.MODID, "dm_pedestal");

	private static Item.Properties ib()
	{
		return new Item.Properties().group(cTab);
	}
	
	private static Item.Properties ibNoStack()
	{
		return ib().maxStackSize(1);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> evt)
	{
		IForgeRegistry<Block> r = evt.getRegistry();
		r.register(alchChest);
		r.register(collectorMK1);
		r.register(collectorMK2);
		r.register(collectorMK3);
		r.register(condenser);
		r.register(condenserMk2);
		r.register(dmFurnaceOff);
		r.register(dmPedestal);
		r.register(alchemicalCoalBlock);
		r.register(mobiusFuelBlock);
		r.register(aeternalisFuelBlock);
		r.register(interdictionTorch);
		r.register(interdictionTorchWall);
		r.register(dmBlock);
		r.register(rmBlock);
		r.register(novaCatalyst);
		r.register(novaCataclysm);
		r.register(relay);
		r.register(relayMK2);
		r.register(relayMK3);
		r.register(rmFurnaceOff);
		r.register(transmuteStone);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> evt)
	{
		IForgeRegistry<Item> r = evt.getRegistry();
		registerObj(r, new ItemBlock(alchChest, ib()), alchChest.getRegistryName());
		registerObj(r, new ItemBlock(collectorMK1, ib()), collectorMK1.getRegistryName());
		registerObj(r, new ItemBlock(collectorMK2, ib()), collectorMK2.getRegistryName());
		registerObj(r, new ItemBlock(collectorMK3, ib()), collectorMK3.getRegistryName());
		registerObj(r, new ItemBlock(condenser, ib()), condenser.getRegistryName());
		registerObj(r, new ItemBlock(condenserMk2, ib()), condenserMk2.getRegistryName());
		registerObj(r, new ItemBlock(dmFurnaceOff, ib()), dmFurnaceOff.getRegistryName());
		registerObj(r, new ItemBlock(dmPedestal, ib()), dmPedestal.getRegistryName());
		registerObj(r, new ItemFuelBlock(alchemicalCoalBlock, ib(), EnumFuelType.ALCHEMICAL_COAL), alchemicalCoalBlock.getRegistryName());
		registerObj(r, new ItemFuelBlock(mobiusFuelBlock, ib(), EnumFuelType.MOBIUS_FUEL), mobiusFuelBlock.getRegistryName());
		registerObj(r, new ItemFuelBlock(aeternalisFuelBlock, ib(), EnumFuelType.AETERNALIS_FUEL), aeternalisFuelBlock.getRegistryName());
		registerObj(r, new ItemWallOrFloor(interdictionTorch, interdictionTorchWall, ib()), interdictionTorch.getRegistryName());
		registerObj(r, new ItemBlock(dmBlock, ib()), dmBlock.getRegistryName());
		registerObj(r, new ItemBlock(rmBlock, ib()), rmBlock.getRegistryName());
		registerObj(r, new ItemBlock(novaCatalyst, ib()), novaCatalyst.getRegistryName());
		registerObj(r, new ItemBlock(novaCataclysm, ib()), novaCataclysm.getRegistryName());
		registerObj(r, new ItemBlock(relay, ib()), relay.getRegistryName());
		registerObj(r, new ItemBlock(relayMK2, ib()), relayMK2.getRegistryName());
		registerObj(r, new ItemBlock(relayMK3, ib()), relayMK3.getRegistryName());
		registerObj(r, new ItemBlock(rmFurnaceOff, ib()), rmFurnaceOff.getRegistryName());
		registerObj(r, new ItemBlock(transmuteStone, ib()), transmuteStone.getRegistryName());

		r.register(philosStone);
		r.register(alchBagWhite); r.register(alchBagOrange); r.register(alchBagMagenta); r.register(alchBagLightBlue);
		r.register(alchBagYellow); r.register(alchBagLime); r.register(alchBagPink); r.register(alchBagGray);
		r.register(alchBagLightGray); r.register(alchBagCyan); r.register(alchBagPurple); r.register(alchBagBlue);
		r.register(alchBagBrown); r.register(alchBagGreen); r.register(alchBagRed); r.register(alchBagBlack);
		r.register(repairTalisman);
		r.register(kleinStarEin);
		r.register(kleinStarZwei);
		r.register(kleinStarDrei);
		r.register(kleinStarVier);
		r.register(kleinStarSphere);
		r.register(kleinStarOmega);
		r.register(alchemicalCoal);
		r.register(mobiusFuel);
		r.register(aeternalisFuel);
		r.register(covalenceDustLow);
		r.register(covalenceDustMedium);
		r.register(covalenceDustHigh);
		r.register(darkMatter);
		r.register(redMatter);

		r.register(dmPick);
		r.register(dmAxe);
		r.register(dmShovel);
		r.register(dmSword);
		r.register(dmHoe);
		r.register(dmShears);
		r.register(dmHammer);

		r.register(rmPick);
		r.register(rmAxe);
		r.register(rmShovel);
		r.register(rmSword);
		r.register(rmHoe);
		r.register(rmShears);
		r.register(rmHammer);
		r.register(rmKatar);
		r.register(rmStar);

		r.register(dmHelmet);
		r.register(dmChest);
		r.register(dmLegs);
		r.register(dmFeet);

		r.register(rmHelmet);
		r.register(rmChest);
		r.register(rmLegs);
		r.register(rmFeet);

		r.register(gemHelmet);
		r.register(gemChest);
		r.register(gemLegs);
		r.register(gemFeet);

		r.register(ironBand);
		r.register(blackHole);
		r.register(angelSmite);
		r.register(harvestGod);
		r.register(ignition);
		r.register(zero);
		r.register(swrg);
		r.register(timeWatch);
		r.register(eternalDensity);
		r.register(dRod1);
		r.register(dRod2);
		r.register(dRod3);
		r.register(mercEye);
		r.register(voidRing);
		r.register(arcana);

		r.register(bodyStone);
		r.register(soulStone);
		r.register(mindStone);
		r.register(lifeStone);

		r.register(everTide);
		r.register(volcanite);

		r.register(waterOrb);
		r.register(lavaOrb);
		r.register(mobRandomizer);
		r.register(lensExplosive);
		r.register(fireProjectile);
		r.register(windProjectile);

		r.register(dCatalyst);
		r.register(hyperLens);
		r.register(cataliticLens);

		r.register(tome);
		r.register(transmutationTablet);
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> evt)
	{
		evt.getRegistry().register(WATER_PROJECTILE);
		evt.getRegistry().register(LAVA_PROJECTILE);
		evt.getRegistry().register(MOB_RANDOMIZER);
		evt.getRegistry().register(LENS_PROJECTILE);
		evt.getRegistry().register(NOVA_CATALYST_PRIMED);
		evt.getRegistry().register(NOVA_CATACLYSM_PRIMED);
		evt.getRegistry().register(HOMING_ARROW);
		evt.getRegistry().register(FIRE_PROJECTILE);
		evt.getRegistry().register(SWRG_PROJECTILE);
	}

	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt)
	{
		evt.getRegistry().register(ALCH_CHEST_TILE);
		evt.getRegistry().register(INTERDICTION_TORCH_TILE);
		evt.getRegistry().register(CONDENSER_TILE);
		evt.getRegistry().register(CONDENSER_MK2_TILE);
		evt.getRegistry().register(RM_FURNACE_TILE);
		evt.getRegistry().register(DM_FURNACE_TILE);
		evt.getRegistry().register(COLLECTOR_MK1_TILE);
		evt.getRegistry().register(COLLECTOR_MK2_TILE);
		evt.getRegistry().register(COLLECTOR_MK3_TILE);
		evt.getRegistry().register(RELAY_MK1_TILE);
		evt.getRegistry().register(RELAY_MK2_TILE);
		evt.getRegistry().register(RELAY_MK3_TILE);
		evt.getRegistry().register(DM_PEDESTAL_TILE);
	}

	private static <V extends IForgeRegistryEntry<V>> void registerObj(IForgeRegistry<V> registry, IForgeRegistryEntry<V> o, ResourceLocation name)
	{
		registry.register(o.setRegistryName(name));
	}
}
