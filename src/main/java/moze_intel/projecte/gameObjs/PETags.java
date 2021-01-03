package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class PETags {

	public static class Items {

		public static final INamedTag<Item> ALCHEMICAL_BAGS = tag("alchemical_bags");
		/**
		 * Items in this tag will be used for the various collector fuel upgrade recipes.
		 */
		public static final INamedTag<Item> COLLECTOR_FUEL = tag("collector_fuel");
		/**
		 * Items in this tag can have their NBT tags duped by condensers and transmutation tables
		 */
		public static final INamedTag<Item> NBT_WHITELIST = tag("nbt_whitelist");
		//Curios tags
		public static final INamedTag<Item> CURIOS_BELT = curiosTag("belt");
		public static final INamedTag<Item> CURIOS_KLEIN_STAR = curiosTag("klein_star");
		public static final INamedTag<Item> CURIOS_NECKLACE = curiosTag("necklace");
		public static final INamedTag<Item> CURIOS_RING = curiosTag("ring");

		private static INamedTag<Item> tag(String name) {
			return ItemTags.makeWrapperTag(PECore.rl(name).toString());
		}

		private static INamedTag<Item> curiosTag(String name) {
			return ItemTags.makeWrapperTag(new ResourceLocation(IntegrationHelper.CURIO_MODID, name).toString());
		}
	}

	public static class Blocks {

		/**
		 * Blocks added here (that are IGrowable) will not be broken by the harvest goddess band when unable to continue growing.
		 */
		public static final INamedTag<Block> BLACKLIST_HARVEST = tag("blacklist/harvest");
		/**
		 * Blocks added will not receive extra random ticks from the Watch of Flowing Time
		 */
		public static final INamedTag<Block> BLACKLIST_TIME_WATCH = tag("blacklist/time_watch");

		private static INamedTag<Block> tag(String name) {
			return BlockTags.makeWrapperTag(PECore.rl(name).toString());
		}
	}

	public static class Entities {

		/**
		 * Entity types added here will not be repelled by the Swiftwolf Rending Gale's repel effect.
		 */
		public static final INamedTag<EntityType<?>> BLACKLIST_SWRG = tag("blacklist/swrg");
		/**
		 * Entity types added here will not be repelled by the Interdiction Torch.
		 */
		public static final INamedTag<EntityType<?>> BLACKLIST_INTERDICTION = tag("blacklist/interdiction");
		/**
		 * Philosopher stone's (peaceful) entity randomizer list (Only supports Mob Entities)
		 */
		public static final INamedTag<EntityType<?>> RANDOMIZER_PEACEFUL = tag("randomizer/peaceful");
		/**
		 * Philosopher stone's (hostile) entity randomizer list (Only supports Mob Entities)
		 */
		public static final INamedTag<EntityType<?>> RANDOMIZER_HOSTILE = tag("randomizer/hostile");

		private static INamedTag<EntityType<?>> tag(String name) {
			return EntityTypeTags.getTagById(PECore.rl(name).toString());
		}
	}

	public static class TileEntities {

		/**
		 * Tile Entity Types added will not receive extra ticks from the Watch of Flowing Time
		 */
		public static final INamedTag<TileEntityType<?>> BLACKLIST_TIME_WATCH = tag("blacklist/time_watch");

		private static INamedTag<TileEntityType<?>> tag(String name) {
			return ForgeTagHandler.makeWrapperTag(ForgeRegistries.TILE_ENTITIES, PECore.rl(name));
		}
	}
}