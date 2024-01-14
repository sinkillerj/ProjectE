package moze_intel.projecte.api.codec;

import com.mojang.serialization.Codec;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;

/**
 * Helper to hold references to the important codec information of {@link NormalizedSimpleStack NormalizedSimpleStacks}
 *
 * @param legacyPrefix A string representing the prefix to use for serialization. Does not include the '|'
 * @param legacy       Legacy codec capable of reading and writing a {@link NormalizedSimpleStack} to/from strings.
 * @param explicit     Explicit codec capable of reading and writing a {@link NormalizedSimpleStack}.
 */
public record NSSCodecHolder<NSS extends NormalizedSimpleStack>(String legacyPrefix, Codec<NSS> legacy, Codec<NSS> explicit) {
}