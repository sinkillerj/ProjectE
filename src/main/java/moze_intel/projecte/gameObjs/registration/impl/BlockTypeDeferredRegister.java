package moze_intel.projecte.gameObjs.registration.impl;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockTypeDeferredRegister extends PEDeferredRegister<MapCodec<? extends Block>> {

	public BlockTypeDeferredRegister(String modid) {
		super(Registries.BLOCK_TYPE, modid);
	}

	public <BLOCK extends Block> PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BLOCK>> registerSimple(String name, Function<BlockBehaviour.Properties, BLOCK> factory) {
		return register(name, () -> BlockBehaviour.simpleCodec(factory));
	}
}