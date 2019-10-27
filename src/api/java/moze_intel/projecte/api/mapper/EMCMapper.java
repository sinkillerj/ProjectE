package moze_intel.projecte.api.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that this class should be loaded as an {@link IEMCMapper)} from {@link moze_intel.projecte.api.nss.NormalizedSimpleStack} to {@link Long}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EMCMapper {

	/**
	 * Array of modids that are required for this {@link EMCMapper} to be loaded, empty String or array for no dependencies.
	 *
	 * @return array of modids.
	 */
	String[] requiredMods() default "";

	/**
	 * Used to on a static field of a class annotated with {@link EMCMapper} to represent the field is an instance of an {@link EMCMapper}. This instance will then be
	 * used instead of attempting to create a new instance of the class.
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Instance {

	}
}