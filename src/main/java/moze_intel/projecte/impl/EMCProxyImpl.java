package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EMCProxyImpl implements IEMCProxy {

	public static final EMCProxyImpl instance = new EMCProxyImpl();

	private EMCProxyImpl() {
	}

	@Override
	public boolean hasValue(@Nonnull Block block) {
		Preconditions.checkNotNull(block);
		return EMCHelper.doesItemHaveEmc(block);
	}

	@Override
	public boolean hasValue(@Nonnull Item item) {
		Preconditions.checkNotNull(item);
		return EMCHelper.doesItemHaveEmc(item);
	}

	@Override
	public boolean hasValue(@Nonnull ItemStack stack) {
		Preconditions.checkNotNull(stack);
		return EMCHelper.doesItemHaveEmc(stack);
	}

	@Override
	public long getValue(@Nonnull Block block) {
		Preconditions.checkNotNull(block);
		return EMCHelper.getEmcValue(block);
	}

	@Override
	public long getValue(@Nonnull Item item) {
		Preconditions.checkNotNull(item);
		return EMCHelper.getEmcValue(item);
	}

	@Override
	public long getValue(@Nonnull ItemStack stack) {
		Preconditions.checkNotNull(stack);
		return EMCHelper.getEmcValue(stack);
	}

	@Override
	public long getSellValue(@Nonnull ItemStack stack) {
		Preconditions.checkNotNull(stack);
		return EMCHelper.getEmcSellValue(stack);
	}
}