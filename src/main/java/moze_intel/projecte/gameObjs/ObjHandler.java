package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.blocks.*;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.container.AlchChestContainer;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.gameObjs.container.MercurialEyeContainer;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.RelayMK2Container;
import moze_intel.projecte.gameObjs.container.RelayMK3Container;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
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
import moze_intel.projecte.gameObjs.gui.AbstractCondenserScreen;
import moze_intel.projecte.gameObjs.gui.AlchBagScreen;
import moze_intel.projecte.gameObjs.gui.AlchChestScreen;
import moze_intel.projecte.gameObjs.gui.GUICollectorMK1;
import moze_intel.projecte.gameObjs.gui.GUICollectorMK2;
import moze_intel.projecte.gameObjs.gui.GUICollectorMK3;
import moze_intel.projecte.gameObjs.gui.GUIDMFurnace;
import moze_intel.projecte.gameObjs.gui.GUIEternalDensity;
import moze_intel.projecte.gameObjs.gui.GUIMercurialEye;
import moze_intel.projecte.gameObjs.gui.GUIRMFurnace;
import moze_intel.projecte.gameObjs.gui.GUIRelayMK1;
import moze_intel.projecte.gameObjs.gui.GUIRelayMK2;
import moze_intel.projecte.gameObjs.gui.GUIRelayMK3;
import moze_intel.projecte.gameObjs.gui.GUITransmutation;
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
import moze_intel.projecte.gameObjs.items.rings.TimeWatch;
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
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(modid = PECore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ObjHandler
{
	private static final ItemGroup cTab = new ItemGroup(PECore.MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(philosStone);
		}
	};
	public static final IRecipeSerializer<RecipesCovalenceRepair> COVALENCE_REPAIR_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(RecipesCovalenceRepair::new);
	public static final IRecipeSerializer<RecipeShapelessKleinStar> KLEIN_RECIPE_SERIALIZER = new RecipeShapelessKleinStar.Serializer();
	public static final IRecipeSerializer<RecipeShapelessHidden> SHAPELESS_HIDDEN_SERIALIZER = new RecipeShapelessHidden.Serializer();
	public static final Block alchChest = new AlchemicalChest(Block.Properties.create(Material.ROCK).hardnessAndResistance(10, 6000000)).setRegistryName(PECore.MODID, "alchemical_chest");
	public static final Block interdictionTorch = new InterdictionTorch(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0).lightValue(14).tickRandomly()).setRegistryName(PECore.MODID, "interdiction_torch");
	public static final Block interdictionTorchWall = new InterdictionTorchWall(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0).lightValue(14).tickRandomly()).setRegistryName(PECore.MODID, "wall_interdiction_torch");
	public static final Block transmuteStone = new TransmutationStone(Block.Properties.create(Material.ROCK).hardnessAndResistance(10)).setRegistryName(PECore.MODID, "transmutation_table");
	public static final Block condenser = new Condenser(Block.Properties.create(Material.ROCK).hardnessAndResistance(10, 6000000)).setRegistryName(PECore.MODID, "condenser_mk1");
	public static final Block condenserMk2 = new CondenserMK2(Block.Properties.create(Material.ROCK).hardnessAndResistance(10, 6000000)).setRegistryName(PECore.MODID, "condenser_mk2");
	public static final Block rmFurnaceOff = new MatterFurnace(Block.Properties.create(Material.ROCK).hardnessAndResistance(1000000F).lightValue(14), EnumMatterType.DARK_MATTER).setRegistryName(PECore.MODID, "dm_furnace");
	public static final Block dmFurnaceOff = new MatterFurnace(Block.Properties.create(Material.ROCK).hardnessAndResistance(2000000F).lightValue(14), EnumMatterType.RED_MATTER).setRegistryName(PECore.MODID, "rm_furnace");
	public static final Block dmPedestal = new Pedestal(Block.Properties.create(Material.ROCK).hardnessAndResistance(1).lightValue(12)).setRegistryName(PECore.MODID, "dm_pedestal");
	//TODO: 1.14, Check this again, in 1.12 this would have equivalently had a resistance of 6 not something in the millions
	public static final Block dmBlock = new MatterBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(1000000), EnumMatterType.DARK_MATTER).setRegistryName(PECore.MODID, "dark_matter_block");
	public static final Block rmBlock = new MatterBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2000000), EnumMatterType.RED_MATTER).setRegistryName(PECore.MODID, "red_matter_block");
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
	public static final Item alchBagWhite = new AlchemicalBag(ibNoStack(), DyeColor.WHITE).setRegistryName(PECore.MODID, "white_alchemical_bag");
	public static final Item alchBagOrange = new AlchemicalBag(ibNoStack(), DyeColor.ORANGE).setRegistryName(PECore.MODID, "orange_alchemical_bag");
	public static final Item alchBagMagenta = new AlchemicalBag(ibNoStack(), DyeColor.MAGENTA).setRegistryName(PECore.MODID, "magenta_alchemical_bag");
	public static final Item alchBagLightBlue = new AlchemicalBag(ibNoStack(), DyeColor.LIGHT_BLUE).setRegistryName(PECore.MODID, "light_blue_alchemical_bag");
	public static final Item alchBagYellow = new AlchemicalBag(ibNoStack(), DyeColor.YELLOW).setRegistryName(PECore.MODID, "yellow_alchemical_bag");
	public static final Item alchBagLime = new AlchemicalBag(ibNoStack(), DyeColor.LIME).setRegistryName(PECore.MODID, "lime_alchemical_bag");
	public static final Item alchBagPink = new AlchemicalBag(ibNoStack(), DyeColor.PINK).setRegistryName(PECore.MODID, "pink_alchemical_bag");
	public static final Item alchBagGray = new AlchemicalBag(ibNoStack(), DyeColor.GRAY).setRegistryName(PECore.MODID, "gray_alchemical_bag");
	public static final Item alchBagLightGray = new AlchemicalBag(ibNoStack(), DyeColor.LIGHT_GRAY).setRegistryName(PECore.MODID, "light_gray_alchemical_bag");
	public static final Item alchBagCyan = new AlchemicalBag(ibNoStack(), DyeColor.CYAN).setRegistryName(PECore.MODID, "cyan_alchemical_bag");
	public static final Item alchBagPurple = new AlchemicalBag(ibNoStack(), DyeColor.PURPLE).setRegistryName(PECore.MODID, "purple_alchemical_bag");
	public static final Item alchBagBlue = new AlchemicalBag(ibNoStack(), DyeColor.BLUE).setRegistryName(PECore.MODID, "blue_alchemical_bag");
	public static final Item alchBagBrown = new AlchemicalBag(ibNoStack(), DyeColor.BROWN).setRegistryName(PECore.MODID, "brown_alchemical_bag");
	public static final Item alchBagGreen = new AlchemicalBag(ibNoStack(), DyeColor.GREEN).setRegistryName(PECore.MODID, "green_alchemical_bag");
	public static final Item alchBagRed = new AlchemicalBag(ibNoStack(), DyeColor.RED).setRegistryName(PECore.MODID, "red_alchemical_bag");
	public static final Item alchBagBlack = new AlchemicalBag(ibNoStack(), DyeColor.BLACK).setRegistryName(PECore.MODID, "black_alchemical_bag");
	public static final Item repairTalisman = new RepairTalisman(ibNoStack()).setRegistryName(PECore.MODID, "repair_talisman");
	public static final Item kleinStarEin = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.EIN).setRegistryName(PECore.MODID, "klein_star_ein");
	public static final Item kleinStarZwei = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.ZWEI).setRegistryName(PECore.MODID, "klein_star_zwei");
	public static final Item kleinStarDrei = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.DREI).setRegistryName(PECore.MODID, "klein_star_drei");
	public static final Item kleinStarVier = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.VIER).setRegistryName(PECore.MODID, "klein_star_vier");
	public static final Item kleinStarSphere = new KleinStar(ibNoStack(), KleinStar.EnumKleinTier.SPHERE).setRegistryName(PECore.MODID, "klein_star_sphere");
	public static final Item kleinStarOmega = new KleinStar(ibNoStack().rarity(Rarity.EPIC), KleinStar.EnumKleinTier.OMEGA).setRegistryName(PECore.MODID, "klein_star_omega");
	public static final Item alchemicalCoal = new AlchemicalFuel(ib(), EnumFuelType.ALCHEMICAL_COAL).setRegistryName(PECore.MODID, "alchemical_coal");
	public static final Item mobiusFuel = new AlchemicalFuel(ib(), EnumFuelType.MOBIUS_FUEL).setRegistryName(PECore.MODID, "mobius_fuel");
	public static final Item aeternalisFuel = new AlchemicalFuel(ib().rarity(Rarity.RARE), EnumFuelType.AETERNALIS_FUEL).setRegistryName(PECore.MODID, "aeternalis_fuel");
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

	public static final Item dmHelmet = new DMArmor(EquipmentSlotType.HEAD, ibNoStack()).setRegistryName(PECore.MODID, "dm_helmet");
	public static final Item dmChest = new DMArmor(EquipmentSlotType.CHEST, ibNoStack()).setRegistryName(PECore.MODID, "dm_chestplate");
	public static final Item dmLegs = new DMArmor(EquipmentSlotType.LEGS, ibNoStack()).setRegistryName(PECore.MODID, "dm_leggings");
	public static final Item dmFeet = new DMArmor(EquipmentSlotType.FEET, ibNoStack()).setRegistryName(PECore.MODID, "dm_boots");

	public static final Item rmHelmet = new RMArmor(EquipmentSlotType.HEAD, ibNoStack()).setRegistryName(PECore.MODID, "rm_helmet");
	public static final Item rmChest = new RMArmor(EquipmentSlotType.CHEST, ibNoStack()).setRegistryName(PECore.MODID, "rm_chestplate");
	public static final Item rmLegs = new RMArmor(EquipmentSlotType.LEGS, ibNoStack()).setRegistryName(PECore.MODID, "rm_leggings");
	public static final Item rmFeet = new RMArmor(EquipmentSlotType.FEET, ibNoStack()).setRegistryName(PECore.MODID, "rm_boots");

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
	public static final Item dRod1 = new DiviningRod(ibNoStack(), new String[] { "pe.diving_rod.mode.range.3" }).setRegistryName(PECore.MODID, "divining_rod_1");
	public static final Item dRod2 = new DiviningRod(ibNoStack(), new String[]{ "pe.diving_rod.mode.range.3", "pe.diving_rod.mode.range.16" }).setRegistryName(PECore.MODID, "divining_rod_2");
	public static final Item dRod3 = new DiviningRod(ibNoStack(), new String[] { "pe.diving_rod.mode.range.3", "pe.diving_rod.mode.range.16", "pe.diving_rod.mode.range.64" }).setRegistryName(PECore.MODID, "divining_rod_3");
	public static final Item mercEye = new MercurialEye(ibNoStack()).setRegistryName(PECore.MODID, "mercurial_eye");
	public static final Item voidRing = new VoidRing(ibNoStack()).setRegistryName(PECore.MODID, "void_ring");
	public static final Item arcana = new Arcana(ibNoStack().rarity(Rarity.RARE)).setRegistryName(PECore.MODID, "arcana_ring");

	public static final Item dCatalyst = new DestructionCatalyst(ibNoStack()).setRegistryName(PECore.MODID, "destruction_catalyst");
	public static final Item hyperLens = new HyperkineticLens(ibNoStack()).setRegistryName(PECore.MODID, "hyperkinetic_lens");
	public static final Item cataliticLens = new CataliticLens(ibNoStack()).setRegistryName(PECore.MODID, "catalytic_lens");

	public static final Item bodyStone = new BodyStone(ibNoStack()).setRegistryName(PECore.MODID, "body_stone");
	public static final Item soulStone = new SoulStone(ibNoStack()).setRegistryName(PECore.MODID, "soul_stone");
	public static final Item mindStone = new MindStone(ibNoStack()).setRegistryName(PECore.MODID, "mind_stone");
	public static final Item lifeStone = new LifeStone(ibNoStack()).setRegistryName(PECore.MODID, "life_stone");

	public static final Item tome = new Tome(ibNoStack().rarity(Rarity.EPIC)).setRegistryName(PECore.MODID, "tome");

	// TODO 1.13 get rid of these
	public static final Item waterOrb = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "water_orb");
	public static final Item lavaOrb = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "lava_orb");
	public static final Item mobRandomizer = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "randomizer");
	public static final Item lensExplosive = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "lens_explosive");
	public static final Item fireProjectile = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "fire_projectile");
	public static final Item windProjectile = new Item(new Item.Properties()).setRegistryName(PECore.MODID, "wind_projectile");
	public static final Item transmutationTablet = new TransmutationTablet(ibNoStack()).setRegistryName(PECore.MODID, "transmutation_tablet");

	public static final EntityType<EntityFireProjectile> FIRE_PROJECTILE = EntityType.Builder
			.create((EntityType.IFactory<EntityFireProjectile>) EntityFireProjectile::new, EntityClassification.MISC)
			.setTrackingRange(256)
			.setUpdateInterval(10)
			.build("");
	public static final EntityType<EntityHomingArrow> HOMING_ARROW = EntityType.Builder
			.create((EntityType.IFactory<EntityHomingArrow>) EntityHomingArrow::new, EntityClassification.MISC)
			.setTrackingRange(5)
			.setUpdateInterval(20)
			.setShouldReceiveVelocityUpdates(true)
			.build("");
	public static final EntityType<EntityLavaProjectile> LAVA_PROJECTILE = EntityType.Builder
			.create((EntityType.IFactory<EntityLavaProjectile>) EntityLavaProjectile::new, EntityClassification.MISC)
			.setTrackingRange(256)
			.setUpdateInterval(10)
			.build("");
	public static final EntityType<EntityLensProjectile> LENS_PROJECTILE = EntityType.Builder
			.create((EntityType.IFactory<EntityLensProjectile>) EntityLensProjectile::new, EntityClassification.MISC)
			.setTrackingRange(256)
			.setUpdateInterval(10)
			.build("");
	public static final EntityType<EntityMobRandomizer> MOB_RANDOMIZER = EntityType.Builder
			.create((EntityType.IFactory<EntityMobRandomizer>) EntityMobRandomizer::new, EntityClassification.MISC)
			.setTrackingRange(256)
			.setUpdateInterval(10)
			.build("");
	public static final EntityType<EntityNovaCatalystPrimed> NOVA_CATALYST_PRIMED = EntityType.Builder
			.create((EntityType.IFactory<EntityNovaCatalystPrimed>) EntityNovaCatalystPrimed::new, EntityClassification.MISC)
			.setTrackingRange(10)
			.setUpdateInterval(10)
			.build("");
	public static final EntityType<EntityNovaCataclysmPrimed> NOVA_CATACLYSM_PRIMED = EntityType.Builder
			.create((EntityType.IFactory<EntityNovaCataclysmPrimed>) EntityNovaCataclysmPrimed::new, EntityClassification.MISC)
			.setTrackingRange(10)
			.setUpdateInterval(10)
			.build("");
	public static final EntityType<EntitySWRGProjectile> SWRG_PROJECTILE = EntityType.Builder
			.create((EntityType.IFactory<EntitySWRGProjectile>) EntitySWRGProjectile::new, EntityClassification.MISC)
			.setTrackingRange(256)
			.setUpdateInterval(10)
			.build("");
	public static final EntityType<EntityWaterProjectile> WATER_PROJECTILE = EntityType.Builder
			.create((EntityType.IFactory<EntityWaterProjectile>) EntityWaterProjectile::new, EntityClassification.MISC)
			.setTrackingRange(256)
			.setUpdateInterval(10)
			.build("");

	public static final TileEntityType<?> ALCH_CHEST_TILE = TileEntityType.Builder.create(AlchChestTile::new, alchChest).build(null).setRegistryName(PECore.MODID, "alchemical_chest");
	public static final TileEntityType<?> COLLECTOR_MK1_TILE = TileEntityType.Builder.create(CollectorMK1Tile::new, collectorMK1).build(null).setRegistryName(PECore.MODID, "collector_mk1");
	public static final TileEntityType<?> COLLECTOR_MK2_TILE = TileEntityType.Builder.create(CollectorMK2Tile::new, collectorMK2).build(null).setRegistryName(PECore.MODID, "collector_mk2");
	public static final TileEntityType<?> COLLECTOR_MK3_TILE = TileEntityType.Builder.create(CollectorMK3Tile::new, collectorMK3).build(null).setRegistryName(PECore.MODID, "collector_mk3");
	public static final TileEntityType<?> CONDENSER_TILE = TileEntityType.Builder.create(CondenserTile::new, condenser).build(null).setRegistryName(PECore.MODID, "condenser");
	public static final TileEntityType<?> CONDENSER_MK2_TILE = TileEntityType.Builder.create(CondenserMK2Tile::new, condenserMk2).build(null).setRegistryName(PECore.MODID, "condenser_mk2");
	public static final TileEntityType<?> RELAY_MK1_TILE = TileEntityType.Builder.create(RelayMK1Tile::new, relay).build(null).setRegistryName(PECore.MODID, "relay_mk1");
	public static final TileEntityType<?> RELAY_MK2_TILE = TileEntityType.Builder.create(RelayMK2Tile::new, relayMK2).build(null).setRegistryName(PECore.MODID, "relay_mk2");
	public static final TileEntityType<?> RELAY_MK3_TILE = TileEntityType.Builder.create(RelayMK3Tile::new, relayMK3).build(null).setRegistryName(PECore.MODID, "relay_mk3");
	public static final TileEntityType<?> DM_FURNACE_TILE = TileEntityType.Builder.create(DMFurnaceTile::new, dmFurnaceOff).build(null).setRegistryName(PECore.MODID, "dm_furnace");
	public static final TileEntityType<?> RM_FURNACE_TILE = TileEntityType.Builder.create(RMFurnaceTile::new, rmFurnaceOff).build(null).setRegistryName(PECore.MODID, "rm_furnace");
	public static final TileEntityType<?> INTERDICTION_TORCH_TILE = TileEntityType.Builder.create(InterdictionTile::new, interdictionTorch, interdictionTorchWall).build(null).setRegistryName(PECore.MODID, "interdiction_torch");
	public static final TileEntityType<?> DM_PEDESTAL_TILE = TileEntityType.Builder.create(DMPedestalTile::new, dmPedestal).build(null).setRegistryName(PECore.MODID, "dm_pedestal");

	public static final ContainerType<RMFurnaceContainer> RM_FURNACE_CONTAINER = IForgeContainerType.create(RMFurnaceContainer::fromNetwork);
	public static final ContainerType<DMFurnaceContainer> DM_FURNACE_CONTAINER = IForgeContainerType.create(DMFurnaceContainer::fromNetwork);
	public static final ContainerType<CondenserContainer> CONDENSER_CONTAINER = IForgeContainerType.create(CondenserContainer::fromNetwork);
	public static final ContainerType<CondenserMK2Container> CONDENSER_MK2_CONTAINER = IForgeContainerType.create(CondenserMK2Container::fromNetwork);
	public static final ContainerType<AlchChestContainer> ALCH_CHEST_CONTAINER = IForgeContainerType.create(AlchChestContainer::fromNetwork);
	public static final ContainerType<AlchBagContainer> ALCH_BAG_CONTAINER = IForgeContainerType.create(AlchBagContainer::fromNetwork);
	public static final ContainerType<EternalDensityContainer> ETERNAL_DENSITY_CONTAINER = IForgeContainerType.create(EternalDensityContainer::fromNetwork);
	public static final ContainerType<TransmutationContainer> TRANSMUTATION_CONTAINER = IForgeContainerType.create(TransmutationContainer::fromNetwork);
	public static final ContainerType<RelayMK1Container> RELAY_MK1_CONTAINER = IForgeContainerType.create(RelayMK1Container::fromNetwork);
	public static final ContainerType<RelayMK2Container> RELAY_MK2_CONTAINER = IForgeContainerType.create(RelayMK2Container::fromNetwork);
	public static final ContainerType<RelayMK3Container> RELAY_MK3_CONTAINER = IForgeContainerType.create(RelayMK3Container::fromNetwork);
	public static final ContainerType<CollectorMK1Container> COLLECTOR_MK1_CONTAINER = IForgeContainerType.create(CollectorMK1Container::fromNetwork);
	public static final ContainerType<CollectorMK2Container> COLLECTOR_MK2_CONTAINER = IForgeContainerType.create(CollectorMK2Container::fromNetwork);
	public static final ContainerType<CollectorMK3Container> COLLECTOR_MK3_CONTAINER = IForgeContainerType.create(CollectorMK3Container::fromNetwork);
	public static final ContainerType<MercurialEyeContainer> MERCURIAL_EYE_CONTAINER = IForgeContainerType.create(MercurialEyeContainer::fromNetwork);

	private static Item.Properties ib()
	{
		return new Item.Properties().group(cTab);
	}
	
	private static Item.Properties ibNoStack()
	{
		return ib().maxStackSize(1);
	}

	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> evt)
	{
		IForgeRegistry<ContainerType<?>> r = evt.getRegistry();
		r.register(RM_FURNACE_CONTAINER.setRegistryName(rmFurnaceOff.getRegistryName()));
		r.register(DM_FURNACE_CONTAINER.setRegistryName(dmFurnaceOff.getRegistryName()));
		r.register(CONDENSER_CONTAINER.setRegistryName(condenser.getRegistryName()));
		r.register(CONDENSER_MK2_CONTAINER.setRegistryName(condenserMk2.getRegistryName()));
		r.register(ALCH_CHEST_CONTAINER.setRegistryName(alchChest.getRegistryName()));
		r.register(ALCH_BAG_CONTAINER.setRegistryName(new ResourceLocation(PECore.MODID, "alchemical_bag")));
		r.register(ETERNAL_DENSITY_CONTAINER.setRegistryName(eternalDensity.getRegistryName()));
		r.register(TRANSMUTATION_CONTAINER.setRegistryName(transmuteStone.getRegistryName()));
		r.register(RELAY_MK1_CONTAINER.setRegistryName(relay.getRegistryName()));
		r.register(RELAY_MK2_CONTAINER.setRegistryName(relayMK2.getRegistryName()));
		r.register(RELAY_MK3_CONTAINER.setRegistryName(relayMK3.getRegistryName()));
		r.register(COLLECTOR_MK1_CONTAINER.setRegistryName(collectorMK1.getRegistryName()));
		r.register(COLLECTOR_MK2_CONTAINER.setRegistryName(collectorMK2.getRegistryName()));
		r.register(COLLECTOR_MK3_CONTAINER.setRegistryName(collectorMK3.getRegistryName()));
		r.register(MERCURIAL_EYE_CONTAINER.setRegistryName(mercEye.getRegistryName()));
		ScreenManager.registerFactory(RM_FURNACE_CONTAINER, GUIRMFurnace::new);
		ScreenManager.registerFactory(DM_FURNACE_CONTAINER, GUIDMFurnace::new);
		ScreenManager.registerFactory(CONDENSER_CONTAINER, AbstractCondenserScreen.MK1::new);
		ScreenManager.registerFactory(CONDENSER_MK2_CONTAINER, AbstractCondenserScreen.MK2::new);
		ScreenManager.registerFactory(ALCH_CHEST_CONTAINER, AlchChestScreen::new);
		ScreenManager.registerFactory(ALCH_BAG_CONTAINER, AlchBagScreen::new);
		ScreenManager.registerFactory(ETERNAL_DENSITY_CONTAINER, GUIEternalDensity::new);
		ScreenManager.registerFactory(TRANSMUTATION_CONTAINER, GUITransmutation::new);
		ScreenManager.registerFactory(RELAY_MK1_CONTAINER, GUIRelayMK1::new);
		ScreenManager.registerFactory(RELAY_MK2_CONTAINER, GUIRelayMK2::new);
		ScreenManager.registerFactory(RELAY_MK3_CONTAINER, GUIRelayMK3::new);
		ScreenManager.registerFactory(COLLECTOR_MK1_CONTAINER, GUICollectorMK1::new);
		ScreenManager.registerFactory(COLLECTOR_MK2_CONTAINER, GUICollectorMK2::new);
		ScreenManager.registerFactory(COLLECTOR_MK3_CONTAINER, GUICollectorMK3::new);
		ScreenManager.registerFactory(MERCURIAL_EYE_CONTAINER, GUIMercurialEye::new);
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> evt)
	{
		IForgeRegistry<IRecipeSerializer<?>> r = evt.getRegistry();
		r.register(COVALENCE_REPAIR_RECIPE_SERIALIZER
				.setRegistryName(new ResourceLocation(PECore.MODID, "covalence_repair")));
		r.register(SHAPELESS_HIDDEN_SERIALIZER
				.setRegistryName(new ResourceLocation(PECore.MODID, "shapeless_recipe_hidden")));
		r.register(KLEIN_RECIPE_SERIALIZER
				.setRegistryName(new ResourceLocation(PECore.MODID, "crafting_shapeless_kleinstar")));
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
		registerObj(r, new BlockItem(alchChest, ib()), alchChest.getRegistryName());
		registerObj(r, new BlockItem(collectorMK1, ib()), collectorMK1.getRegistryName());
		registerObj(r, new BlockItem(collectorMK2, ib()), collectorMK2.getRegistryName());
		registerObj(r, new BlockItem(collectorMK3, ib()), collectorMK3.getRegistryName());
		registerObj(r, new BlockItem(condenser, ib()), condenser.getRegistryName());
		registerObj(r, new BlockItem(condenserMk2, ib()), condenserMk2.getRegistryName());
		registerObj(r, new BlockItem(dmFurnaceOff, ib()), dmFurnaceOff.getRegistryName());
		registerObj(r, new BlockItem(dmPedestal, ib()), dmPedestal.getRegistryName());
		registerObj(r, new ItemFuelBlock(alchemicalCoalBlock, ib(), EnumFuelType.ALCHEMICAL_COAL), alchemicalCoalBlock.getRegistryName());
		registerObj(r, new ItemFuelBlock(mobiusFuelBlock, ib(), EnumFuelType.MOBIUS_FUEL), mobiusFuelBlock.getRegistryName());
		registerObj(r, new ItemFuelBlock(aeternalisFuelBlock, ib(), EnumFuelType.AETERNALIS_FUEL), aeternalisFuelBlock.getRegistryName());
		registerObj(r, new WallOrFloorItem(interdictionTorch, interdictionTorchWall, ib()), interdictionTorch.getRegistryName());
		registerObj(r, new BlockItem(dmBlock, ib()), dmBlock.getRegistryName());
		registerObj(r, new BlockItem(rmBlock, ib()), rmBlock.getRegistryName());
		registerObj(r, new BlockItem(novaCatalyst, ib()), novaCatalyst.getRegistryName());
		registerObj(r, new BlockItem(novaCataclysm, ib()), novaCataclysm.getRegistryName());
		registerObj(r, new BlockItem(relay, ib()), relay.getRegistryName());
		registerObj(r, new BlockItem(relayMK2, ib()), relayMK2.getRegistryName());
		registerObj(r, new BlockItem(relayMK3, ib()), relayMK3.getRegistryName());
		registerObj(r, new BlockItem(rmFurnaceOff, ib()), rmFurnaceOff.getRegistryName());
		registerObj(r, new BlockItem(transmuteStone, ib()), transmuteStone.getRegistryName());

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
		evt.getRegistry().register(WATER_PROJECTILE.setRegistryName(PECore.MODID, "water_projectile"));
		evt.getRegistry().register(LAVA_PROJECTILE.setRegistryName(PECore.MODID, "lava_projectile"));
		evt.getRegistry().register(MOB_RANDOMIZER.setRegistryName(PECore.MODID, "mob_randomizer"));
		evt.getRegistry().register(LENS_PROJECTILE.setRegistryName(PECore.MODID, "lens_projectile"));
		evt.getRegistry().register(NOVA_CATALYST_PRIMED.setRegistryName(PECore.MODID, "nova_catalyst_primed"));
		evt.getRegistry().register(NOVA_CATACLYSM_PRIMED.setRegistryName(PECore.MODID, "nova_cataclysm_primed"));
		evt.getRegistry().register(HOMING_ARROW.setRegistryName(PECore.MODID, "homing_arrow"));
		evt.getRegistry().register(FIRE_PROJECTILE.setRegistryName(PECore.MODID, "fire_projectile"));
		evt.getRegistry().register(SWRG_PROJECTILE.setRegistryName(PECore.MODID, "swrg_projectile"));
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

	public static Item getBag(DyeColor color)
	{
		switch (color)
		{
			default:
			case WHITE: return alchBagWhite;
			case ORANGE: return alchBagOrange;
			case MAGENTA: return alchBagMagenta;
			case LIGHT_BLUE: return alchBagLightBlue;
			case YELLOW: return alchBagYellow;
			case LIME: return alchBagLime;
			case PINK: return alchBagPink;
			case GRAY: return alchBagGray;
			case LIGHT_GRAY: return alchBagLightGray;
			case CYAN: return alchBagCyan;
			case PURPLE: return alchBagPurple;
			case BLUE: return alchBagBlue;
			case BROWN: return alchBagBrown;
			case GREEN: return alchBagGreen;
			case RED: return alchBagRed;
			case BLACK: return alchBagBlack;
		}
	}
}
