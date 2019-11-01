package moze_intel.projecte.capability;

import net.minecraftforge.common.util.LazyOptional;

public abstract class BasicItemCapability<TYPE> extends ItemCapability<TYPE> {

	private final LazyOptional<TYPE> capability = LazyOptional.of(() -> (TYPE) this);

	@Override
	public LazyOptional<TYPE> getLazyCapability() {
		return capability;
	}
}