package moze_intel.projecte.client.lang;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Util;

public class PELangProvider extends BaseLanguageProvider {

	public PELangProvider(DataGenerator gen) {
		super(gen, PECore.MODID);
	}

	@Override
	protected void addTranslations() {
		addAdvancements();
		addBlocks();
		addCommands();
		addEMC();
		addEntityTypes();
		addItems();
		addModes();
		addPedestalTooltips();
		addSubtitles();
		addTooltips();
		addTransmutation();
		//Misc stuff
		add(PELang.PROJECTE, "ProjectE");
		add(PELang.SECONDS, "%s seconds");
		add(PELang.EVERY_TICK, "%s seconds (every tick)");
		add(PELang.HIGH_ALCHEMIST, "High alchemist %s has joined the server");
		add(PELang.UPDATE_AVAILABLE, "New ProjectE update available! Version: %s");
		add(PELang.UPDATE_GET_IT, "Get it here!");
		add(PELang.BLACKLIST, "Blacklist");
		add(PELang.WHITELIST, "Whitelist");
		add(PELang.DENSITY_MODE_TARGET, "Set target to: %s");
		//Divining Rod
		add(PELang.DIVINING_AVG_EMC, "Average EMC for %s blocks: %s");
		add(PELang.DIVINING_MAX_EMC, "Max EMC: %s");
		add(PELang.DIVINING_SECOND_MAX, "Second Max EMC: %s");
		add(PELang.DIVINING_THIRD_MAX, "Third Max EMC: %s");
		add(PELang.DIVINING_RANGE_3, "3x3x3");
		add(PELang.DIVINING_RANGE_16, "16x3x3");
		add(PELang.DIVINING_RANGE_64, "64x3x3");
		//Keybinds
		add(PEKeybind.HELMET_TOGGLE, "Helmet Effects");
		add(PEKeybind.BOOTS_TOGGLE, "Boots Effects");
		add(PEKeybind.CHARGE, "Charge");
		add(PEKeybind.EXTRA_FUNCTION, "Extra Function");
		add(PEKeybind.FIRE_PROJECTILE, "Fire Projectile");
		add(PEKeybind.MODE, "Change Mode");
		//JEI
		add(PELang.JEI_COLLECTOR, "Collector Fuel Upgrades");
		add(PELang.WORLD_TRANSMUTE, "World Transmutation");
		add(PELang.WORLD_TRANSMUTE_DESCRIPTION, "Click in world, shift click for second output");
		//Curios
		add(PELang.CURIOS_KLEIN_STAR, "Klein Star");
		//Gem Armor
		add(PELang.GEM_ENABLED, "ENABLED");
		add(PELang.GEM_DISABLED, "DISABLED");
		add(PELang.GEM_ACTIVATE, "Activated Gem Armor Offensive Abilities");
		add(PELang.GEM_DEACTIVATE, "Deactivated Gem Armor Offensive Abilities");
		add(PELang.NIGHT_VISION, "Night Vision: %s");
		add(PELang.NIGHT_VISION_PROMPT, "Press %s to toggle Night Vision");
		add(PELang.STEP_ASSIST, "Step Assist: %s");
		add(PELang.STEP_ASSIST_PROMPT, "Press %s to toggle Step Assist");
		add(PELang.GEM_LORE_HELM, "Abyss Helmet");
		add(PELang.GEM_LORE_CHEST, "Infernal Armor");
		add(PELang.GEM_LORE_LEGS, "Gravity Greaves");
		add(PELang.GEM_LORE_FEET, "Hurricane Boots");
		//Watch of Flowing Time
		add(PELang.TIME_WATCH_DISABLED, "Item disabled by server admin");
		add(PELang.TIME_WATCH_MODE, "Time control mode: %s");
		add(PELang.TIME_WATCH_MODE_SWITCH, "Time control mode set to: %s");
		add(PELang.TIME_WATCH_OFF, "Off");
		add(PELang.TIME_WATCH_FAST_FORWARD, "Fast-Forward");
		add(PELang.TIME_WATCH_REWIND, "Rewind");
		//GUI
		add(PELang.GUI_DARK_MATTER_FURNACE, "DM Furnace");
		add(PELang.GUI_RED_MATTER_FURNACE, "RM Furnace");
		add(PELang.GUI_RELAY_MK1, "Relay MKI");
		add(PELang.GUI_RELAY_MK2, "Relay MKII");
		add(PELang.GUI_RELAY_MK3, "Relay MKIII");
	}

	private void addAdvancements() {
		add(PELang.ADVANCEMENTS_PROJECTE_DESCRIPTION, "Correspondent Commerce?");
		add(PELang.ADVANCEMENTS_PHILO_STONE, "An alchemist's best friend!");
		add(PELang.ADVANCEMENTS_PHILO_STONE_DESCRIPTION, "Let's get things started! Craft a philosopher's stone");
		add(PELang.ADVANCEMENTS_ALCH_CHEST, "Storage Upgrade!");
		add(PELang.ADVANCEMENTS_ALCH_CHEST_DESCRIPTION, "A \"little\" chest upgrade.");
		add(PELang.ADVANCEMENTS_ALCH_BAG, "Pocket storage!");
		add(PELang.ADVANCEMENTS_ALCH_BAG_DESCRIPTION, "All the wonders of an alchemical chest, in your pocket.");
		add(PELang.ADVANCEMENTS_TRANSMUTATION_TABLE, "Transmute this into that!");
		add(PELang.ADVANCEMENTS_TRANSMUTATION_TABLE_DESCRIPTION, "The beginning (and end) of everything.");
		add(PELang.ADVANCEMENTS_CONDENSER, "Condense the world!");
		add(PELang.ADVANCEMENTS_CONDENSER_DESCRIPTION, "MORE DIAMONDS!");
		add(PELang.ADVANCEMENTS_COLLECTOR, "The power of the sun!");
		add(PELang.ADVANCEMENTS_COLLECTOR_DESCRIPTION, "Now the fun begins.");
		add(PELang.ADVANCEMENTS_RELAY, "Power flowers!");
		add(PELang.ADVANCEMENTS_RELAY_DESCRIPTION, "Linking collectors together for even more power.");
		add(PELang.ADVANCEMENTS_TRANSMUTATION_TABLET, "Transmutation on the go!");
		add(PELang.ADVANCEMENTS_TRANSMUTATION_TABLET_DESCRIPTION, "And then you thought things couldn't get better.");
		add(PELang.ADVANCEMENTS_DARK_MATTER, "All that Matters.");
		add(PELang.ADVANCEMENTS_DARK_MATTER_DESCRIPTION, "It looks... weird....");
		add(PELang.ADVANCEMENTS_RED_MATTER, "Even better Matter!");
		add(PELang.ADVANCEMENTS_RED_MATTER_DESCRIPTION, "The space time continuum may be broken.");
		add(PELang.ADVANCEMENTS_DARK_MATTER_BLOCK, "A block that Matters!");
		add(PELang.ADVANCEMENTS_DARK_MATTER_BLOCK_DESCRIPTION, "Stuffing matter together. Because that's a good idea.");
		add(PELang.ADVANCEMENTS_RED_MATTER_BLOCK, "Red and shiny!");
		add(PELang.ADVANCEMENTS_RED_MATTER_BLOCK_DESCRIPTION, "Now you're getting somewhere!");
		add(PELang.ADVANCEMENTS_DARK_MATTER_FURNACE, "Hot matter!");
		add(PELang.ADVANCEMENTS_DARK_MATTER_FURNACE_DESCRIPTION, "A furnace is even better when made from dark matter.");
		add(PELang.ADVANCEMENTS_RED_MATTER_FURNACE, "Even hotter matter!");
		add(PELang.ADVANCEMENTS_RED_MATTER_FURNACE_DESCRIPTION, "Wow, that thing is fast.");
		add(PELang.ADVANCEMENTS_DARK_MATTER_PICKAXE, "Using Matter on Matter");
		add(PELang.ADVANCEMENTS_DARK_MATTER_PICKAXE_DESCRIPTION, "Because why not?");
		add(PELang.ADVANCEMENTS_RED_MATTER_PICKAXE, "Is this thing safe?");
		add(PELang.ADVANCEMENTS_RED_MATTER_PICKAXE_DESCRIPTION, "Probably not.");
		add(PELang.ADVANCEMENTS_KLEIN_STAR, "EMC Batteries");
		add(PELang.ADVANCEMENTS_KLEIN_STAR_DESCRIPTION, "Storing EMC for a rainy day.");
		add(PELang.ADVANCEMENTS_KLEIN_STAR_BIG, "BIG EMC Batteries");
		add(PELang.ADVANCEMENTS_KLEIN_STAR_BIG_DESCRIPTION, "Holding the universe in your pocket.");
	}

	private void addBlocks() {
		add(PEBlocks.ALCHEMICAL_CHEST, "Alchemical Chest");
		add(PEBlocks.INTERDICTION_TORCH, "Interdiction Torch");
		add(PEBlocks.TRANSMUTATION_TABLE, "Transmutation Table");
		add(PEBlocks.CONDENSER, "Energy Condenser");
		add(PEBlocks.CONDENSER_MK2, "Energy Condenser MK2");
		add(PEBlocks.DARK_MATTER_FURNACE, "Dark Matter Furnace");
		add(PEBlocks.RED_MATTER_FURNACE, "Red Matter Furnace");
		add(PEBlocks.DARK_MATTER, "Dark Matter Block");
		add(PEBlocks.RED_MATTER, "Red Matter Block");
		add(PEBlocks.COLLECTOR, "Energy Collector MK1");
		add(PEBlocks.COLLECTOR_MK2, "Energy Collector MK2");
		add(PEBlocks.COLLECTOR_MK3, "Energy Collector MK3");
		add(PEBlocks.RELAY, "Anti-Matter Relay MK1");
		add(PEBlocks.RELAY_MK2, "Anti-Matter Relay MK2");
		add(PEBlocks.RELAY_MK3, "Anti-Matter Relay MK3");
		add(PEBlocks.NOVA_CATALYST, "Nova Catalyst");
		add(PEBlocks.NOVA_CATACLYSM, "Nova Cataclysm");
		add(PEBlocks.ALCHEMICAL_COAL, "Alchemical Coal Block");
		add(PEBlocks.MOBIUS_FUEL, "Mobius Fuel Block");
		add(PEBlocks.AETERNALIS_FUEL, "Aeternalis Fuel Block");
		add(PEBlocks.DARK_MATTER_PEDESTAL, "Dark Matter Pedestal");
	}

	private void addCommands() {
		add(PELang.CLEAR_KNOWLEDGE_NOTIFY, "Your transmutation knowledge was cleared by %s!");
		add(PELang.CLEAR_KNOWLEDGE_SUCCESS, "Cleared knowledge for: %s");
		add(PELang.COMMAND_REMOVE_INVALID_ITEM, "Error: The item or tag \"%s\" was not found!");
		add(PELang.COMMAND_REMOVE_NO_ITEM, "Please give an item or tag to change");
		add(PELang.COMMAND_REMOVE_SUCCESS, "Removed EMC value for %s.");
		add(PELang.COMMAND_RESET_SUCCESS, "Reset EMC value for %s.");
		add(PELang.COMMAND_SET_SUCCESS, "Set EMC value for %s to %s!");
		add(PELang.RELOAD_NOTICE, "Restart or use \"/reload\" when all changes are complete.");
		add(PELang.SHOWBAG_NOT_FOUND, "UUID %s not found in playerdata/");
		add(PELang.SHOWBAG_UUID, "Malformed UUID: %s");
		add(PELang.SHOWBAG_NAMED, "%s (%s)");
	}

	private void addEMC() {
		add(PELang.EMC, "%s EMC");
		add(PELang.EMC_MAX_GEN_RATE, "Maximum Generation Rate: %s EMC/s");
		add(PELang.EMC_MAX_OUTPUT_RATE, "Maximum Output Rate: %s EMC/s");
		add(PELang.EMC_MAX_STORAGE, "Maximum Storage: %s EMC");
		add(PELang.EMC_STORED, "Stored EMC: %s");
		add(PELang.EMC_HAS_KNOWLEDGE, "Transmutable");
		add(PELang.EMC_TOOLTIP, "EMC: %s");
		add(PELang.EMC_STACK_TOOLTIP, "Stack EMC: %s");
		add(PELang.EMC_TOOLTIP_WITH_SELL, "EMC: %s (%s)");
		add(PELang.EMC_STACK_TOOLTIP_WITH_SELL, "Stack EMC: %s (%s)");
		add(PELang.EMC_TOO_MUCH, "WAY TOO MUCH");
		//Postfix names for large EMC values. Names gotten from: https://en.wikipedia.org/wiki/Names_of_large_numbers
		addPostfix(0, "Trillion");
		addPostfix(1, "Quadrillion");
		addPostfix(2, "Quintillion");
		addPostfix(3, "Sextillion");
		addPostfix(4, "Septillion");
		addPostfix(5, "Octillion");
		addPostfix(6, "Nonillion");
		addPostfix(7, "Decillion");
		addPostfix(8, "Undecillion");
		addPostfix(9, "Duodecillion");
		addPostfix(10, "Tredecillion");
		addPostfix(11, "Quattuordecillion");
		addPostfix(12, "Quindecillion");
		addPostfix(13, "Sexdecillion");
		addPostfix(14, "Septendecillion");
		addPostfix(15, "Octodecillion");
		addPostfix(16, "Novemdecillion");
		addPostfix(17, "Vigintillion");
	}

	private void addPostfix(int index, String postfix) {
		add(Util.makeTranslationKey("emc", PECore.rl("postfix." + index)), "%s " + postfix);
	}

	private void addEntityTypes() {
		add(PEEntityTypes.FIRE_PROJECTILE, "Fireball");
		add(PEEntityTypes.HOMING_ARROW, "Homing Arrow");
		add(PEEntityTypes.LAVA_PROJECTILE, "Lava Orb");
		add(PEEntityTypes.LENS_PROJECTILE, "Explosive Lens");
		add(PEEntityTypes.MOB_RANDOMIZER, "Randomizer Orb");
		add(PEEntityTypes.NOVA_CATALYST_PRIMED, "Primed Nova Catalyst");
		add(PEEntityTypes.NOVA_CATACLYSM_PRIMED, "Primed Nova Cataclysm");
		add(PEEntityTypes.SWRG_PROJECTILE, "SWRG Projectile");
		add(PEEntityTypes.WATER_PROJECTILE, "Water Orb");
	}

	private void addItems() {
		add(PEItems.PHILOSOPHERS_STONE, "Philosopher's Stone");
		add(PEItems.REPAIR_TALISMAN, "Repair Talisman");
		add(PEItems.WHITE_ALCHEMICAL_BAG, "Alchemical Bag (White)");
		add(PEItems.ORANGE_ALCHEMICAL_BAG, "Alchemical Bag (Orange)");
		add(PEItems.MAGENTA_ALCHEMICAL_BAG, "Alchemical Bag (Magenta)");
		add(PEItems.LIGHT_BLUE_ALCHEMICAL_BAG, "Alchemical Bag (Light Blue)");
		add(PEItems.YELLOW_ALCHEMICAL_BAG, "Alchemical Bag (Yellow)");
		add(PEItems.LIME_ALCHEMICAL_BAG, "Alchemical Bag (Lime)");
		add(PEItems.PINK_ALCHEMICAL_BAG, "Alchemical Bag (Pink)");
		add(PEItems.GRAY_ALCHEMICAL_BAG, "Alchemical Bag (Gray)");
		add(PEItems.LIGHT_GRAY_ALCHEMICAL_BAG, "Alchemical Bag (Light Gray)");
		add(PEItems.CYAN_ALCHEMICAL_BAG, "Alchemical Bag (Cyan)");
		add(PEItems.PURPLE_ALCHEMICAL_BAG, "Alchemical Bag (Purple)");
		add(PEItems.BLUE_ALCHEMICAL_BAG, "Alchemical Bag (Blue)");
		add(PEItems.BROWN_ALCHEMICAL_BAG, "Alchemical Bag (Brown)");
		add(PEItems.GREEN_ALCHEMICAL_BAG, "Alchemical Bag (Green)");
		add(PEItems.RED_ALCHEMICAL_BAG, "Alchemical Bag (Red)");
		add(PEItems.BLACK_ALCHEMICAL_BAG, "Alchemical Bag (Black)");
		add(PEItems.KLEIN_STAR_EIN, "Klein Star Ein");
		add(PEItems.KLEIN_STAR_ZWEI, "Klein Star Zwei");
		add(PEItems.KLEIN_STAR_DREI, "Klein Star Drei");
		add(PEItems.KLEIN_STAR_VIER, "Klein Star Vier");
		add(PEItems.KLEIN_STAR_SPHERE, "Klein Star Sphere");
		add(PEItems.KLEIN_STAR_OMEGA, "Klein Star Omega");
		add(PEItems.ALCHEMICAL_COAL, "Alchemical Coal");
		add(PEItems.MOBIUS_FUEL, "Mobius Fuel");
		add(PEItems.AETERNALIS_FUEL, "Aeternalis Fuel");
		add(PEItems.LOW_COVALENCE_DUST, "Low Covalence Dust");
		add(PEItems.MEDIUM_COVALENCE_DUST, "Medium Covalence Dust");
		add(PEItems.HIGH_COVALENCE_DUST, "High Covalence Dust");
		add(PEItems.DARK_MATTER, "Dark Matter");
		add(PEItems.RED_MATTER, "Red Matter");
		add(PEItems.IRON_BAND, "Iron Band");
		add(PEItems.BLACK_HOLE_BAND, "Black Hole Band");
		add(PEItems.HARVEST_GODDESS_BAND, "Harvest Goddess Band");
		add(PEItems.ARCHANGEL_SMITE, "Archangel's Smite");
		add(PEItems.IGNITION_RING, "Ignition Ring");
		add(PEItems.BODY_STONE, "Body Stone");
		add(PEItems.SOUL_STONE, "Soul Stone");
		add(PEItems.MIND_STONE, "Mind Stone");
		add(PEItems.LIFE_STONE, "Life Stone");
		add(PEItems.EVERTIDE_AMULET, "Evertide Amulet");
		add(PEItems.VOLCANITE_AMULET, "Volcanite Amulet");
		add(PEItems.SWIFTWOLF_RENDING_GALE, "Swiftwolf's Rending Gale");
		add(PEItems.MERCURIAL_EYE, "Mercurial Eye");
		add(PEItems.WATCH_OF_FLOWING_TIME, "Watch of Flowing Time");
		add(PEItems.GEM_OF_ETERNAL_DENSITY, "Gem of Eternal Density");
		add(PEItems.LOW_DIVINING_ROD, "Divining Rod (low)");
		add(PEItems.MEDIUM_DIVINING_ROD, "Divining Rod (medium)");
		add(PEItems.HIGH_DIVINING_ROD, "Divining Rod (high)");
		add(PEItems.DESTRUCTION_CATALYST, "Destruction Catalyst");
		add(PEItems.HYPERKINETIC_LENS, "Hyperkinetic Lens");
		add(PEItems.CATALYTIC_LENS, "Catalytic Lens");
		add(PEItems.DARK_MATTER_PICKAXE, "Dark Matter Pickaxe");
		add(PEItems.DARK_MATTER_AXE, "Dark Matter Axe");
		add(PEItems.DARK_MATTER_SHOVEL, "Dark Matter Shovel");
		add(PEItems.DARK_MATTER_SWORD, "Dark Matter Sword");
		add(PEItems.DARK_MATTER_HOE, "Dark Matter Hoe");
		add(PEItems.DARK_MATTER_SHEARS, "Dark Matter Shears");
		add(PEItems.DARK_MATTER_HAMMER, "Dark Matter Hammer");
		add(PEItems.RED_MATTER_PICKAXE, "Red Matter Pickaxe");
		add(PEItems.RED_MATTER_AXE, "Red Matter Axe");
		add(PEItems.RED_MATTER_SHOVEL, "Red Matter Shovel");
		add(PEItems.RED_MATTER_SWORD, "Red Matter Sword");
		add(PEItems.RED_MATTER_HOE, "Red Matter Hoe");
		add(PEItems.RED_MATTER_SHEARS, "Red Matter Shears");
		add(PEItems.RED_MATTER_HAMMER, "Red Matter Hammer");
		add(PEItems.RED_MATTER_KATAR, "Red Katar");
		add(PEItems.RED_MATTER_MORNING_STAR, "Red Morningstar");
		add(PEItems.DARK_MATTER_HELMET, "Dark Matter Helmet");
		add(PEItems.DARK_MATTER_CHESTPLATE, "Dark Matter Chestplate");
		add(PEItems.DARK_MATTER_LEGGINGS, "Dark Matter Leggings");
		add(PEItems.DARK_MATTER_BOOTS, "Dark Matter Boots");
		add(PEItems.RED_MATTER_HELMET, "Red Matter Helmet");
		add(PEItems.RED_MATTER_CHESTPLATE, "Red Matter Chestplate");
		add(PEItems.RED_MATTER_LEGGINGS, "Red Matter Leggings");
		add(PEItems.RED_MATTER_BOOTS, "Red Matter Boots");
		add(PEItems.GEM_HELMET, "Gem Helmet");
		add(PEItems.GEM_CHESTPLATE, "Gem Chestplate");
		add(PEItems.GEM_LEGGINGS, "Gem Leggings");
		add(PEItems.GEM_BOOTS, "Gem Boots");
		add(PEItems.ARCANA_RING, "Ring of Arcana");
		add(PEItems.VOID_RING, "Void Ring");
		add(PEItems.ZERO_RING, "Zero Ring");
		add(PEItems.TOME_OF_KNOWLEDGE, "Tome of Knowledge");
		add(PEItems.TRANSMUTATION_TABLET, "Transmutation Tablet");
	}

	private void addModes() {
		add(PELang.CURRENT_MODE, "Mode: %s");
		add(PELang.INVALID_MODE, "Invalid Mode");
		add(PELang.MODE_SWITCH, "Switched to %s Mode");
		add(PELang.MODE_ARCANA_1, "Zero");
		add(PELang.MODE_ARCANA_2, "Ignition");
		add(PELang.MODE_ARCANA_3, "Harvest");
		add(PELang.MODE_ARCANA_4, "SWRG");
		add(PELang.MODE_KATAR_1, "Slay Hostile");
		add(PELang.MODE_KATAR_2, "Slay All");
		add(PELang.MODE_MERCURIAL_EYE_1, "Creation");
		add(PELang.MODE_MERCURIAL_EYE_2, "Extension");
		add(PELang.MODE_MERCURIAL_EYE_3, "Extension-Classic");
		add(PELang.MODE_MERCURIAL_EYE_4, "Transmutation");
		add(PELang.MODE_MERCURIAL_EYE_5, "Transmutation-Classic");
		add(PELang.MODE_MERCURIAL_EYE_6, "Pillar");
		add(PELang.MODE_MORNING_STAR_1, "Standard");
		add(PELang.MODE_MORNING_STAR_2, "3x Tallshot");
		add(PELang.MODE_MORNING_STAR_3, "3x Wideshot");
		add(PELang.MODE_MORNING_STAR_4, "3x Longshot");
		add(PELang.MODE_PHILOSOPHER_1, "Cube");
		add(PELang.MODE_PHILOSOPHER_2, "Panel");
		add(PELang.MODE_PHILOSOPHER_3, "Line");
		add(PELang.MODE_PICK_1, "Standard");
		add(PELang.MODE_PICK_2, "3x Tallshot");
		add(PELang.MODE_PICK_3, "3x Wideshot");
		add(PELang.MODE_PICK_4, "3x Longshot");
		add(PELang.MODE_RED_SWORD_1, "Slay Hostile");
		add(PELang.MODE_RED_SWORD_2, "Slay All");
	}

	private void addPedestalTooltips() {
		add(PELang.PEDESTAL_DISABLED, "Pedestal function has been disabled!");
		add(PELang.PEDESTAL_ON, "On Pedestal:");
		add(PELang.PEDESTAL_TOOLTIP1, "Right click to insert an item, left click to remove.");
		add(PELang.PEDESTAL_TOOLTIP2, "Right click with empty hand to activate!");
		add(PELang.PEDESTAL_ARCHANGEL_1, "Fires arrows at nearby mobs");
		add(PELang.PEDESTAL_ARCHANGEL_2, "Triggers every %s");
		add(PELang.PEDESTAL_BLACK_HOLE_BAND_1, "Sucks in nearby item drops");
		add(PELang.PEDESTAL_BLACK_HOLE_BAND_2, "Dumps in adjacent inventories");
		add(PELang.PEDESTAL_BODY_STONE_1, "Restores nearby players' hunger");
		add(PELang.PEDESTAL_BODY_STONE_2, "Half a shank every %s");
		add(PELang.PEDESTAL_EVERTIDE_1, "Create rain/snow storms");
		add(PELang.PEDESTAL_EVERTIDE_2, "Attempts to start rain every %s");
		add(PELang.PEDESTAL_HARVEST_GODDESS_1, "Accelerates growth of nearby crops");
		add(PELang.PEDESTAL_HARVEST_GODDESS_2, "Harvests nearby grown crops");
		add(PELang.PEDESTAL_HARVEST_GODDESS_3, "Activates every %s");
		add(PELang.PEDESTAL_IGNITION_1, "Nearby mobs combust");
		add(PELang.PEDESTAL_IGNITION_2, "Activates every %s");
		add(PELang.PEDESTAL_LIFE_STONE_1, "Restores both hunger and hearts");
		add(PELang.PEDESTAL_LIFE_STONE_2, "Half a heart and shank every %s");
		add(PELang.PEDESTAL_MIND_STONE, "Sucks nearby XP orbs into the Mind Stone");
		add(PELang.PEDESTAL_REPAIR_TALISMAN_1, "Repairs nearby players' items");
		add(PELang.PEDESTAL_REPAIR_TALISMAN_2, "Restores 1 durability every %s");
		add(PELang.PEDESTAL_SOUL_STONE_1, "Heals nearby players");
		add(PELang.PEDESTAL_SOUL_STONE_2, "Half a heart every %s");
		add(PELang.PEDESTAL_SWRG_1, "Shoots lightning at nearby mobs");
		add(PELang.PEDESTAL_SWRG_2, "Activates every %s");
		add(PELang.PEDESTAL_TIME_WATCH_1, "Gives %s bonus ticks to nearby blocks every tick");
		add(PELang.PEDESTAL_TIME_WATCH_2, "Each tick, nearby mobs move %s times the speed");
		add(PELang.PEDESTAL_VOLCANITE_1, "Prevents rain/snow storms");
		add(PELang.PEDESTAL_VOLCANITE_2, "Attempts to stop weather every %s");
		add(PELang.PEDESTAL_ZERO_1, "Extinguishes entities");
		add(PELang.PEDESTAL_ZERO_2, "Freezes surroundings");
		add(PELang.PEDESTAL_ZERO_3, "Activates every %s");
	}

	private void addSubtitles() {
		//TODO: Improve on these if someone has better ideas for the subtitles
		add(PESoundEvents.WIND_MAGIC, "Wind Magic");
		add(PESoundEvents.WATER_MAGIC, "Water Magic");
		add(PESoundEvents.POWER, "Device Powered");
		add(PESoundEvents.HEAL, "Healing Performed");
		add(PESoundEvents.DESTRUCT, "Destruction");
		add(PESoundEvents.CHARGE, "Device Charged");
		add(PESoundEvents.UNCHARGE, "Device Uncharged");
		add(PESoundEvents.TRANSMUTE, "Block Transmuted");
	}

	private void addTooltips() {
		add(PELang.TOOLTIP_ARCANA_INACTIVE, "Inactive!");
		add(PELang.TOOLTIP_EVERTIDE_1, "Press %s to fire a water projectile");
		add(PELang.TOOLTIP_EVERTIDE_2, "Acts as an infinite water bucket");
		add(PELang.TOOLTIP_EVERTIDE_3, "Right click to fill tanks and cauldrons");
		add(PELang.TOOLTIP_EVERTIDE_4, "All operations are completely free!");
		add(PELang.TOOLTIP_GEM_DENSITY_1, "Condenses items on the go");
		add(PELang.TOOLTIP_GEM_DENSITY_2, "Current target: %s");
		add(PELang.TOOLTIP_GEM_DENSITY_3, "Press %s to change target");
		add(PELang.TOOLTIP_GEM_DENSITY_4, "Right click to set up blacklist/whitelist");
		add(PELang.TOOLTIP_GEM_DENSITY_5, "Shift right click to toggle");
		add(PELang.TOOLTIP_PHILOSTONE, "Press %s to open a crafting grid");
		add(PELang.TOOLTIP_STORED_XP, "Stored XP: %s");
		add(PELang.TOOLTIP_TIME_WATCH_1, "Become the master of time");
		add(PELang.TOOLTIP_TIME_WATCH_2, "Right click to change mode");
		add(PELang.TOOLTIP_TOME, "Unlocks all transmutation knowledge when learned");
		add(PELang.TOOLTIP_VOLCANITE_1, "Press %s to fire a lava projectile");
		add(PELang.TOOLTIP_VOLCANITE_2, "Acts as infinitely full lava bucket");
		add(PELang.TOOLTIP_VOLCANITE_3, "Right click to fill tanks");
		add(PELang.TOOLTIP_VOLCANITE_4, "All operations cost 32 EMC!");
	}

	private void addTransmutation() {
		add(PELang.TRANSMUTATION_TRANSMUTE, "Transmutation");
		add(PELang.TRANSMUTATION_LEARNED_1, "L");
		add(PELang.TRANSMUTATION_LEARNED_2, "e");
		add(PELang.TRANSMUTATION_LEARNED_3, "a");
		add(PELang.TRANSMUTATION_LEARNED_4, "r");
		add(PELang.TRANSMUTATION_LEARNED_5, "n");
		add(PELang.TRANSMUTATION_LEARNED_6, "e");
		add(PELang.TRANSMUTATION_LEARNED_7, "d");
		add(PELang.TRANSMUTATION_LEARNED_8, "!");
		add(PELang.TRANSMUTATION_UNLEARNED_1, "U");
		add(PELang.TRANSMUTATION_UNLEARNED_2, "n");
		add(PELang.TRANSMUTATION_UNLEARNED_3, "l");
		add(PELang.TRANSMUTATION_UNLEARNED_4, "e");
		add(PELang.TRANSMUTATION_UNLEARNED_5, "a");
		add(PELang.TRANSMUTATION_UNLEARNED_6, "r");
		add(PELang.TRANSMUTATION_UNLEARNED_7, "n");
		add(PELang.TRANSMUTATION_UNLEARNED_8, "e");
		add(PELang.TRANSMUTATION_UNLEARNED_9, "d");
	}
}