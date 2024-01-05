package moze_intel.projecte.gameObjs.registration;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public class DeferredCodecHolder<R, T extends R> extends PEDeferredHolder<Codec<? extends R>, Codec<T>> {

	protected DeferredCodecHolder(@NotNull ResourceKey<Codec<? extends R>> key) {
		super(key);
	}
}