package moze_intel.projecte.gameObjs.registries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.blocks.TransmutationStone;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.BlockTypeDeferredRegister;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PEBlockTypes {

	private PEBlockTypes() {
	}

	public static final BlockTypeDeferredRegister BLOCK_TYPES = new BlockTypeDeferredRegister(PECore.MODID);

	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<TransmutationStone>> TRANSMUTATION_TABLE = BLOCK_TYPES.registerSimple("transmutation_table", TransmutationStone::new);
	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<MatterFurnace>> MATTER_FURNACE = BLOCK_TYPES.register("matter_furnace", () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockBehaviour.propertiesCodec(),
			EnumMatterType.CODEC.fieldOf("type").forGetter(MatterFurnace::getMatterType)
	).apply(instance, MatterFurnace::new)));
}