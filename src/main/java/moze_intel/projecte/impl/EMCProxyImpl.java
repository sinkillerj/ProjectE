package moze_intel.projecte.impl;

import java.util.Objects;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.utils.EMCHelper;

public class EMCProxyImpl implements IEMCProxy {

	public static final EMCProxyImpl instance = new EMCProxyImpl();

	private EMCProxyImpl() {
	}

	@Override
	public long getValue(@Nonnull ItemInfo info) {
		return EMCHelper.getEmcValue(Objects.requireNonNull(info));
	}

	@Override
	public long getSellValue(@Nonnull ItemInfo info) {
		return EMCHelper.getEmcSellValue(Objects.requireNonNull(info));
	}
}