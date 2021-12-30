package moze_intel.projecte.common.tag;

import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
				Blocks.COBWEB,
				Blocks.REDSTONE_WIRE,
				Blocks.TRIPWIRE
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
	}

	private void addImmuneBlocks(Named<Block> tag) {
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