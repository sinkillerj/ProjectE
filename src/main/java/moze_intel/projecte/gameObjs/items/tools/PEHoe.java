package moze_intel.projecte.gameObjs.items.tools;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PEHoe extends HoeItem implements IItemCharge {

	private final EnumMatterType matterType;
	private final int numCharges;

	public PEHoe(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, matterType.ordinal() + 1, props);
		this.matterType = matterType;
		this.numCharges = numCharges;
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return 0;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1.0D - getChargePercent(stack);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		return ToolHelper.getDestroySpeed(super.getDestroySpeed(stack, state), matterType, getCharge(stack));
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return numCharges;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new ItemCapabilityWrapper(stack, new ChargeItemCapabilityWrapper());
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		ToolHelper.tillAOE(ctx.getHand(), ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getFace(), 0);
		return ActionResultType.SUCCESS;
	}
}