package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.LazyTagLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class PETags {

	private PETags() {
	}

	/**
	 * Call to force make sure this is all initialized
	 */
	public static void init() {
		Items.init();
		Blocks.init();
		Entities.init();
		BlockEntities.init();
	}

	public static class Items {

		private static void init() {
		}

		private Items() {
		}

		public static final TagKey<Item> ALCHEMICAL_BAGS = tag("alchemical_bags");
		/**
		 * Items in this tag will be used for the various collector fuel upgrade recipes.
		 */
		public static final TagKey<Item> COLLECTOR_FUEL = tag("collector_fuel");
		public static final LazyTagLookup<Item> COLLECTOR_FUEL_LOOKUP = LazyTagLookup.create(ForgeRegistries.ITEMS, COLLECTOR_FUEL);
		/**
		 * Items in this tag can have their NBT tags duped by condensers and transmutation tables
		 */
		public static final TagKey<Item> NBT_WHITELIST = tag("nbt_whitelist");
		/**
		 * Items in this tag can contribute and are "valid dusts" for the covalence repair recipe
		 */
		public static final TagKey<Item> COVALENCE_DUST = tag("covalence_dust");
		//Curios tags
		public static final TagKey<Item> CURIOS_BELT = curiosTag("belt");
		public static final TagKey<Item> CURIOS_KLEIN_STAR = curiosTag("klein_star");
		public static final TagKey<Item> CURIOS_NECKLACE = curiosTag("necklace");
		public static final TagKey<Item> CURIOS_RING = curiosTag("ring");

		public static final LazyTagLookup<Item> ORES_LOOKUP = LazyTagLookup.create(ForgeRegistries.ITEMS, Tags.Items.ORES);
		public static final LazyTagLookup<Item> RAW_ORES_LOOKUP = LazyTagLookup.create(ForgeRegistries.ITEMS, Tags.Items.RAW_MATERIALS);

		private static TagKey<Item> tag(String name) {
			return ItemTags.create(PECore.rl(name));
		}

		private static TagKey<Item> curiosTag(String name) {
			return ItemTags.create(new ResourceLocation(IntegrationHelper.CURIO_MODID, name));
		}
	}

	public static class Blocks {

		private static void init() {
		}

		private Blocks() {
		}

		/**
		 * Blocks added here (that are IGrowable) will not be broken by the harvest goddess band when unable to continue growing.
		 */
		public static final TagKey<Block> BLACKLIST_HARVEST = tag("blacklist/harvest");
		/**
		 * Blocks added will not receive extra random ticks from the Watch of Flowing Time
		 */
		public static final TagKey<Block> BLACKLIST_TIME_WATCH = tag("blacklist/time_watch");

		public static final TagKey<Block> NEEDS_DARK_MATTER_TOOL = tag("needs_dark_matter_tool");
		public static final TagKey<Block> NEEDS_RED_MATTER_TOOL = tag("needs_red_matter_tool");

		public static final TagKey<Block> MINEABLE_WITH_PE_KATAR = tag("mineable/katar");
		public static final TagKey<Block> MINEABLE_WITH_PE_HAMMER = tag("mineable/hammer");
		public static final TagKey<Block> MINEABLE_WITH_PE_MORNING_STAR = tag("mineable/morning_star");
		public static final TagKey<Block> MINEABLE_WITH_PE_SHEARS = tag("mineable/shears");
		public static final TagKey<Block> MINEABLE_WITH_PE_SWORD = tag("mineable/sword");

		public static final TagKey<Block> MINEABLE_WITH_HAMMER = forgeTag("mineable/hammer");
		public static final TagKey<Block> MINEABLE_WITH_KATAR = forgeTag("mineable/katar");
		public static final TagKey<Block> MINEABLE_WITH_MORNING_STAR = forgeTag("mineable/morning_star");


		private static TagKey<Block> tag(String name) {
			return BlockTags.create(PECore.rl(name));
		}

		private static TagKey<Block> forgeTag(String name) {
			return BlockTags.create(new ResourceLocation("forge", name));
		}
	}

	public static class Entities {

		private static void init() {
		}

		private Entities() {
		}

		/**
		 * Entity types added here will not be repelled by the Swiftwolf Rending Gale's repel effect.
		 */
		public static final TagKey<EntityType<?>> BLACKLIST_SWRG = tag("blacklist/swrg");
		/**
		 * Entity types added here will not be repelled by the Interdiction Torch.
		 */
		public static final TagKey<EntityType<?>> BLACKLIST_INTERDICTION = tag("blacklist/interdiction");
		/**
		 * Philosopher stone's (peaceful) entity randomizer list (Only supports Mob Entities)
		 */
		public static final TagKey<EntityType<?>> RANDOMIZER_PEACEFUL = tag("randomizer/peaceful");
		/**
		 * Philosopher stone's (hostile) entity randomizer list (Only supports Mob Entities)
		 */
		public static final TagKey<EntityType<?>> RANDOMIZER_HOSTILE = tag("randomizer/hostile");

		private static TagKey<EntityType<?>> tag(String name) {
			return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, PECore.rl(name));
		}
	}

	public static class BlockEntities {

		private static void init() {
		}

		private BlockEntities() {
		}

		/**
		 * Block Entity Types added will not receive extra ticks from the Watch of Flowing Time
		 */
		public static final TagKey<BlockEntityType<?>> BLACKLIST_TIME_WATCH = tag("blacklist/time_watch");
		public static final LazyTagLookup<BlockEntityType<?>> BLACKLIST_TIME_WATCH_LOOKUP = LazyTagLookup.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BLACKLIST_TIME_WATCH);

		private static TagKey<BlockEntityType<?>> tag(String name) {
			return TagKey.create(Registry.BLOCK_ENTITY_TYPE_REGISTRY, PECore.rl(name));
		}
	}
}