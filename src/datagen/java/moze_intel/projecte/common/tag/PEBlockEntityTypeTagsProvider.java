package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags.BlockEntities;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PEBlockEntityTypeTagsProvider extends TagsProvider<BlockEntityType<?>> {

	public PEBlockEntityTypeTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.BLOCK_ENTITY_TYPE, lookupProvider, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		tag(BlockEntities.BLACKLIST_TIME_WATCH).add(
				ForgeRegistries.BLOCK_ENTITY_TYPES.getResourceKey(PEBlockEntityTypes.DARK_MATTER_PEDESTAL.get()).orElseThrow()
		);
	}

	@NotNull
	@Override
	public String getName() {
		return "Block Entity Type Tags";
	}
}
