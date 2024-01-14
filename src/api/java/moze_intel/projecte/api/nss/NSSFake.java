package moze_intel.projecte.api.nss;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.codec.NSSCodecHolder;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link NormalizedSimpleStack} for representing abstract objects, that do not actually exist.
 */
public final class NSSFake implements NormalizedSimpleStack {

	/**
	 * Codec for encoding NSSFake to and from strings.
	 */
	public static final Codec<NSSFake> LEGACY_CODEC = IPECodecHelper.INSTANCE.withPrefix("FAKE|").xmap(NSSFake::create, nss -> nss.description);

	public static final Codec<NSSFake> EXPLICIT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.strictOptionalField(ExtraCodecs.NON_EMPTY_STRING, "namespace").forGetter(nssFake -> IPECodecHelper.INSTANCE.ifNotEmpty(nssFake.namespace, String::isEmpty)),
			ExtraCodecs.NON_EMPTY_STRING.fieldOf("description").forGetter(nssFake -> nssFake.description)
	).apply(instance, (namespace, description) ->
			namespace.map(n -> new NSSFake(n, description))
					.orElseGet(() -> NSSFake.create(description))
	));

	public static final NSSCodecHolder<NSSFake> CODECS = new NSSCodecHolder<>("FAKE", LEGACY_CODEC, EXPLICIT_CODEC);

	/**
	 * We need this bit of global mutable state, so that we can distinguish fake NSS's originating from separate files, but have the same name. For example, "FAKE|foo"
	 * from a.json and "FAKE|foo" from b.json should not have anything to do with each other.
	 */
	private static String currentNamespace = "";

	private final String namespace;
	private final String description;

	private NSSFake(String namespace, String description) {
		this.namespace = namespace;
		this.description = description;
	}

	/**
	 * Resets the current namespace that will be used for any newly created {@link NSSFake} objects.
	 *
	 * @apiNote For internal use by ProjectE
	 */
	public static void resetNamespace() {
		setCurrentNamespace("");
	}

	/**
	 * Sets the current namespace that will be used for any newly created {@link NSSFake} objects.
	 *
	 * @param ns Namespace
	 *
	 * @apiNote For internal use by ProjectE
	 */
	public static void setCurrentNamespace(@NotNull String ns) {
		currentNamespace = ns;
	}

	/**
	 * Helper method to create an {@link NSSFake} representing an abstract object that does not actually exist from a description.
	 */
	@NotNull
	public static NSSFake create(String description) {
		if (description.isEmpty()) {
			throw new IllegalArgumentException("Description must not be empty");
		}
		return new NSSFake(currentNamespace, description);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof NSSFake fake && description.equals(fake.description) && namespace.equals(fake.namespace);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(namespace, description);
	}

	@Override
	public String toString() {
		return "NSSFake:" + namespace + "/" + description;
	}

	@Override
	public NSSCodecHolder<NSSFake> codecs() {
		return CODECS;
	}
}