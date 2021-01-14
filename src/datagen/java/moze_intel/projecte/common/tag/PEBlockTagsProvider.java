package moze_intel.projecte.common.tag;

import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PEBlockTagsProvider extends BlockTagsProvider {

	public PEBlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		getOrCreateBuilder(PETags.Blocks.BLACKLIST_HARVEST).add(
				Blocks.GRASS_BLOCK,
				Blocks.CRIMSON_NYLIUM,
				Blocks.NETHERRACK,
				Blocks.MELON_STEM,
				Blocks.PUMPKIN_STEM,
				Blocks.WARPED_NYLIUM
		);
		getOrCreateBuilder(PETags.Blocks.BLACKLIST_TIME_WATCH);
	}
}