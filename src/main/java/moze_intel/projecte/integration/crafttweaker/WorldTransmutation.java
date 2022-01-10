package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import moze_intel.projecte.integration.crafttweaker.actions.WorldTransmuteAction;
import net.minecraft.world.level.block.state.BlockState;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/ProjectE/WorldTransmutation")
@ZenCodeType.Name("mods.projecte.WorldTransmutation")
public class WorldTransmutation {

	private WorldTransmutation() {
	}

	/**
	 * Adds an in world transmutation "recipe".
	 *
	 * @param input       {@link BlockState} representing the input or target state.
	 * @param output      {@link BlockState} representing the output state.
	 * @param sneakOutput Optional {@link BlockState} representing the output state when sneaking.
	 */
	@ZenCodeType.Method
	public static void add(BlockState input, BlockState output, @ZenCodeType.Optional BlockState sneakOutput) {
		CraftTweakerAPI.apply(new WorldTransmuteAction.Add(input, output, sneakOutput));
	}

	/**
	 * Removes an existing in world transmutation "recipe".
	 *
	 * @param input       {@link BlockState} representing the input or target state.
	 * @param output      {@link BlockState} representing the output state.
	 * @param sneakOutput Optional {@link BlockState} representing the output state when sneaking.
	 */
	@ZenCodeType.Method
	public static void remove(BlockState input, BlockState output, @ZenCodeType.Optional BlockState sneakOutput) {
		CraftTweakerAPI.apply(new WorldTransmuteAction.Remove(input, output, sneakOutput));
	}

	/**
	 * Removes all existing in world transmutation "recipes".
	 */
	@ZenCodeType.Method
	public static void removeAll() {
		CraftTweakerAPI.apply(new WorldTransmuteAction.RemoveAll());
	}
}