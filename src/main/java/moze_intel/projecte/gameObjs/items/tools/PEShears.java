package moze_intel.projecte.gameObjs.items.tools;

import java.util.function.Consumer;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;

public class PEShears extends ShearsItem implements IItemCharge, IBarHelper {

	private final EnumMatterType matterType;
	private final int numCharges;

	public PEShears(EnumMatterType matterType, int numCharges, Properties props) {
		super(props);
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
		if (speed == 1 && state.is(PETags.Blocks.MINEABLE_WITH_PE_SHEARS)) {
			speed = matterType.getSpeed();
		}
		return ToolHelper.getDestroySpeed(speed, matterType, getCharge(stack));
	}

	@Override
	public int getNumCharges(@NotNull ItemStack stack) {
		return numCharges;
	}

	@Override
	public boolean isCorrectToolForDrops(@NotNull ItemStack stack, BlockState state) {
		//Note: our tag intercepts the vanilla shears matches
		return state.is(PETags.Blocks.MINEABLE_WITH_PE_SHEARS) && TierSortingRegistry.isCorrectTierForDrops(matterType, state);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		return ItemHelper.actionResultFromType(ToolHelper.shearEntityAOE(player, hand, 0), player.getItemInHand(hand));
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
		//Shear the block instead of breaking it if it supports shearing (and has drops to give) instead of actually breaking it normally
		return ToolHelper.shearBlock(stack, pos, player).consumesAction();
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player != null) {
			Level level = context.getLevel();
			BlockState state = level.getBlockState(context.getClickedPos());
			if (state.is(BlockTags.LEAVES)) {
				//Mass clear leaves
				ToolHelper.clearTagAOE(level, player, context.getHand(), context.getItemInHand(), 0, BlockTags.LEAVES);
			}
		}
		return InteractionResult.PASS;
	}
}