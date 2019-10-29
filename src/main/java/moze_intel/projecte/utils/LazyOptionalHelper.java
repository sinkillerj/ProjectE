package moze_intel.projecte.utils;

import java.util.Optional;
import net.minecraftforge.common.util.LazyOptional;

public class LazyOptionalHelper<T> {

	public static <T> Optional<T> toOptional(LazyOptional<T> lazyOptional) {
		if (lazyOptional.isPresent()) {
			return Optional.of(lazyOptional.orElseThrow(() -> new RuntimeException("Failed to retrieve value of lazy optional when it said it was present")));
		}
		return Optional.empty();
	}
}