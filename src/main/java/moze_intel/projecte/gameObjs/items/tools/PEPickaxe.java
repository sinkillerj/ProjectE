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
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PEPickaxe extends PickaxeItem implements IItemCharge, IItemMode {

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
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1.0D - getChargePercent(stack);
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
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new ItemCapabilityWrapper(stack, new ChargeItemCapabilityWrapper(), new ModeChangerItemCapabilityWrapper());
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> use(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (ProjectEConfig.server.items.pickaxeAoeVeinMining.get()) {
			//If we are supposed to mine in an AOE then attempt to do so
			return ItemHelper.actionResultFromType(ToolHelper.mineOreVeinsInAOE(player, hand), stack);
		}
		return ActionResult.pass(stack);
	}

	@Nonnull
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if (player == null || ProjectEConfig.server.items.pickaxeAoeVeinMining.get()) {
			//If we don't have a player or the config says we should mine in an AOE (this happens when right clicking air as well)
			// Then we just pass so that it can be processed in onItemRightClick
			return ActionResultType.PASS;
		}
		BlockPos pos = context.getClickedPos();
		if (ItemHelper.isOre(context.getLevel().getBlockState(pos))) {
			return ToolHelper.tryVeinMine(player, context.getItemInHand(), pos, context.getClickedFace());
		}
		return ActionResultType.PASS;
	}

	@Override
	public boolean mineBlock(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity living) {
		ToolHelper.digBasedOnMode(stack, world, pos, living, Item::getPlayerPOVHitResult);
		return true;
	}
}