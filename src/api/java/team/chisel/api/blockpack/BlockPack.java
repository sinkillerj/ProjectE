package team.chisel.api.blockpack;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraftforge.fml.common.Mod;

/**
 * This annotation marks a "Block Pack" for chisel, Chisel will automatically detect it (much like {@link Mod}) and load it.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BlockPack {

	/**
	 * The name of the block pack.
	 */
	String value();
	
	String[] modDeps() default {};
	
	String[] blockPackDeps() default {};
}
