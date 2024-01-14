package moze_intel.projecte.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.impl.codec.PECodecHelper;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.Nullable;

public final class CustomEMCParser {

	private static final Path CONFIG = ProjectEConfig.CONFIG_DIR.resolve("custom_emc.json");

	public record CustomEMCFile(Map<NSSItem, Long> entries, @Nullable String comment) {

		private static final NSSItem INVALID_ITEM = NSSItem.createItem(PECore.rl("invalid_custom_emc_nss"));

		private static final MapCodec<NSSItem> LEGACY_KEY_CODEC = IPECodecHelper.INSTANCE.orElseWithLog(NeoForgeExtraCodecs.withAlternative(
				//True legacy format where it only supported the legacy item representation
				NSSItem.LEGACY_CODEC.fieldOf("item"),
				//Extended legacy format to allow more explicit declaration of the item
				NSSItem.EXPLICIT_MAP_CODEC
		), INVALID_ITEM, () -> "Unable to deserialize normalized item: {}");

		private static final Codec<Entry<NSSItem, Long>> LEGACY_ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
				LEGACY_KEY_CODEC.forGetter(Map.Entry::getKey),
				IPECodecHelper.INSTANCE.nonNegativeLong().fieldOf("emc").forGetter(Map.Entry::getValue)
		).apply(instance, Map::entry));

		private static final Codec<Map<NSSItem, Long>> ENTRIES_CODEC = NeoForgeExtraCodecs.withAlternative(
				IPECodecHelper.INSTANCE.modifiableMap(
						//Skip invalid keys
						IPECodecHelper.INSTANCE.lenientKeyUnboundedMap(NSSItem.LEGACY_CODEC, IPECodecHelper.INSTANCE.nonNegativeLong()),
						LinkedHashMap::new
				),
				//Load legacy data where it was an array of json objects of an item and emc value
				//Note: The list does not need to be mutable as when we xmap it into a Map we then make it mutable
				LEGACY_ENTRY_CODEC.listOf().xmap(
						list -> list.stream()
								//Filter out any invalid entries
								.filter(entry -> entry.getKey() != INVALID_ITEM)
								.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new)),
						map -> map.entrySet().stream().toList()
				)
		);

		public static final Codec<CustomEMCFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.NON_EMPTY_STRING.optionalFieldOf("comment").forGetter(file -> Optional.ofNullable(file.comment)),
				ENTRIES_CODEC.fieldOf("entries").forGetter(CustomEMCFile::entries)
		).apply(instance, (comment, entries) -> new CustomEMCFile(entries, comment.orElse(null))));
	}

	public static CustomEMCFile currentEntries;
	private static boolean dirty = false;

	private static CustomEMCFile createDefault() {
		return new CustomEMCFile(new LinkedHashMap<>(), "Use the in-game commands to edit this file");
	}

	public static void init() {
		flush();

		if (Files.exists(CONFIG)) {
			currentEntries = PECodecHelper.readFromFile(CONFIG, CustomEMCFile.CODEC, "custom emc")
					.orElseGet(CustomEMCParser::createDefault);
		} else {
			currentEntries = createDefault();
			PECodecHelper.writeToFile(CONFIG, CustomEMCFile.CODEC, currentEntries, "default custom EMC");
		}
	}

	public static void addToFile(NSSItem toAdd, long emc) {
		if (emc < 0) {
			throw new IllegalArgumentException("EMC must be non-negative: " + emc);
		}
		Long old = currentEntries.entries().put(toAdd, emc);
		if (old == null || old != emc) {
			dirty = true;
		}
	}

	public static boolean removeFromFile(NSSItem toRemove) {
		boolean removed = currentEntries.entries().remove(toRemove) != null;
		if (removed) {
			dirty = true;
		}
		return removed;
	}

	public static void flush() {
		if (dirty) {
			PECodecHelper.writeToFile(CONFIG, CustomEMCFile.CODEC, currentEntries, "custom EMC");
			dirty = false;
		}
	}
}