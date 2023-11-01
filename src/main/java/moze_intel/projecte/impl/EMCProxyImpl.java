package moze_intel.projecte.impl;

import java.util.Objects;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.utils.EMCHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class EMCProxyImpl implements IEMCProxy {

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getValue(@NotNull ItemInfo info) {
		return EMCHelper.getEmcValue(Objects.requireNonNull(info));
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getSellValue(@NotNull ItemInfo info) {
		return EMCHelper.getEmcSellValue(Objects.requireNonNull(info));
	}

	@NotNull
	@Override
	public ItemInfo getPersistentInfo(@NotNull ItemInfo info) {
		return NBTManager.getPersistentInfo(info);
	}
}