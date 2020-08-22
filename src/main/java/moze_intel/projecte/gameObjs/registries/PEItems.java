package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.gameObjs.EnumFuelType;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.items.AlchemicalFuel;
import moze_intel.projecte.gameObjs.items.CataliticLens;
import moze_intel.projecte.gameObjs.items.DestructionCatalyst;
import moze_intel.projecte.gameObjs.items.DiviningRod;
import moze_intel.projecte.gameObjs.items.EvertideAmulet;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.gameObjs.items.HyperkineticLens;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.items.KleinStar.EnumKleinTier;
import moze_intel.projecte.gameObjs.items.MercurialEye;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.gameObjs.items.RepairTalisman;
import moze_intel.projecte.gameObjs.items.Tome;
import moze_intel.projecte.gameObjs.items.TransmutationTablet;
import moze_intel.projecte.gameObjs.items.VolcaniteAmulet;
import moze_intel.projecte.gameObjs.items.armor.DMArmor;
import moze_intel.projecte.gameObjs.items.armor.GemChest;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.gameObjs.items.armor.GemHelmet;
import moze_intel.projecte.gameObjs.items.armor.GemLegs;
import moze_intel.projecte.gameObjs.items.armor.RMArmor;
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
import moze_intel.projecte.gameObjs.items.rings.TimeWatch;
import moze_intel.projecte.gameObjs.items.rings.VoidRing;
import moze_intel.projecte.gameObjs.items.rings.Zero;
import moze_intel.projecte.gameObjs.items.tools.PEAxe;
import moze_intel.projecte.gameObjs.items.tools.PEHammer;
import moze_intel.projecte.gameObjs.items.tools.PEHoe;
import moze_intel.projecte.gameObjs.items.tools.PEKatar;
import moze_intel.projecte.gameObjs.items.tools.PEMorningStar;
import moze_intel.projecte.gameObjs.items.tools.PEPickaxe;
import moze_intel.projecte.gameObjs.items.tools.PEShears;
import moze_intel.projecte.gameObjs.items.tools.PEShovel;
import moze_intel.projecte.gameObjs.items.tools.PESword;
import moze_intel.projecte.gameObjs.items.tools.RedMatterSword;
import moze_intel.projecte.gameObjs.registration.impl.ItemDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

public class PEItems {

	public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister();

	public static final ItemRegistryObject<PhilosophersStone> PHILOSOPHERS_STONE = ITEMS.registerNoStack("philosophers_stone", PhilosophersStone::new);
	public static final ItemRegistryObject<RepairTalisman> REPAIR_TALISMAN = ITEMS.registerNoStack("repair_talisman", RepairTalisman::new);

	public static final ItemRegistryObject<AlchemicalBag> WHITE_ALCHEMICAL_BAG = registerBag(DyeColor.WHITE);
	public static final ItemRegistryObject<AlchemicalBag> ORANGE_ALCHEMICAL_BAG = registerBag(DyeColor.ORANGE);
	public static final ItemRegistryObject<AlchemicalBag> MAGENTA_ALCHEMICAL_BAG = registerBag(DyeColor.MAGENTA);
	public static final ItemRegistryObject<AlchemicalBag> LIGHT_BLUE_ALCHEMICAL_BAG = registerBag(DyeColor.LIGHT_BLUE);
	public static final ItemRegistryObject<AlchemicalBag> YELLOW_ALCHEMICAL_BAG = registerBag(DyeColor.YELLOW);
	public static final ItemRegistryObject<AlchemicalBag> LIME_ALCHEMICAL_BAG = registerBag(DyeColor.LIME);
	public static final ItemRegistryObject<AlchemicalBag> PINK_ALCHEMICAL_BAG = registerBag(DyeColor.PINK);
	public static final ItemRegistryObject<AlchemicalBag> GRAY_ALCHEMICAL_BAG = registerBag(DyeColor.GRAY);
	public static final ItemRegistryObject<AlchemicalBag> LIGHT_GRAY_ALCHEMICAL_BAG = registerBag(DyeColor.LIGHT_GRAY);
	public static final ItemRegistryObject<AlchemicalBag> CYAN_ALCHEMICAL_BAG = registerBag(DyeColor.CYAN);
	public static final ItemRegistryObject<AlchemicalBag> PURPLE_ALCHEMICAL_BAG = registerBag(DyeColor.PURPLE);
	public static final ItemRegistryObject<AlchemicalBag> BLUE_ALCHEMICAL_BAG = registerBag(DyeColor.BLUE);
	public static final ItemRegistryObject<AlchemicalBag> BROWN_ALCHEMICAL_BAG = registerBag(DyeColor.BROWN);
	public static final ItemRegistryObject<AlchemicalBag> GREEN_ALCHEMICAL_BAG = registerBag(DyeColor.GREEN);
	public static final ItemRegistryObject<AlchemicalBag> RED_ALCHEMICAL_BAG = registerBag(DyeColor.RED);
	public static final ItemRegistryObject<AlchemicalBag> BLACK_ALCHEMICAL_BAG = registerBag(DyeColor.BLACK);

	public static final ItemRegistryObject<KleinStar> KLEIN_STAR_EIN = registerKleinStar(EnumKleinTier.EIN);
	public static final ItemRegistryObject<KleinStar> KLEIN_STAR_ZWEI = registerKleinStar(EnumKleinTier.ZWEI);
	public static final ItemRegistryObject<KleinStar> KLEIN_STAR_DREI = registerKleinStar(EnumKleinTier.DREI);
	public static final ItemRegistryObject<KleinStar> KLEIN_STAR_VIER = registerKleinStar(EnumKleinTier.VIER);
	public static final ItemRegistryObject<KleinStar> KLEIN_STAR_SPHERE = registerKleinStar(EnumKleinTier.SPHERE);
	public static final ItemRegistryObject<KleinStar> KLEIN_STAR_OMEGA = registerKleinStar(EnumKleinTier.OMEGA);

	public static final ItemRegistryObject<AlchemicalFuel> ALCHEMICAL_COAL = registerAlchemicalFuel(EnumFuelType.ALCHEMICAL_COAL);
	public static final ItemRegistryObject<AlchemicalFuel> MOBIUS_FUEL = registerAlchemicalFuel(EnumFuelType.MOBIUS_FUEL);
	public static final ItemRegistryObject<AlchemicalFuel> AETERNALIS_FUEL = registerAlchemicalFuel(EnumFuelType.AETERNALIS_FUEL);

	public static final ItemRegistryObject<Item> LOW_COVALENCE_DUST = ITEMS.register("low_covalence_dust");
	public static final ItemRegistryObject<Item> MEDIUM_COVALENCE_DUST = ITEMS.register("medium_covalence_dust");
	public static final ItemRegistryObject<Item> HIGH_COVALENCE_DUST = ITEMS.register("high_covalence_dust");
	public static final ItemRegistryObject<Item> DARK_MATTER = ITEMS.register("dark_matter");
	public static final ItemRegistryObject<Item> RED_MATTER = ITEMS.register("red_matter");

	public static final ItemRegistryObject<PEPickaxe> DARK_MATTER_PICKAXE = ITEMS.registerNoStack("dm_pick", properties -> new PEPickaxe(EnumMatterType.DARK_MATTER, 2, properties));
	public static final ItemRegistryObject<PEAxe> DARK_MATTER_AXE = ITEMS.registerNoStack("dm_axe", properties -> new PEAxe(EnumMatterType.DARK_MATTER, 2, properties));
	public static final ItemRegistryObject<PEShovel> DARK_MATTER_SHOVEL = ITEMS.registerNoStack("dm_shovel", properties -> new PEShovel(EnumMatterType.DARK_MATTER, 2, properties));
	public static final ItemRegistryObject<PESword> DARK_MATTER_SWORD = ITEMS.registerNoStack("dm_sword", properties -> new PESword(EnumMatterType.DARK_MATTER, 2, 9, properties));
	public static final ItemRegistryObject<PEHoe> DARK_MATTER_HOE = ITEMS.registerNoStack("dm_hoe", properties -> new PEHoe(EnumMatterType.DARK_MATTER, 2, properties));
	public static final ItemRegistryObject<PEShears> DARK_MATTER_SHEARS = ITEMS.registerNoStack("dm_shears", properties -> new PEShears(EnumMatterType.DARK_MATTER, 2, properties));
	public static final ItemRegistryObject<PEHammer> DARK_MATTER_HAMMER = ITEMS.registerNoStack("dm_hammer", properties -> new PEHammer(EnumMatterType.DARK_MATTER, 2, properties));

	public static final ItemRegistryObject<PEPickaxe> RED_MATTER_PICKAXE = ITEMS.registerNoStack("rm_pick", properties -> new PEPickaxe(EnumMatterType.RED_MATTER, 3, properties));
	public static final ItemRegistryObject<PEAxe> RED_MATTER_AXE = ITEMS.registerNoStack("rm_axe", properties -> new PEAxe(EnumMatterType.RED_MATTER, 3, properties));
	public static final ItemRegistryObject<PEShovel> RED_MATTER_SHOVEL = ITEMS.registerNoStack("rm_shovel", properties -> new PEShovel(EnumMatterType.RED_MATTER, 3, properties));
	public static final ItemRegistryObject<RedMatterSword> RED_MATTER_SWORD = ITEMS.registerNoStack("rm_sword", RedMatterSword::new);
	public static final ItemRegistryObject<PEHoe> RED_MATTER_HOE = ITEMS.registerNoStack("rm_hoe", properties -> new PEHoe(EnumMatterType.RED_MATTER, 3, properties));
	public static final ItemRegistryObject<PEShears> RED_MATTER_SHEARS = ITEMS.registerNoStack("rm_shears", properties -> new PEShears(EnumMatterType.RED_MATTER, 3, properties));
	public static final ItemRegistryObject<PEHammer> RED_MATTER_HAMMER = ITEMS.registerNoStack("rm_hammer", properties -> new PEHammer(EnumMatterType.RED_MATTER, 3, properties));
	public static final ItemRegistryObject<PEKatar> RED_MATTER_KATAR = ITEMS.registerNoStack("rm_katar", properties -> new PEKatar(EnumMatterType.RED_MATTER, 4, properties));
	public static final ItemRegistryObject<PEMorningStar> RED_MATTER_MORNING_STAR = ITEMS.registerNoStack("rm_morning_star", properties -> new PEMorningStar(EnumMatterType.RED_MATTER, 4, properties));

	public static final ItemRegistryObject<DMArmor> DARK_MATTER_HELMET = ITEMS.registerNoStack("dm_helmet", properties -> new DMArmor(EquipmentSlotType.HEAD, properties));
	public static final ItemRegistryObject<DMArmor> DARK_MATTER_CHESTPLATE = ITEMS.registerNoStack("dm_chestplate", properties -> new DMArmor(EquipmentSlotType.CHEST, properties));
	public static final ItemRegistryObject<DMArmor> DARK_MATTER_LEGGINGS = ITEMS.registerNoStack("dm_leggings", properties -> new DMArmor(EquipmentSlotType.LEGS, properties));
	public static final ItemRegistryObject<DMArmor> DARK_MATTER_BOOTS = ITEMS.registerNoStack("dm_boots", properties -> new DMArmor(EquipmentSlotType.FEET, properties));

	public static final ItemRegistryObject<RMArmor> RED_MATTER_HELMET = ITEMS.registerNoStack("rm_helmet", properties -> new RMArmor(EquipmentSlotType.HEAD, properties));
	public static final ItemRegistryObject<RMArmor> RED_MATTER_CHESTPLATE = ITEMS.registerNoStack("rm_chestplate", properties -> new RMArmor(EquipmentSlotType.CHEST, properties));
	public static final ItemRegistryObject<RMArmor> RED_MATTER_LEGGINGS = ITEMS.registerNoStack("rm_leggings", properties -> new RMArmor(EquipmentSlotType.LEGS, properties));
	public static final ItemRegistryObject<RMArmor> RED_MATTER_BOOTS = ITEMS.registerNoStack("rm_boots", properties -> new RMArmor(EquipmentSlotType.FEET, properties));

	public static final ItemRegistryObject<GemHelmet> GEM_HELMET = ITEMS.registerNoStack("gem_helmet", GemHelmet::new);
	public static final ItemRegistryObject<GemChest> GEM_CHESTPLATE = ITEMS.registerNoStack("gem_chestplate", GemChest::new);
	public static final ItemRegistryObject<GemLegs> GEM_LEGGINGS = ITEMS.registerNoStack("gem_leggings", GemLegs::new);
	public static final ItemRegistryObject<GemFeet> GEM_BOOTS = ITEMS.registerNoStack("gem_boots", GemFeet::new);

	public static final ItemRegistryObject<Item> IRON_BAND = ITEMS.register("iron_band");
	public static final ItemRegistryObject<BlackHoleBand> BLACK_HOLE_BAND = ITEMS.registerNoStack("black_hole_band", BlackHoleBand::new);
	public static final ItemRegistryObject<ArchangelSmite> ARCHANGEL_SMITE = ITEMS.registerNoStack("archangel_smite", ArchangelSmite::new);
	public static final ItemRegistryObject<HarvestGoddess> HARVEST_GODDESS_BAND = ITEMS.registerNoStack("harvest_goddess_band", HarvestGoddess::new);
	public static final ItemRegistryObject<Ignition> IGNITION_RING = ITEMS.registerNoStack("ignition_ring", Ignition::new);
	public static final ItemRegistryObject<Zero> ZERO_RING = ITEMS.registerNoStack("zero_ring", Zero::new);
	public static final ItemRegistryObject<SWRG> SWIFTWOLF_RENDING_GALE = ITEMS.registerNoStack("swiftwolf_rending_gale", SWRG::new);
	public static final ItemRegistryObject<TimeWatch> WATCH_OF_FLOWING_TIME = ITEMS.registerNoStack("watch_of_flowing_time", TimeWatch::new);
	public static final ItemRegistryObject<EvertideAmulet> EVERTIDE_AMULET = ITEMS.registerNoStack("evertide_amulet", EvertideAmulet::new);
	public static final ItemRegistryObject<VolcaniteAmulet> VOLCANITE_AMULET = ITEMS.registerNoStack("volcanite_amulet", VolcaniteAmulet::new);
	public static final ItemRegistryObject<GemEternalDensity> GEM_OF_ETERNAL_DENSITY = ITEMS.registerNoStack("gem_of_eternal_density", GemEternalDensity::new);
	public static final ItemRegistryObject<MercurialEye> MERCURIAL_EYE = ITEMS.registerNoStack("mercurial_eye", MercurialEye::new);
	public static final ItemRegistryObject<VoidRing> VOID_RING = ITEMS.registerNoStack("void_ring", VoidRing::new);
	public static final ItemRegistryObject<Arcana> ARCANA_RING = ITEMS.registerNoStack("arcana_ring", properties -> new Arcana(properties.rarity(Rarity.RARE)));
	public static final ItemRegistryObject<BodyStone> BODY_STONE = ITEMS.registerNoStack("body_stone", BodyStone::new);
	public static final ItemRegistryObject<SoulStone> SOUL_STONE = ITEMS.registerNoStack("soul_stone", SoulStone::new);
	public static final ItemRegistryObject<MindStone> MIND_STONE = ITEMS.registerNoStack("mind_stone", MindStone::new);
	public static final ItemRegistryObject<LifeStone> LIFE_STONE = ITEMS.registerNoStack("life_stone", LifeStone::new);

	public static final ItemRegistryObject<DiviningRod> LOW_DIVINING_ROD = ITEMS.registerNoStack("divining_rod_1", properties -> new DiviningRod(properties, PELang.DIVINING_RANGE_3));
	public static final ItemRegistryObject<DiviningRod> MEDIUM_DIVINING_ROD = ITEMS.registerNoStack("divining_rod_2", properties -> new DiviningRod(properties, PELang.DIVINING_RANGE_3, PELang.DIVINING_RANGE_16));
	public static final ItemRegistryObject<DiviningRod> HIGH_DIVINING_ROD = ITEMS.registerNoStack("divining_rod_3", properties -> new DiviningRod(properties, PELang.DIVINING_RANGE_3, PELang.DIVINING_RANGE_16, PELang.DIVINING_RANGE_64));

	public static final ItemRegistryObject<DestructionCatalyst> DESTRUCTION_CATALYST = ITEMS.registerNoStack("destruction_catalyst", DestructionCatalyst::new);
	public static final ItemRegistryObject<HyperkineticLens> HYPERKINETIC_LENS = ITEMS.registerNoStack("hyperkinetic_lens", HyperkineticLens::new);
	public static final ItemRegistryObject<CataliticLens> CATALYTIC_LENS = ITEMS.registerNoStack("catalytic_lens", CataliticLens::new);

	public static final ItemRegistryObject<Tome> TOME_OF_KNOWLEDGE = ITEMS.registerNoStack("tome", properties -> new Tome(properties.rarity(Rarity.EPIC)));
	public static final ItemRegistryObject<TransmutationTablet> TRANSMUTATION_TABLET = ITEMS.registerNoStack("transmutation_tablet", TransmutationTablet::new);

	private static ItemRegistryObject<AlchemicalBag> registerBag(DyeColor color) {
		return ITEMS.registerNoStack(color.getTranslationKey() + "_alchemical_bag", properties -> new AlchemicalBag(properties, color));
	}

	private static ItemRegistryObject<KleinStar> registerKleinStar(EnumKleinTier tier) {
		return ITEMS.registerNoStack("klein_star_" + tier.name, properties -> {
			if (tier == EnumKleinTier.OMEGA) {
				properties = properties.rarity(Rarity.EPIC);
			}
			return new KleinStar(properties, tier);
		});
	}

	private static ItemRegistryObject<AlchemicalFuel> registerAlchemicalFuel(EnumFuelType fuelType) {
		return ITEMS.register(fuelType.getString(), properties -> {
			if (fuelType == EnumFuelType.AETERNALIS_FUEL) {
				properties = properties.rarity(Rarity.RARE);
			}
			return new AlchemicalFuel(properties, fuelType);
		});
	}

	public static AlchemicalBag getBag(DyeColor color) {
		switch (color) {
			default:
			case WHITE:
				return WHITE_ALCHEMICAL_BAG.get();
			case ORANGE:
				return ORANGE_ALCHEMICAL_BAG.get();
			case MAGENTA:
				return MAGENTA_ALCHEMICAL_BAG.get();
			case LIGHT_BLUE:
				return LIGHT_BLUE_ALCHEMICAL_BAG.get();
			case YELLOW:
				return YELLOW_ALCHEMICAL_BAG.get();
			case LIME:
				return LIME_ALCHEMICAL_BAG.get();
			case PINK:
				return PINK_ALCHEMICAL_BAG.get();
			case GRAY:
				return GRAY_ALCHEMICAL_BAG.get();
			case LIGHT_GRAY:
				return LIGHT_GRAY_ALCHEMICAL_BAG.get();
			case CYAN:
				return CYAN_ALCHEMICAL_BAG.get();
			case PURPLE:
				return PURPLE_ALCHEMICAL_BAG.get();
			case BLUE:
				return BLUE_ALCHEMICAL_BAG.get();
			case BROWN:
				return BROWN_ALCHEMICAL_BAG.get();
			case GREEN:
				return GREEN_ALCHEMICAL_BAG.get();
			case RED:
				return RED_ALCHEMICAL_BAG.get();
			case BLACK:
				return BLACK_ALCHEMICAL_BAG.get();
		}
	}
}