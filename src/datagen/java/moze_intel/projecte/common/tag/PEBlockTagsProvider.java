package moze_intel.projecte.common.tag;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class PEBlockTagsProvider extends BlockTagsProvider {

	public PEBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		tag(PETags.Blocks.FARMING_OVERRIDE).add(Blocks.PINK_PETALS);
		IntrinsicTagAppender<Block> blacklistHarvest = tag(PETags.Blocks.BLACKLIST_HARVEST);
		//Add blocks that sometimes return false from isValidBonemealTarget, but that we don't actually want to be broken
		blacklistHarvest.add(
				//If there is no neighboring nylium we don't want to cause the netherrack to be broken
				Blocks.NETHERRACK,
				//If it doesn't have air above it
				Blocks.BAMBOO_SAPLING,
				//If it doesn't have air below it
				Blocks.ROOTED_DIRT,
				//If it has a fluid above it
				Blocks.AZALEA,
				Blocks.FLOWERING_AZALEA,
				//If it doesn't have air
				Blocks.BIG_DRIPLEAF,
				Blocks.BIG_DRIPLEAF_STEM
		);
		IntrinsicTagAppender<Block> overridePlantable = tag(PETags.Blocks.OVERRIDE_PLANTABLE);
		overridePlantable.addTags(
				BlockTags.LEAVES,
				//Note: All vanilla tall flowers are bonemealable, so will get handled before being used by this tag
				// but if a mod adds a tall flower that doesn't inherit the class hierarchy, having this could be useful
				BlockTags.FLOWERS,
				Tags.Blocks.PUMPKINS_NORMAL
		).add(
				Blocks.MELON
		);
		for (Block block : BuiltInRegistries.BLOCK) {
			if (WorldHelper.isPlantableImplementation(block)) {
				overridePlantable.add(block);
			}
			if (WorldHelper.isUnharvestableImplementation(block)) {
				blacklistHarvest.add(block);
			}
		}
		tag(PETags.Blocks.BLACKLIST_TIME_WATCH);
		tag(PETags.Blocks.VEIN_SHOVEL)
				.add(Blocks.CLAY)
				.addTag(Tags.Blocks.GRAVELS);
		//Vanilla/Forge Tags
		tag(Tags.Blocks.CHESTS).add(
				PEBlocks.ALCHEMICAL_CHEST.getBlock()
		);
		tag(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES).add(
				PEBlocks.DARK_MATTER_FURNACE.getBlock(),
				PEBlocks.RED_MATTER_FURNACE.getBlock()
		);
		tag(BlockTags.BEACON_BASE_BLOCKS).add(
				PEBlocks.DARK_MATTER.getBlock(),
				PEBlocks.RED_MATTER.getBlock()
		);
		tag(BlockTags.GUARDED_BY_PIGLINS).add(
				PEBlocks.ALCHEMICAL_CHEST.getBlock(),
				PEBlocks.CONDENSER.getBlock(),
				PEBlocks.CONDENSER_MK2.getBlock()
		);
		tag(BlockTags.INFINIBURN_OVERWORLD).add(
				PEBlocks.ALCHEMICAL_COAL.getBlock(),
				PEBlocks.MOBIUS_FUEL.getBlock(),
				PEBlocks.AETERNALIS_FUEL.getBlock()
		);
		addImmuneBlocks(BlockTags.DRAGON_IMMUNE);
		addImmuneBlocks(BlockTags.WITHER_IMMUNE);

		tag(PETags.Blocks.MINEABLE_WITH_HAMMER);
		tag(PETags.Blocks.MINEABLE_WITH_KATAR);
		tag(PETags.Blocks.MINEABLE_WITH_MORNING_STAR);

		tag(PETags.Blocks.NEEDS_DARK_MATTER_TOOL).add(
				PEBlocks.DARK_MATTER.getBlock(),
				PEBlocks.DARK_MATTER_FURNACE.getBlock(),
				PEBlocks.DARK_MATTER_PEDESTAL.getBlock()
		);
		tag(PETags.Blocks.NEEDS_RED_MATTER_TOOL).add(
				PEBlocks.RED_MATTER.getBlock(),
				PEBlocks.RED_MATTER_FURNACE.getBlock()
		);
		tag(PETags.Blocks.INCORRECT_FOR_RED_MATTER_TOOL);
		tag(PETags.Blocks.INCORRECT_FOR_DARK_MATTER_TOOL).addTags(PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		tag(BlockTags.INCORRECT_FOR_NETHERITE_TOOL).addTags(PETags.Blocks.NEEDS_DARK_MATTER_TOOL, PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		tag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL).addTags(PETags.Blocks.NEEDS_DARK_MATTER_TOOL, PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		tag(BlockTags.INCORRECT_FOR_IRON_TOOL).addTags(PETags.Blocks.NEEDS_DARK_MATTER_TOOL, PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		tag(BlockTags.INCORRECT_FOR_STONE_TOOL).addTags(PETags.Blocks.NEEDS_DARK_MATTER_TOOL, PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		tag(BlockTags.INCORRECT_FOR_GOLD_TOOL).addTags(PETags.Blocks.NEEDS_DARK_MATTER_TOOL, PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL).addTags(PETags.Blocks.NEEDS_DARK_MATTER_TOOL, PETags.Blocks.NEEDS_RED_MATTER_TOOL);

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
				PEBlocks.ALCHEMICAL_CHEST.getBlock(),
				PEBlocks.ALCHEMICAL_COAL.getBlock(),
				PEBlocks.MOBIUS_FUEL.getBlock(),
				PEBlocks.AETERNALIS_FUEL.getBlock(),
				PEBlocks.COLLECTOR.getBlock(),
				PEBlocks.COLLECTOR_MK2.getBlock(),
				PEBlocks.COLLECTOR_MK3.getBlock(),
				PEBlocks.CONDENSER.getBlock(),
				PEBlocks.CONDENSER_MK2.getBlock(),
				PEBlocks.DARK_MATTER_PEDESTAL.getBlock(),
				PEBlocks.DARK_MATTER_FURNACE.getBlock(),
				PEBlocks.RED_MATTER_FURNACE.getBlock(),
				PEBlocks.DARK_MATTER.getBlock(),
				PEBlocks.RED_MATTER.getBlock(),
				PEBlocks.TRANSMUTATION_TABLE.getBlock(),
				PEBlocks.RELAY.getBlock(),
				PEBlocks.RELAY_MK2.getBlock(),
				PEBlocks.RELAY_MK3.getBlock()
		);

		//MINEABLE_WITH_PE_SHEARS
		tag(PETags.Blocks.MINEABLE_WITH_PE_HAMMER).addTags(
				PETags.Blocks.MINEABLE_WITH_HAMMER,
				BlockTags.MINEABLE_WITH_PICKAXE
		);
		tag(PETags.Blocks.MINEABLE_WITH_PE_SHEARS).add(
				//Blocks supported by vanilla shears
				Blocks.COBWEB
		);
		tag(PETags.Blocks.MINEABLE_WITH_PE_SWORD).add(
				//Blocks supported by vanilla swords
				Blocks.COBWEB
		);
		tag(PETags.Blocks.MINEABLE_WITH_PE_KATAR).addTags(
				PETags.Blocks.MINEABLE_WITH_KATAR,
				BlockTags.MINEABLE_WITH_AXE,
				BlockTags.MINEABLE_WITH_HOE,
				PETags.Blocks.MINEABLE_WITH_PE_SHEARS,
				PETags.Blocks.MINEABLE_WITH_PE_SWORD
		).add(Blocks.COBWEB);//Sword items
		tag(PETags.Blocks.MINEABLE_WITH_PE_MORNING_STAR).addTags(
				PETags.Blocks.MINEABLE_WITH_MORNING_STAR,
				PETags.Blocks.MINEABLE_WITH_PE_HAMMER,//Note: Pickaxe is inherited from hammer
				BlockTags.MINEABLE_WITH_SHOVEL
		);

		tag(BlockTags.WALL_POST_OVERRIDE).add(PEBlocks.INTERDICTION_TORCH.getBlock());
	}

	private void addImmuneBlocks(TagKey<Block> tag) {
		tag(tag).add(
				PEBlocks.DARK_MATTER.getBlock(),
				PEBlocks.DARK_MATTER_FURNACE.getBlock(),
				PEBlocks.DARK_MATTER_PEDESTAL.getBlock(),
				PEBlocks.RED_MATTER.getBlock(),
				PEBlocks.RED_MATTER_FURNACE.getBlock(),
				PEBlocks.CONDENSER_MK2.getBlock()
		);
	}
}