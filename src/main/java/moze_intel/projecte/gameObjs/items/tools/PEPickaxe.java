package moze_intel.projecte.gameObjs.items.tools;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PEPickaxe extends PickaxeItem implements IItemCharge, IItemMode, IBarHelper {

	private final EnumMatterType matterType;
	private final ILangEntry[] modeDesc;
	private final int numCharges;

	public PEPickaxe(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, 4, -2.8F, props);
		this.modeDesc = new ILangEntry[]{PELang.MODE_PICK_1, PELang.MODE_PICK_2, PELang.MODE_PICK_3, PELang.MODE_PICK_4};
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
	public boolean isBarVisible(@Nonnull ItemStack stack) {
		return true;
	}

	@Override
	public float getWidthForBar(ItemStack stack) {
		return 1 - getChargePercent(stack);
	}

	@Override
	public int getBarWidth(@Nonnull ItemStack stack) {
		return getScaledBarWidth(stack);
	}

	@Override
	public int getBarColor(@Nonnull ItemStack stack) {
		return getColorForBar(stack);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		if (ToolHelper.canMatterMine(matterType, state.getBlock())) {
			return 1_200_000;
		}
		return ToolHelper.getDestroySpeed(super.getDestroySpeed(stack, state), matterType, getCharge(stack));
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return numCharges;
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modeDesc;
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return new ItemCapabilityWrapper(stack, new ChargeItemCapabilityWrapper(), new ModeChangerItemCapabilityWrapper());
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (ProjectEConfig.server.items.pickaxeAoeVeinMining.get()) {
			//If we are supposed to mine in an AOE then attempt to do so
			return ItemHelper.actionResultFromType(ToolHelper.mineOreVeinsInAOE(player, hand), stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null || ProjectEConfig.server.items.pickaxeAoeVeinMining.get()) {
			//If we don't have a player or the config says we should mine in an AOE (this happens when right clicking air as well)
			// Then we just pass so that it can be processed in onItemRightClick
			return InteractionResult.PASS;
		}
		BlockPos pos = context.getClickedPos();
		if (ItemHelper.isOre(context.getLevel().getBlockState(pos))) {
			return ToolHelper.tryVeinMine(player, context.getItemInHand(), pos, context.getClickedFace());
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean mineBlock(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity living) {
		ToolHelper.digBasedOnMode(stack, world, pos, living, Item::getPlayerPOVHitResult);
		return true;
	}
}