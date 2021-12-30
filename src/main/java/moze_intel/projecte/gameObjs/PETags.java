package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeTagHandler;
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

		public static final Named<Item> ALCHEMICAL_BAGS = tag("alchemical_bags");
		/**
		 * Items in this tag will be used for the various collector fuel upgrade recipes.
		 */
		public static final Named<Item> COLLECTOR_FUEL = tag("collector_fuel");
		/**
		 * Items in this tag can have their NBT tags duped by condensers and transmutation tables
		 */
		public static final Named<Item> NBT_WHITELIST = tag("nbt_whitelist");
		/**
		 * Items in this tag can contribute and are "valid dusts" for the covalence repair recipe
		 */
		public static final Named<Item> COVALENCE_DUST = tag("covalence_dust");
		//Curios tags
		public static final Named<Item> CURIOS_BELT = curiosTag("belt");
		public static final Named<Item> CURIOS_KLEIN_STAR = curiosTag("klein_star");
		public static final Named<Item> CURIOS_NECKLACE = curiosTag("necklace");
		public static final Named<Item> CURIOS_RING = curiosTag("ring");

		private static Named<Item> tag(String name) {
			return ItemTags.bind(PECore.rl(name).toString());
		}

		private static Named<Item> curiosTag(String name) {
			return ItemTags.bind(new ResourceLocation(IntegrationHelper.CURIO_MODID, name).toString());
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
		public static final Named<Block> BLACKLIST_HARVEST = tag("blacklist/harvest");
		/**
		 * Blocks added will not receive extra random ticks from the Watch of Flowing Time
		 */
		public static final Named<Block> BLACKLIST_TIME_WATCH = tag("blacklist/time_watch");

		public static final Named<Block> NEEDS_DARK_MATTER_TOOL = tag("needs/dark_matter");
		public static final Named<Block> NEEDS_RED_MATTER_TOOL = tag("needs/red_matter");

		public static final Named<Block> MINEABLE_WITH_PE_KATAR = tag("mineable/katar");
		public static final Named<Block> MINEABLE_WITH_PE_HAMMER = tag("mineable/hammer");
		public static final Named<Block> MINEABLE_WITH_PE_MORNING_STAR = tag("mineable/morning_star");
		public static final Named<Block> MINEABLE_WITH_PE_SHEARS = tag("mineable/shears");
		public static final Named<Block> MINEABLE_WITH_PE_SWORD = tag("mineable/sword");

		public static final Named<Block> MINEABLE_WITH_HAMMER = forgeTag("mineable/hammer");
		public static final Named<Block> MINEABLE_WITH_KATAR = forgeTag("mineable/katar");
		public static final Named<Block> MINEABLE_WITH_MORNING_STAR = forgeTag("mineable/morning_star");


		private static Named<Block> tag(String name) {
			return BlockTags.bind(PECore.rl(name).toString());
		}

		private static Named<Block> forgeTag(String name) {
			return BlockTags.bind(new ResourceLocation("forge", name).toString());
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
		public static final Named<EntityType<?>> BLACKLIST_SWRG = tag("blacklist/swrg");
		/**
		 * Entity types added here will not be repelled by the Interdiction Torch.
		 */
		public static final Named<EntityType<?>> BLACKLIST_INTERDICTION = tag("blacklist/interdiction");
		/**
		 * Philosopher stone's (peaceful) entity randomizer list (Only supports Mob Entities)
		 */
		public static final Named<EntityType<?>> RANDOMIZER_PEACEFUL = tag("randomizer/peaceful");
		/**
		 * Philosopher stone's (hostile) entity randomizer list (Only supports Mob Entities)
		 */
		public static final Named<EntityType<?>> RANDOMIZER_HOSTILE = tag("randomizer/hostile");

		private static Named<EntityType<?>> tag(String name) {
			return EntityTypeTags.bind(PECore.rl(name).toString());
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
		public static final Named<BlockEntityType<?>> BLACKLIST_TIME_WATCH = tag("blacklist/time_watch");

		private static Named<BlockEntityType<?>> tag(String name) {
			return ForgeTagHandler.makeWrapperTag(ForgeRegistries.BLOCK_ENTITIES, PECore.rl(name));
		}
	}
}