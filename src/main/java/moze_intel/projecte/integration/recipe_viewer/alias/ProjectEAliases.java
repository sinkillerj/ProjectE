package moze_intel.projecte.integration.recipe_viewer.alias;

import moze_intel.projecte.PECore;
import net.minecraft.util.Util;

public enum ProjectEAliases implements IAliasedTranslation {
	AUTO_FEEDER("auto.feeder", "Auto-Feeder"),
	AUTO_HEALER("auto.healer", "Auto-Healer"),
	BUILDING_WAND("building_wand", "Building Wand"),
	EXPLOSIVE("explosive", "Explosive"),
	FIRE_EXTINGUISHER("fire.extinguisher", "Fire Extinguisher"),
	FIRE_PROTECTION("protection.fire", "Fire Protection"),
	FIRE_STARTER("fire.starter", "Fire Starter"),
	FREEZE("freeze", "Freeze"),
	ITEM_REPAIR("item_repair", "Item Repair"),
	LIGHTNING("lightning", "Lightning"),
	MAGNET("magnet", "Magnet"),
	NIGHT_VISION("night_vision", "Night Vision"),
	PLANT_ACCELERATOR("accelerator.plant", "Plant Accelerator"),
	PLANT_GROWER("grower.plant", "Plant Grower"),
	RING_BASE("ring.base", "Ring Base"),
	TICK_ACCELERATOR("accelerator.tick", "Tick Accelerator"),
	TIME_CONTROL("control.time", "Time Control"),
	WEATHER_CONTROL("control.weather", "Weather Control"),
	WORD_TRANSMUTATION("transmutation", "Word Transmutation"),
	//Condensing
	CONDENSER_ITEMS("condenser.items", "Item Condenser"),
	CONDENSER_MATTER("condenser.matter", "Matter Condenser"),
	//Emc
	EMC_CHARGER("emc.charger", "EMC Charger"),
	EMC_DETECTOR("emc.detector", "EMC Detector"),
	EMC_GENERATOR("emc.generator", "EMC Generator"),
	EMC_TRANSFER("emc.transfer", "EMC Transfer"),
	//Storage Block
	BLOCK_ALCHEMICAL_COAL("block.alchemical_coal", "Block of Alchemical Coal"),
	BLOCK_MOBIUS_FUEL("block.mobius_fuel", "Block of Mobius Fuel"),
	BLOCK_AETERNALIS_FUEL("block.aeternalis_fuel", "Block of Aeternalis Fuel"),
	BLOCK_DARK_MATTER("block.dark_matter", "Block of Dark Matter"),
	BLOCK_RED_MATTER("block.red_matter", "Block of Red Matter"),
	//Portability
	PORTABLE_CRAFTING_TABLE("crafting.portable", "Portable Crafting Table"),
	PORTABLE_WORKBENCH("crafting.portable.workbench", "Portable Workbench"),
	PORTABLE_TRANSMUTATION("transmutation.portable", "Portable Transmutation"),
	//Infinite Resources
	INFINITE_LAVA("infinite.lava", "Infinite Lava"),
	INFINITE_WATER("infinite.water", "Infinite Water"),
	//Flight
	CREATIVE_FLIGHT("flight.creative", "Creative Flight"),
	//Gear
	MOVEMENT_SPEED("speed.movement", "Movement Speed"),
	AUTO_STEP("auto_step", "Auto-Step"),
	STEP_ASSIST("step_assist", "Step Assist"),
	LAVA_WALKING("walking.lava", "Lava Walking"),
	WATER_WALKING("walking.water", "Water Walking"),
	//Tools
	TOOL_AXE("tool.axe", "Axe"),
	TOOL_HOE("tool.hoe", "Hoe"),
	TOOL_PICKAXE("tool.pickaxe", "Pickaxe"),
	TOOL_SHOVEL("tool.shovel", "Shovel"),
	TOOL_SWORD("tool.sword", "Sword"),
	TOOL_RANGED("tool.ranged", "Ranged"),
	TOOL_WEAPON("tool.weapon", "Weapon"),
	TOOL_HAMMER("tool.hammer", "Hammer"),
	TOOL_SHEARS("tool.shears", "Shears"),
	//AOE
	AOE("aoe", "AOE"),
	AOE_LONG("aoe.long", "Area of Effect"),
	//Mob effecting
	REPEL_MOB("repel.mob", "Repel Mobs"),
	REPEL_HOSTILE("repel.hostile", "Repel Hostile"),
	REPEL_PROJECTILE("repel.projectile", "Repel Projectile"),
	SLOW_HOSTILE("slow.hostile", "Slow Hostile"),
	SLOW_MOBS("slow.mobs", "Slow Mobs"),
	//Storage
	BACKPACK("storage.backpack", "Backpack"),
	STORAGE_PORTABLE("storage.portable", "Portable Storage"),
	ITEM_STORAGE("storage.item", "Item Storage"),
	EMC_STORAGE("storage.emc", "EMC Storage"),
	EMC_BATTERY("emc.battery", "EMC Battery"),
	XP_STORAGE("storage.xp", "Experience Storage"),
	//Teleportation
	SELF_TELEPORTER("teleporter", "Self-Teleporter"),
	TELEPORATION("teleporation", "Teleporation"),
	//Voiding
	VOID_FLUID("fluid.void", "Void Fluid"),
	FLUID_REMOVER("fluid.remover", "Fluid Remover"),
	;

	private final String key;
	private final String alias;

	ProjectEAliases(String path, String alias) {
		this.key = Util.makeDescriptionId("alias", PECore.rl(path));
		this.alias = alias;
	}

	@Override
	public String getTranslationKey() {
		return key;
	}

	@Override
	public String getAlias() {
		return alias;
	}
}
