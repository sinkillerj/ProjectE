package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.block_entities.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;

public class CondenserMK2 extends Condenser {

	public CondenserMK2(Properties props) {
		super(props);
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<CondenserMK2Tile> getType() {
		return PEBlockEntityTypes.CONDENSER_MK2;
	}
}