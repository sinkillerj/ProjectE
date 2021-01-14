package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.ToolHelper.ChargeAttributeCache;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PESword extends SwordItem implements IExtraFunction, IItemCharge {

	private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();
	private final ChargeAttributeCache attributeCache = new ChargeAttributeCache();
	private final EnumMatterType matterType;
	private final int numCharges;

	public PESword(EnumMatterType matterType, int numCharges, int damage, Properties props) {
		super(matterType, damage, -2.4F, props);
		this.matterType = matterType;
		this.numCharges = numCharges;
		addItemCapability(ChargeItemCapabilityWrapper::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
	}

	protected void addItemCapability(Supplier<ItemCapability<?>> capabilitySupplier) {
		supportedCapabilities.add(capabilitySupplier);
	}

	@Override
	public boolean isEnchantable(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
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
		return ToolHelper.getDestroySpeed(super.getDestroySpeed(stack, state), matterType, getCharge(stack));
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
	public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand) {
		if (player.getCooledAttackStrength(0F) == 1) {
			ToolHelper.attackAOE(stack, player, slayAll(stack), getAttackDamage(), 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		}
		return false;
	}

	protected boolean slayAll(@Nonnull ItemStack stack) {
		return false;
	}

	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		return attributeCache.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
	}
}