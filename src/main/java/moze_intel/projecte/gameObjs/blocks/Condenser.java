package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.block_entities.CondenserBlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;

public class Condenser extends AlchemicalChest {

	public Condenser(Properties props) {
		super(props);
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<? extends CondenserBlockEntity> getType() {
		return PEBlockEntityTypes.CONDENSER;
	}
}