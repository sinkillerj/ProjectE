package moze_intel.projecte.gameObjs.items.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class PETool extends ToolItem implements IItemCharge {

	private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();
	protected final EnumMatterType matterType;
	private final int numCharges;

	public PETool(EnumMatterType matterType, float damage, float attackSpeed, int numCharges, Properties props) {
		super(damage, attackSpeed, matterType, new HashSet<>(), props);
		this.matterType = matterType;
		this.numCharges = numCharges;
		addItemCapability(ChargeItemCapabilityWrapper::new);
	}

	protected void addItemCapability(Supplier<ItemCapability<?>> capabilitySupplier) {
		supportedCapabilities.add(capabilitySupplier);
	}

	@Override
	public boolean isBookEnchantable(@Nonnull ItemStack stack, @Nonnull ItemStack book) {
		return false;
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
		return ToolHelper.getDestroySpeed(getShortCutDestroySpeed(stack, state), matterType, getCharge(stack));
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return numCharges;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		if (supportedCapabilities.isEmpty()) {
			return super.initCapabilities(stack, nbt);
		}
		return new ItemCapabilityWrapper(stack, supportedCapabilities);
	}

	@Override
	public boolean canHarvestBlock(@Nonnull ItemStack stack, BlockState state) {
		//Note: We override the more specific implementation as we need the stack to get our tool's supported ToolTypes
		ToolType requiredTool = state.getHarvestTool();
		for (ToolType toolType : getToolTypes(stack)) {
			if (toolType == requiredTool) {
				//Patch ToolItem to return true for canHarvestBlock when the block's harvest tool matches one of our supported tools
				return getHarvestLevel(stack, toolType, null, state) >= state.getHarvestLevel();
			}
		}
		return super.canHarvestBlock(stack, state);
	}

	/**
	 * Override this if we need to also include any "shortcuts" that specific vanilla tool types include for specific blocks/material types.
	 *
	 * For example: Axes are "effective" on all types of wood, but do not automatically allow HARVESTING of all types of wood.
	 */
	protected float getShortCutDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		return super.getDestroySpeed(stack, state);
	}
}