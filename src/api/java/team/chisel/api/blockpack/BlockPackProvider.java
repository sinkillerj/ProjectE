package team.chisel.api.blockpack;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to register a "Block Pack" Provider
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BlockPackProvider {

    /**
     * Name of the block pack provider
     */
    String value();

    /**
     * The Mod that owns this block pack. Not required but may be used in the future
     */
    String owner() default "";

    /**
     * The Mods required for this block pack provider to be used
     */
    String[] modDeps() default {};
}
