package moze_intel.projecte.common.tag;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags.BlockEntities;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PEBlockEntityTypeTagsProvider extends ForgeRegistryTagsProvider<BlockEntityType<?>> {

	public PEBlockEntityTypeTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, ForgeRegistries.BLOCK_ENTITIES, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(BlockEntities.BLACKLIST_TIME_WATCH).add(
				PEBlockEntityTypes.DARK_MATTER_PEDESTAL.get()
		);
	}

	@NotNull
	@Override
	public String getName() {
		return "Block Entity Type Tags";
	}
}