package moze_intel.projecte.api.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that this class should be loaded as an {@link IDataComponentProcessor )}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataComponentProcessor {

	/**
	 * Gets the priority of this {@link DataComponentProcessor}. This is used when loading the list of processors. The higher this number is the earlier it gets ran.
	 * Elements with equal priority retain loader discovery order; integrations that require a defined precedence must use distinct priorities.
	 *
	 * @return Sort priority of this {@link DataComponentProcessor}
	 */
	int priority() default 0;

	/**
	 * Array of modids that are required for this {@link DataComponentProcessor} to be loaded, empty String or array for no dependencies.
	 *
	 * @return array of modids.
	 */
	String[] requiredMods() default "";

	/**
	 * Used to on a static field of a class annotated with {@link DataComponentProcessor} to represent the field is an instance of an {@link DataComponentProcessor}. This instance will then
	 * be used instead of attempting to create a new instance of the class.
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Instance {}
}
