package moze_intel.projecte.common;

import java.util.Optional;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.DetectedVersion;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;

//From Mekanism's BasePackMetadataGenerator
public class PEPackMetadataGenerator extends PackMetadataGenerator {

	public PEPackMetadataGenerator(PackOutput output, IHasTranslationKey description) {
		super(output);
		int minVersion = Integer.MAX_VALUE;
		int maxVersion = 0;
		for (PackType packType : PackType.values()) {
			int version = DetectedVersion.BUILT_IN.getPackVersion(packType);
			maxVersion = Math.max(maxVersion, version);
			minVersion = Math.min(minVersion, version);
		}
		add(PackMetadataSection.TYPE, new PackMetadataSection(
				Component.translatable(description.getTranslationKey()),
				maxVersion,
				Optional.of(new InclusiveRange<>(minVersion, maxVersion))
		));
	}
}