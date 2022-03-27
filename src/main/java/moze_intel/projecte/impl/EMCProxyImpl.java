package moze_intel.projecte.impl;

import java.util.Objects;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.utils.EMCHelper;
import org.jetbrains.annotations.Range;

public class EMCProxyImpl implements IEMCProxy {

	public static final EMCProxyImpl instance = new EMCProxyImpl();

	private EMCProxyImpl() {
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getValue(@Nonnull ItemInfo info) {
		return EMCHelper.getEmcValue(Objects.requireNonNull(info));
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getSellValue(@Nonnull ItemInfo info) {
		return EMCHelper.getEmcSellValue(Objects.requireNonNull(info));
	}

	@Nonnull
	@Override
	public ItemInfo getPersistentInfo(@Nonnull ItemInfo info) {
		return NBTManager.getPersistentInfo(info);
	}
}