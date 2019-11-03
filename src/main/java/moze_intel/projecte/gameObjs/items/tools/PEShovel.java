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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PEShovel extends ShovelItem implements IItemCharge {

	private final EnumMatterType matterType;
	private final int numCharges;

	public PEShovel(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, 2, -3, props);
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

	@Override
	public boolean canHarvestBlock(BlockState state) {
		if (state.getHarvestTool() == ToolType.SHOVEL) {
			//Patch ShovelItem to return true for canHarvestBlock for more things than just snow
			return getTier().getHarvestLevel() >= state.getHarvestLevel();
		}
		return super.canHarvestBlock(state);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		Hand hand = ctx.getHand();
		PlayerEntity player = ctx.getPlayer();
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		Direction sideHit = ctx.getFace();
		ActionResultType result = ToolHelper.tillShovelAOE(hand, player, world, pos, sideHit, 0);
		if (player == null) {
			return result;
		}
		return ToolHelper.performActions(result, () -> {
			if (world.getBlockState(pos).isIn(Tags.Blocks.GRAVEL)) {
				return ToolHelper.tryVeinMine(player.getHeldItem(hand), player, pos, sideHit);
			}
			return ActionResultType.PASS;
		}, () -> ToolHelper.digAOE(player.getHeldItem(hand), world, player, false, 0, hand, Item::rayTrace));
	}
}