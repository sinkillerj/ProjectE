package moze_intel.projecte.client.lang;

import moze_intel.projecte.PECore;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.integration.jade.PEJadeConstants;
import moze_intel.projecte.integration.recipe_viewer.alias.ProjectEAliases;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class PELangProvider extends BaseLanguageProvider {

	public PELangProvider(PackOutput output) {
		super(output, PECore.MODID);
	}

	@Override
	protected void addTranslations() {
		addAdvancements();
		addAliases(ProjectEAliases.values());
		addBlocks();
		addCommands();
		addConfigs();
		addEMC();
		addEntityTypes();
		addTags();
		addItems();
		addModes();
		addPedestalTooltips();
		addSubtitles();
		addTooltips();
		addTransmutation();
		//Misc stuff
		add(PELang.PROJECTE, PECore.MODNAME);
		add(PELang.PACK_DESCRIPTION, "Resources used for " + PECore.MODNAME);
		addModInfo(PECore.MODNAME, "A complete rewrite of EE2 for modern Minecraft versions.");
		add(PELang.SECONDS, "%1$s seconds");
		add(PELang.EVERY_TICK, "%1$s seconds (every tick)");
		add(PELang.HIGH_ALCHEMIST, "High alchemist %1$s has joined the server");
		add(PELang.UPDATE_AVAILABLE, "New " + PECore.MODNAME + " update available! Version: %1$s");
		add(PELang.UPDATE_GET_IT, "Get it here!");
		add(PELang.BLACKLIST, "Blacklist");
		add(PELang.WHITELIST, "Whitelist");
		add(PELang.DENSITY_MODE_TARGET, "Set target to: %1$s");
		//Divining Rod
		add(PELang.DIVINING_AVG_EMC, "Average EMC for %1$s blocks: %2$s");
		add(PELang.DIVINING_MAX_EMC, "Max EMC: %1$s");
		add(PELang.DIVINING_SECOND_MAX, "Second Max EMC: %1$s");
		add(PELang.DIVINING_THIRD_MAX, "Third Max EMC: %1$s");
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
		add(PELang.WORLD_TRANSMUTE_DESCRIPTION, "Use item (%1$s) in world, hold %2$s while interacting for alternate output");
		//Curios
		add(PELang.CURIOS_KLEIN_STAR, "Klein Star");
		//Gem Armor
		add(PELang.GEM_ENABLED, "ENABLED");
		add(PELang.GEM_DISABLED, "DISABLED");
		add(PELang.GEM_ACTIVATE, "Activated Gem Armor Offensive Abilities");
		add(PELang.GEM_DEACTIVATE, "Deactivated Gem Armor Offensive Abilities");
		add(PELang.NIGHT_VISION, "Night Vision: %1$s");
		add(PELang.NIGHT_VISION_PROMPT, "Press %1$s to toggle Night Vision");
		add(PELang.STEP_ASSIST, "Step Assist: %1$s");
		add(PELang.STEP_ASSIST_PROMPT, "Press %1$s to toggle Step Assist");
		add(PELang.GEM_LORE_HELM, "Abyss Helmet");
		add(PELang.GEM_LORE_CHEST, "Infernal Armor");
		add(PELang.GEM_LORE_LEGS, "Gravity Greaves");
		add(PELang.GEM_LORE_FEET, "Hurricane Boots");
		//Watch of Flowing Time
		add(PELang.TIME_WATCH_DISABLED, "Item disabled by server admin");
		add(PELang.TIME_WATCH_MODE, "Time control mode: %1$s");
		add(PELang.TIME_WATCH_MODE_SWITCH, "Time control mode set to: %1$s");
		add(PELang.TIME_WATCH_OFF, "Off");
		add(PELang.TIME_WATCH_FAST_FORWARD, "Fast-Forward");
		add(PELang.TIME_WATCH_REWIND, "Rewind");
		//GUI
		add(PELang.GUI_DARK_MATTER_FURNACE, "DM Furnace");
		add(PELang.GUI_RED_MATTER_FURNACE, "RM Furnace");
		add(PELang.GUI_RELAY_MK1, "Relay MKI");
		add(PELang.GUI_RELAY_MK2, "Relay MKII");
		add(PELang.GUI_RELAY_MK3, "Relay MKIII");
		//Jade config strings
		addJadeConfigTooltip(PEJadeConstants.EMC_PROVIDER, "EMC Provider");
	}

	private void addJadeConfigTooltip(ResourceLocation location, String value) {
		add("config.jade.plugin_" + location.getNamespace() + "." + location.getPath(), value);
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
		add(PELang.CLEAR_KNOWLEDGE_NOTIFY, "Your transmutation knowledge was cleared by %1$s!");
		add(PELang.CLEAR_KNOWLEDGE_SUCCESS, "Cleared knowledge for: %1$s");
		add(PELang.DUMP_MISSING_EMC_NONE_MISSING, "All Items have an EMC value.");
		add(PELang.DUMP_MISSING_EMC_ONE_MISSING, "One Item is missing an EMC value, printing to server log.");
		add(PELang.DUMP_MISSING_EMC_MULTIPLE_MISSING, "%1$s Items are missing an EMC value, printing to client log.");
		add(PELang.COMMAND_INVALID_ITEM, "Error: The item or tag \"%1$s\" was not found!");
		add(PELang.COMMAND_NO_ITEM, "Please give an item or tag to change");
		add(PELang.COMMAND_REMOVE_SUCCESS, "Removed EMC value for %1$s.");
		add(PELang.COMMAND_RESET_SUCCESS, "Reset EMC value for %1$s.");
		add(PELang.COMMAND_SET_SUCCESS, "Set EMC value for %1$s to %2$s!");
		add(PELang.COMMAND_EMC_INVALID, "The value \"%1$s\" is invalid, it must be a positive integer.");
		add(PELang.COMMAND_EMC_NEGATIVE, "Cannot remove %1$s EMC from %2$s as this would make their EMC negative.");
		add(PELang.COMMAND_EMC_ADD_SUCCESS, "Added %1$s EMC to %2$s.");
		add(PELang.COMMAND_EMC_REMOVE_SUCCESS, "Removed %1$s EMC from %2$s.");
		add(PELang.COMMAND_EMC_SET_SUCCESS, "Set the EMC of %1$s to %2$s.");
		add(PELang.COMMAND_EMC_TEST_SUCCESS, "%1$s does have enough EMC to remove %2$s.");
		add(PELang.COMMAND_EMC_TEST_FAIL, "%1$s does not have enough EMC to remove %2$s.");
		add(PELang.COMMAND_EMC_GET_SUCCESS, "%1$s has %2$s EMC.");
		add(PELang.COMMAND_KNOWLEDGE_INVALID, "The item \"%1$s\" does not  have an EMC value, and cannot be learned.");
		add(PELang.COMMAND_KNOWLEDGE_CLEAR_SUCCESS, "Successfully cleared the knowledge of %1$s.");
		add(PELang.COMMAND_KNOWLEDGE_CLEAR_FAIL, "%1$s does not have any knowledge to clear.");
		add(PELang.COMMAND_KNOWLEDGE_LEARN_SUCCESS, "%1$s has successfully learned %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_LEARN_FAIL, "%1$s already has knowledge of %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_UNLEARN_SUCCESS, "%1$s has successfully unlearned %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_UNLEARN_FAIL, "%1$s does not have knowledge of %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_TEST_SUCCESS, "%1$s has knowledge of %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_TEST_FAIL, "%1$s does not have knowledge of %2$s.");
		add(PELang.COMMAND_PROVIDER_FAIL, "Failed to get provider for %1$s.");
		add(PELang.RELOAD_NOTICE, "Restart or use \"/reload\" when all changes are complete.");
		add(PELang.SHOWBAG_NOT_FOUND, "UUID %1$s not found in playerdata/");
		add(PELang.SHOWBAG_NAMED, "%1$s (%2$s)");
	}

	private void addConfigs() {
		addConfigs(ProjectEConfig.getConfigs());
		addConfigs(PEConfigTranslations.values());
	}

	private void addEMC() {
		add(PELang.EMC, "%1$s EMC");
		add(PELang.EMC_MAX_GEN_RATE, "Maximum Generation Rate: %1$s EMC/t");
		add(PELang.EMC_MAX_OUTPUT_RATE, "Maximum Output Rate: %1$s EMC/t");
		add(PELang.EMC_MAX_STORAGE, "Maximum Storage: %1$s EMC");
		add(PELang.EMC_STORED, "Stored EMC: %1$s");
		add(PELang.EMC_HAS_KNOWLEDGE, "Learned");
		add(PELang.EMC_NO_KNOWLEDGE, "Unlearned");
		add(PELang.EMC_TOOLTIP, "EMC: %1$s");
		add(PELang.EMC_STACK_TOOLTIP, "Stack EMC: %1$s");
		add(PELang.EMC_TOOLTIP_WITH_SELL, "EMC: %1$s (%2$s)");
		add(PELang.EMC_STACK_TOOLTIP_WITH_SELL, "Stack EMC: %1$s (%2$s)");
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
		add(Util.makeDescriptionId("emc", PECore.rl("postfix." + index)), "%1$s " + postfix);
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

	private void addTags() {
		add(PETags.Items.CURIOS_KLEIN_STAR, "Klein Stars");
		add(PETags.Items.ALCHEMICAL_BAGS, "Alchemical Bags");
		add(PETags.Items.COLLECTOR_FUEL, "Collector Fuels");
		add(PETags.Items.DATA_COMPONENT_WHITELIST, "Data Component Whitelist");
		add(PETags.Items.COVALENCE_DUST, "Covalence Dusts");
		add(PETags.Items.IGNORE_MISSING_EMC, "Ignore Missing EMC");
		add(PETags.Items.PLANTABLE_SEEDS, "Plantable Seeds");
		add(PETags.Items.KLEIN_STARS, "Klein Stars");
		add(PETags.Items.COLLECTORS, "Energy Collectors");
		add(PETags.Items.RELAYS, "Anti-Matter Relays");
		add(PETags.Items.MATTER_FURNACES, "Matter Furnaces");
		add(PETags.Items.TOOLS_HAMMERS, "Hammers");
		add(PETags.Items.TOOLS_KATARS, "Katars");
		add(PETags.Items.TOOLS_MORNING_STARS, "Morning Stars");
		
		add(PETags.Blocks.BLACKLIST_HARVEST, "Harvest Band Blacklist");
		add(PETags.Blocks.OVERRIDE_PLANTABLE, "Harvest Band Plantable Override");
		add(PETags.Blocks.BLACKLIST_TIME_WATCH, "Time Watch Blacklist");
		add(PETags.Blocks.VEIN_SHOVEL, "Valid Matter Shovel Vein");
		add(PETags.Blocks.FARMING_OVERRIDE, "Farming Override");
		add(PETags.Blocks.NEEDS_DARK_MATTER_TOOL, "Needs Dark Matter Tools");
		add(PETags.Blocks.NEEDS_RED_MATTER_TOOL, "Needs Red Matter Tools");
		add(PETags.Blocks.INCORRECT_FOR_DARK_MATTER_TOOL, "Alchemical");
		add(PETags.Blocks.INCORRECT_FOR_RED_MATTER_TOOL, "Alchemical");
		add(PETags.Blocks.MINEABLE_WITH_PE_KATAR, PECore.MODNAME + " Katar Mineable");
		add(PETags.Blocks.MINEABLE_WITH_PE_HAMMER, PECore.MODNAME + " Hammer Mineable");
		add(PETags.Blocks.MINEABLE_WITH_PE_MORNING_STAR, PECore.MODNAME + " Morning Star Mineable");
		add(PETags.Blocks.MINEABLE_WITH_PE_SHEARS, PECore.MODNAME + " Shears Mineable");
		add(PETags.Blocks.MINEABLE_WITH_PE_SWORD, PECore.MODNAME + " Sword Mineable");
		add(PETags.Blocks.MINEABLE_WITH_HAMMER, "Hammer Mineable");
		add(PETags.Blocks.MINEABLE_WITH_KATAR, "Katar Mineable");
		add(PETags.Blocks.MINEABLE_WITH_MORNING_STAR, "Morning Star Mineable");

		add(PETags.Entities.BLACKLIST_SWRG, "SWRG Blacklist");
		add(PETags.Entities.BLACKLIST_INTERDICTION, "Interdiction Blacklist");
		add(PETags.Entities.RANDOMIZER_PEACEFUL, "Peaceful Randomizer Mobs");
		add(PETags.Entities.RANDOMIZER_HOSTILE, "Hostile Randomizer Mobs");

		add(PETags.BlockEntities.BLACKLIST_TIME_WATCH, "Time Watch Blacklist");
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
		add(PELang.CURRENT_MODE, "Mode: %1$s");
		add(PELang.MODE_SWITCH, "Switched to %1$s Mode");
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
		add(PELang.MODE_PHILOSOPHER_1, "Cube");
		add(PELang.MODE_PHILOSOPHER_2, "Panel");
		add(PELang.MODE_PHILOSOPHER_3, "Line");
		add(PELang.MODE_PICK_1, "Standard");
		add(PELang.MODE_PICK_2, "3x Tallshot");
		add(PELang.MODE_PICK_3, "3x Wideshot");
		add(PELang.MODE_PICK_4, "3x Longshot");
	}

	private void addPedestalTooltips() {
		add(PELang.PEDESTAL_DISABLED, "Pedestal function has been disabled!");
		add(PELang.PEDESTAL_ON, "On Pedestal:");
		add(PELang.PEDESTAL_TOOLTIP1, "Interact (%1$s) with to insert an item, %2$s to remove.");
		add(PELang.PEDESTAL_TOOLTIP2, "Interact (%1$s) with an empty hand to activate!");
		add(PELang.PEDESTAL_ARCHANGEL_1, "Fires arrows at nearby mobs");
		add(PELang.PEDESTAL_ARCHANGEL_2, "Triggers every %1$s");
		add(PELang.PEDESTAL_BLACK_HOLE_BAND_1, "Sucks in nearby item drops");
		add(PELang.PEDESTAL_BLACK_HOLE_BAND_2, "Dumps in adjacent inventories");
		add(PELang.PEDESTAL_BODY_STONE_1, "Restores nearby players' hunger");
		add(PELang.PEDESTAL_BODY_STONE_2, "Half a shank every %1$s");
		add(PELang.PEDESTAL_EVERTIDE_1, "Create rain/snow storms");
		add(PELang.PEDESTAL_EVERTIDE_2, "Attempts to start rain every %1$s");
		add(PELang.PEDESTAL_HARVEST_GODDESS_1, "Accelerates growth of nearby crops");
		add(PELang.PEDESTAL_HARVEST_GODDESS_2, "Harvests nearby grown crops");
		add(PELang.PEDESTAL_HARVEST_GODDESS_3, "Activates every %1$s");
		add(PELang.PEDESTAL_IGNITION_1, "Nearby mobs combust");
		add(PELang.PEDESTAL_IGNITION_2, "Activates every %1$s");
		add(PELang.PEDESTAL_LIFE_STONE_1, "Restores both hunger and hearts");
		add(PELang.PEDESTAL_LIFE_STONE_2, "Half a heart and shank every %1$s");
		add(PELang.PEDESTAL_MIND_STONE, "Sucks nearby XP orbs into the Mind Stone");
		add(PELang.PEDESTAL_REPAIR_TALISMAN_1, "Repairs nearby players' items");
		add(PELang.PEDESTAL_REPAIR_TALISMAN_2, "Restores 1 durability every %1$s");
		add(PELang.PEDESTAL_SOUL_STONE_1, "Heals nearby players");
		add(PELang.PEDESTAL_SOUL_STONE_2, "Half a heart every %1$s");
		add(PELang.PEDESTAL_SWRG_1, "Shoots lightning at nearby mobs");
		add(PELang.PEDESTAL_SWRG_2, "Activates every %1$s");
		add(PELang.PEDESTAL_TIME_WATCH_1, "Gives %1$s bonus ticks to nearby blocks every tick");
		add(PELang.PEDESTAL_TIME_WATCH_2, "Each tick, nearby mobs move %1$s times the speed");
		add(PELang.PEDESTAL_VOLCANITE_1, "Prevents rain/snow storms");
		add(PELang.PEDESTAL_VOLCANITE_2, "Attempts to stop weather every %1$s");
		add(PELang.PEDESTAL_ZERO_1, "Extinguishes entities");
		add(PELang.PEDESTAL_ZERO_2, "Freezes surroundings");
		add(PELang.PEDESTAL_ZERO_3, "Activates every %1$s");
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
		add(PELang.TOOLTIP_EVERTIDE_1, "Press %1$s to fire a water projectile");
		add(PELang.TOOLTIP_EVERTIDE_2, "Acts as an infinite water bucket");
		add(PELang.TOOLTIP_EVERTIDE_3, "Interact with tanks and cauldrons to fill them");
		add(PELang.TOOLTIP_EVERTIDE_4, "All operations are completely free!");
		add(PELang.TOOLTIP_GEM_DENSITY_1, "Condenses items on the go");
		add(PELang.TOOLTIP_GEM_DENSITY_2, "Current target: %1$s");
		add(PELang.TOOLTIP_GEM_DENSITY_3, "Press %1$s to change target");
		add(PELang.TOOLTIP_GEM_DENSITY_4, "Interact (%1$s) to set up blacklist/whitelist");
		add(PELang.TOOLTIP_GEM_DENSITY_5, "Hold %1$s and interact (%2$s) with the air to toggle");
		add(PELang.TOOLTIP_PHILOSTONE, "Press %1$s to open a crafting grid");
		add(PELang.TOOLTIP_STORED_XP, "Stored XP: %1$s");
		add(PELang.TOOLTIP_TIME_WATCH_1, "Become the master of time");
		add(PELang.TOOLTIP_TIME_WATCH_2, "Interact (%1$s) to change mode");
		add(PELang.TOOLTIP_TOME, "Unlocks all transmutation knowledge when learned");
		add(PELang.TOOLTIP_VOLCANITE_1, "Press %1$s to fire a lava projectile");
		add(PELang.TOOLTIP_VOLCANITE_2, "Acts as infinitely full lava bucket");
		add(PELang.TOOLTIP_VOLCANITE_3, "Interact with tanks and cauldrons to fill them");
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