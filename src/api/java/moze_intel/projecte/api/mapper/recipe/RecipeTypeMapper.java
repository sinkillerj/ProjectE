package moze_intel.projecte.api.mapper.recipe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that this class should be loaded as an {@link IRecipeTypeMapper)}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecipeTypeMapper {

	/**
	 * Gets the priority of this {@link RecipeTypeMapper}. This is used when loading the list of recipe type mappers. The higher this number is the earlier it gets ran.
	 *
	 * @return Sort priority of this {@link RecipeTypeMapper}
	 */
	int priority() default 0;

	/**
	 * Array of modids that are required for this {@link RecipeTypeMapper} to be loaded, empty String or array for no dependencies.
	 *
	 * @return array of modids.
	 */
	String[] requiredMods() default "";

	/**
	 * Used to on a static field of a class annotated with {@link RecipeTypeMapper} to represent the field is an instance of an {@link RecipeTypeMapper}. This instance
	 * will then be used instead of attempting to create a new instance of the class.
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Instance {}
}