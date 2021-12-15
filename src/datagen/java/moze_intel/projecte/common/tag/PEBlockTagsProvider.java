package moze_intel.projecte.common.tag;

import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PEBlockTagsProvider extends BlockTagsProvider {

	public PEBlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(PETags.Blocks.BLACKLIST_HARVEST).add(
				Blocks.GRASS_BLOCK,
				Blocks.CRIMSON_NYLIUM,
				Blocks.NETHERRACK,
				Blocks.MELON_STEM,
				Blocks.PUMPKIN_STEM,
				Blocks.WARPED_NYLIUM
		);
		tag(PETags.Blocks.BLACKLIST_TIME_WATCH);
		//Vanilla/Forge Tags
		tag(Tags.Blocks.CHESTS).add(
				PEBlocks.ALCHEMICAL_CHEST.getBlock()
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
	}

	private void addImmuneBlocks(INamedTag<Block> tag) {
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