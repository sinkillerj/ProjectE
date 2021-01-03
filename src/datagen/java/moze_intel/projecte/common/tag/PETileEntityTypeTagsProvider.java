package moze_intel.projecte.common.tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class PETileEntityTypeTagsProvider extends ForgeRegistryTagsProvider<TileEntityType<?>> {

	public PETileEntityTypeTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, ForgeRegistries.TILE_ENTITIES, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		getOrCreateBuilder(PETags.TileEntities.BLACKLIST_TIME_WATCH).add(
				PETileEntityTypes.DARK_MATTER_PEDESTAL.get()
		);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Tile Entity Type Tags";
	}
}