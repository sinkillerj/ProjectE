package moze_intel.projecte.common.datagen;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;

/**
 * Wraps a {@link TagAppender} so registry elements can be passed directly instead of {@link ResourceKey}s.
 */
public final class RegistryKeyTagAppender<T> implements TagAppender<T> {

	private final TagAppender<T> delegate;
	private final Function<T, ResourceKey<T>> toKey;

	private RegistryKeyTagAppender(TagAppender<T> delegate, Function<T, ResourceKey<T>> toKey) {
		this.delegate = delegate;
		this.toKey = toKey;
	}

	public static <T> RegistryKeyTagAppender<T> wrap(TagAppender<T> delegate, Registry<T> registry) {
		return new RegistryKeyTagAppender<>(delegate, value -> registry.getResourceKey(value).orElseThrow());
	}

	public RegistryKeyTagAppender<T> add(T value) {
		delegate.add(toKey.apply(value));
		return this;
	}

	public RegistryKeyTagAppender<T> add(T first, T... rest) {
		add(first);
		for (T value : rest) {
			add(value);
		}
		return this;
	}

	@Override
	public TagAppender<T> add(ResourceKey<T> key) {
		delegate.add(key);
		return this;
	}

	@Override
	public TagAppender<T> addOptional(ResourceKey<T> key) {
		delegate.addOptional(key);
		return this;
	}

	@Override
	public TagAppender<T> addTag(TagKey<T> tag) {
		delegate.addTag(tag);
		return this;
	}

	@Override
	public TagAppender<T> addOptionalTag(TagKey<T> tag) {
		delegate.addOptionalTag(tag);
		return this;
	}

	@Override
	public TagAppender<T> add(TagEntry entry) {
		delegate.add(entry);
		return this;
	}

	@Override
	public TagAppender<T> replace(boolean value) {
		delegate.replace(value);
		return this;
	}

	@Override
	public TagAppender<T> remove(ResourceKey<T> key) {
		delegate.remove(key);
		return this;
	}

	@Override
	public TagAppender<T> remove(TagKey<T> tag) {
		delegate.remove(tag);
		return this;
	}

	public RegistryKeyTagAppender<T> remove(T value) {
		delegate.remove(toKey.apply(value));
		return this;
	}

	@Override
	public TagAppender<T> addAll(Collection<ResourceKey<T>> keys) {
		delegate.addAll(keys);
		return this;
	}

	@Override
	public TagAppender<T> addAll(Stream<ResourceKey<T>> keys) {
		delegate.addAll(keys);
		return this;
	}
}
