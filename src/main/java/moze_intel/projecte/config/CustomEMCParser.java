package moze_intel.projecte.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.json.NSSSerializer;

public final class CustomEMCParser {

	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(NormalizedSimpleStack.class, NSSSerializer.INSTANCE).setPrettyPrinting().create();
	private static final File CONFIG = new File(PECore.CONFIG_DIR, "custom_emc.json");

	public static class CustomEMCFile {

		public final List<CustomEMCEntry> entries;

		public CustomEMCFile(List<CustomEMCEntry> entries) {
			this.entries = entries;
		}
	}

	public static class CustomEMCEntry {

		public final NormalizedSimpleStack item;
		public final long emc;

		private CustomEMCEntry(NormalizedSimpleStack item, long emc) {
			this.item = item;
			this.emc = emc;
		}

		@Override
		public boolean equals(Object o) {
			return o == this || o instanceof CustomEMCEntry && item.equals(((CustomEMCEntry) o).item) && emc == ((CustomEMCEntry) o).emc;
		}

		@Override
		public int hashCode() {
			int result = item != null ? item.hashCode() : 0;
			result = 31 * result + (int) (emc ^ (emc >>> 32));
			return result;
		}
	}

	public static CustomEMCFile currentEntries;
	private static boolean dirty = false;

	public static void init() {
		flush();

		if (!CONFIG.exists()) {
			try {
				if (CONFIG.createNewFile()) {
					writeDefaultFile();
				}
			} catch (IOException e) {
				PECore.LOGGER.fatal("Exception in file I/O: couldn't create custom configuration files.");
			}
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG))) {
			currentEntries = GSON.fromJson(reader, CustomEMCFile.class);
			currentEntries.entries.removeIf(e -> !(e.item instanceof NSSItem) || e.emc < 0);
		} catch (IOException | JsonParseException e) {
			PECore.LOGGER.fatal("Couldn't read custom emc file");
			e.printStackTrace();
			currentEntries = new CustomEMCFile(new ArrayList<>());
		}
	}

	private static NormalizedSimpleStack getNss(String str) {
		return NSSSerializer.INSTANCE.deserialize(str);
	}

	public static void addToFile(String toAdd, long emc) {
		NormalizedSimpleStack nss = getNss(toAdd);
		CustomEMCEntry entry = new CustomEMCEntry(nss, emc);
		int setAt = -1;
		for (int i = 0; i < currentEntries.entries.size(); i++) {
			if (currentEntries.entries.get(i).item.equals(nss)) {
				setAt = i;
				break;
			}
		}
		if (setAt == -1) {
			currentEntries.entries.add(entry);
		} else {
			currentEntries.entries.set(setAt, entry);
		}
		dirty = true;
	}

	public static boolean removeFromFile(String toRemove) {
		NormalizedSimpleStack nss = getNss(toRemove);
		Iterator<CustomEMCEntry> iter = currentEntries.entries.iterator();
		boolean removed = false;
		while (iter.hasNext()) {
			if (iter.next().item.equals(nss)) {
				iter.remove();
				dirty = true;
				removed = true;
			}
		}
		return removed;
	}

	private static void flush() {
		if (dirty) {
			try {
				Files.write(GSON.toJson(currentEntries), CONFIG, Charsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}
			dirty = false;
		}
	}

	private static void writeDefaultFile() {
		JsonObject elem = (JsonObject) GSON.toJsonTree(new CustomEMCFile(new ArrayList<>()));
		elem.add("__comment", new JsonPrimitive("Use the in-game commands to edit this file"));
		try {
			Files.write(GSON.toJson(elem), CONFIG, Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}