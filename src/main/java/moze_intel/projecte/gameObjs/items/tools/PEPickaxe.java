package moze_intel.projecte.gameObjs.items.tools;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.blocks.IMatterBlock;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PEPickaxe extends PickaxeItem implements IItemCharge, IItemMode {

	private final EnumMatterType matterType;
	private final String[] modeDesc;
	private final int numCharges;

	public PEPickaxe(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, 4, -2.8F, props);
		this.modeDesc = new String[]{"pe.pick.mode1", "pe.pick.mode2", "pe.pick.mode3", "pe.pick.mode4"};
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
		Block block = state.getBlock();
		if (block instanceof IMatterBlock && ((IMatterBlock) block).getMatterType().getMatterTier() <= matterType.getMatterTier()) {
			return 1_200_000;
		}
		return ToolHelper.getDestroySpeed(super.getDestroySpeed(stack, state), matterType, getCharge(stack));
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return numCharges;
	}

	@Override
	public String[] getModeTranslationKeys() {
		return modeDesc;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new ItemCapabilityWrapper(stack, new ChargeItemCapabilityWrapper(), new ModeChangerItemCapabilityWrapper());
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return ActionResult.newResult(ActionResultType.SUCCESS, stack);
		}
		if (ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
			ToolHelper.mineOreVeinsInAOE(stack, player, hand);
		} else {
			RayTraceResult mop = rayTrace(world, player, RayTraceContext.FluidMode.NONE);
			if (mop instanceof BlockRayTraceResult) {
				if (ItemHelper.isOre(world.getBlockState(((BlockRayTraceResult) mop).getPos()).getBlock())) {
					ToolHelper.tryVeinMine(stack, player, (BlockRayTraceResult) mop);
				}
			}
		}
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public boolean onBlockDestroyed(@Nonnull ItemStack stack, @Nonnull World world, BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity eLiving) {
		ToolHelper.digBasedOnMode(stack, world, state.getBlock(), pos, eLiving, Item::rayTrace);
		return true;
	}
}