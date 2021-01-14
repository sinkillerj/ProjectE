package moze_intel.projecte.gameObjs.items.tools;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PEShears extends ShearsItem implements IItemCharge {

	private final EnumMatterType matterType;
	private final int numCharges;

	public PEShears(EnumMatterType matterType, int numCharges, Properties props) {
		super(props.addToolType(ToolHelper.TOOL_TYPE_SHEARS, matterType.getHarvestLevel()));
		this.matterType = matterType;
		this.numCharges = numCharges;
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
		return new ItemCapabilityWrapper(stack, new ChargeItemCapabilityWrapper());
	}

	@Override
	public boolean canHarvestBlock(BlockState state) {
		if (state.getHarvestTool() == ToolHelper.TOOL_TYPE_SHEARS) {
			//Patch ShearsItem to return true for canHarvestBlock if a mod adds a block with the harvest tool of shears
			return matterType.getHarvestLevel() >= state.getHarvestLevel();
		}
		return super.canHarvestBlock(state);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
		return ItemHelper.actionResultFromType(ToolHelper.shearEntityAOE(player, hand, 0), player.getHeldItem(hand));
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
		//Shear the block instead of breaking it if it supports shearing (and has drops to give) instead of actually breaking it normally
		return ToolHelper.shearBlock(stack, pos, player) == ActionResultType.SUCCESS;
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if (player != null) {
			World world = context.getWorld();
			BlockState state = world.getBlockState(context.getPos());
			if (state.isIn(BlockTags.LEAVES)) {
				//Mass clear leaves
				ToolHelper.clearTagAOE(world, player, context.getHand(), context.getItem(), 0, BlockTags.LEAVES);
			}
		}
		return ActionResultType.PASS;
	}
}