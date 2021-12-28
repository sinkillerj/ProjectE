package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.block_entities.InterdictionTile;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;

public interface InterdictionTorchEntityBlock extends PEEntityBlock<InterdictionTile> {

	@Nullable
	@Override
	default BlockEntityTypeRegistryObject<InterdictionTile> getType() {
		return PEBlockEntityTypes.INTERDICTION_TORCH;
	}

	class InterdictionTorch extends TorchBlock implements InterdictionTorchEntityBlock {

		public InterdictionTorch(Properties props) {
			super(props, ParticleTypes.SOUL_FIRE_FLAME);
		}
	}

	class InterdictionTorchWall extends WallTorchBlock implements InterdictionTorchEntityBlock {

		public InterdictionTorchWall(Properties props) {
			super(props, ParticleTypes.SOUL_FIRE_FLAME);
		}
	}
}