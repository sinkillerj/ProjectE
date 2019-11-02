package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PESword extends SwordItem implements IExtraFunction, IItemCharge {

	private final List<ItemCapability<?>> supportedCapabilities = new ArrayList<>();
	private final EnumMatterType matterType;
	private final int numCharges;

	public PESword(EnumMatterType matterType, int numCharges, int damage, Properties props) {
		super(matterType, damage, -2.4F, props);
		this.matterType = matterType;
		this.numCharges = numCharges;
		addItemCapability(new ChargeItemCapabilityWrapper());
		addItemCapability(new ExtraFunctionItemCapabilityWrapper());
	}

	protected <TYPE> void addItemCapability(ItemCapability<TYPE> capability) {
		supportedCapabilities.add(capability);
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
		//TODO: Re-evaluate this
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

	//TODO: Decide if this impl or the one in PEHammer is better
	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		int charge;
		if (slot != EquipmentSlotType.MAINHAND || (charge = getCharge(stack)) == 0) {
			//If we are not in the correct hand OR we have no charge just fallback as the calculations are correct
			return super.getAttributeModifiers(slot, stack);
		}
		//We manually add both the proper attack damage and the attack speed so that we don't have to deal with removing the existing
		// attack damage value each time this method gets called just to make the charge get taken into effect.
		// NOTE: This has the side effect that if something adds a new modifier in a higher class it will not automatically be found
		// but this is not a large deal and only really needs to be double checked between MC versions
		Multimap<String, AttributeModifier> attributes = HashMultimap.create();
		attributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier",
				getAttackDamage() + charge, Operation.ADDITION));
		attributes.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, Operation.ADDITION));
		return attributes;
	}
}