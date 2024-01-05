package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import java.util.function.Consumer;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.ToolHelper.ChargeAttributeCache;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;

public class PESword extends SwordItem implements IExtraFunction, IItemCharge, IBarHelper {

	private final ChargeAttributeCache attributeCache = new ChargeAttributeCache();
	private final EnumMatterType matterType;
	private final int numCharges;

	public PESword(EnumMatterType matterType, int numCharges, int damage, Properties props) {
		super(matterType, damage, -2.4F, props);
		this.matterType = matterType;
		this.numCharges = numCharges;
	}

	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {
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
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return true;
	}

	@Override
	public float getWidthForBar(ItemStack stack) {
		return 1 - getChargePercent(stack);
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return getScaledBarWidth(stack);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return getColorForBar(stack);
	}

	@Override
	public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
		float speed = super.getDestroySpeed(stack, state);
		if (speed == 1 && state.is(PETags.Blocks.MINEABLE_WITH_PE_SWORD)) {
			speed = matterType.getSpeed();
		}
		return ToolHelper.getDestroySpeed(speed, matterType, getCharge(stack));
	}

	@Override
	public boolean isCorrectToolForDrops(@NotNull ItemStack stack, BlockState state) {
		//Note: our tag intercepts the vanilla sword matches
		return state.is(PETags.Blocks.MINEABLE_WITH_PE_SWORD) && TierSortingRegistry.isCorrectTierForDrops(matterType, state);
	}

	@Override
	public int getNumCharges(@NotNull ItemStack stack) {
		return numCharges;
	}

	@Override
	public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity damaged, @NotNull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@NotNull
	@Override
	public AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target) {
		int charge = getCharge(stack);
		return target.getBoundingBox().inflate(charge, charge / 4D, charge);
	}

	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, InteractionHand hand) {
		if (player.getAttackStrengthScale(0F) == 1) {
			ToolHelper.attackAOE(stack, player, slayAll(stack), getDamage(), 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		}
		return false;
	}

	protected boolean slayAll(@NotNull ItemStack stack) {
		return false;
	}

	@NotNull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot slot, ItemStack stack) {
		return attributeCache.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
	}
}