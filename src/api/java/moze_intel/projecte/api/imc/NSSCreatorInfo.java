package moze_intel.projecte.api.imc;

import moze_intel.projecte.api.nss.NSSCreator;

/**
 * @param key     Key that goes before the | to represent the given {@link NSSCreator} for JSON deserialization
 * @param creator A creator to parse a {@link String} read from JSON and return a {@link moze_intel.projecte.api.nss.NormalizedSimpleStack}
 */
public record NSSCreatorInfo(String key, NSSCreator creator) {
}