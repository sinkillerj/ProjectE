package moze_intel.projecte.client;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import moze_intel.projecte.PECore;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public class PESpriteSourceProvider extends SpriteSourceProvider {

	private final Set<ResourceLocation> trackedSingles = new HashSet<>();

	public PESpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, fileHelper, PECore.MODID);
	}

	@Override
	protected void addSources() {
		//Note: We always stitch this even when curios isn't loaded, but I don't think there is much we can do about that,
		// and it is only a small texture, so it won't matter too much
		addFiles(atlas(BLOCKS_ATLAS), PECore.rl("curios/empty_klein_star"));
	}

	protected void addFiles(SourceList atlas, ResourceLocation... resourceLocations) {
		for (ResourceLocation rl : resourceLocations) {
			//Only add this source if we haven't already added it as a direct single file source
			if (trackedSingles.add(rl)) {
				atlas.addSource(new SingleFile(rl, Optional.empty()));
			}
		}
	}

	protected void addDirectory(SourceList atlas, String directory, String spritePrefix) {
		atlas.addSource(new DirectoryLister(directory, spritePrefix));
	}
}