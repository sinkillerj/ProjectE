package moze_intel.projecte.client;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.integration.IntegrationHelper;
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
		//TODO - 1.20: Test and validate this? We previously checked if curios was loaded
		addFiles(atlas(BLOCKS_ATLAS), IntegrationHelper.CURIOS_KLEIN_STAR);
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