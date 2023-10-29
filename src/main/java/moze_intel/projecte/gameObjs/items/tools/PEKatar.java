package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Random;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.ToolHelper.ChargeAttributeCache;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PEKatar extends PETool implements IItemMode, IExtraFunction {

	private final ChargeAttributeCache attributeCache = new ChargeAttributeCache();
	private final ILangEntry[] modeDesc;

	public PEKatar(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, PETags.Blocks.MINEABLE_WITH_PE_KATAR, 19, -2.4F, numCharges, props);
		modeDesc = new ILangEntry[]{PELang.MODE_KATAR_1, PELang.MODE_KATAR_2};
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modeDesc;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction) || ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction) ||
			   ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction) || ToolActions.DEFAULT_HOE_ACTIONS.contains(toolAction) ||
			   ToolHelper.DEFAULT_PE_KATAR_ACTIONS.contains(toolAction);
	}

	@NotNull
	@Override
	public AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target) {
		int charge = getCharge(stack);
		return target.getBoundingBox().inflate(charge, charge / 4D, charge);
	}

	@Override
	protected float getShortCutDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
		float destroySpeed = super.getShortCutDestroySpeed(stack, state);
		if (destroySpeed == 1) {
			//Special handling for swords which still have hardcoded material checks
			// Note: we don't bother with the cobweb check because that will get caught by the tag for the blocks we can mine,
			// but we do need to include the material based checks that vanilla's sword still has
			if (state.is(BlockTags.SWORD_EFFICIENT)) {
				return 1.5F;
			}
		}
		return destroySpeed;
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		//Order that it attempts to use the item:
		// Strip logs, hoe ground, carve pumpkin, shear beehive, AOE remove logs, AOE remove leaves
		return ToolHelper.performActions(ToolHelper.stripLogsAOE(context, state, 0),
				() -> ToolHelper.scrapeAOE(context, state, 0),
				() -> ToolHelper.waxOffAOE(context, state, 0),
				() -> ToolHelper.tillAOE(context, state, 0),
				() -> {
					if (state.is(BlockTags.LOGS)) {
						//Mass clear (acting as an axe)
						//Note: We already tried to strip the log in an earlier action
						return ToolHelper.clearTagAOE(level, player, context.getHand(), context.getItemInHand(), 0, BlockTags.LOGS);
					}
					return InteractionResult.PASS;
				}, () -> {
					if (state.is(BlockTags.LEAVES)) {
						//Mass clear (acting as shears)
						return ToolHelper.clearTagAOE(level, player, context.getHand(), context.getItemInHand(), 0, BlockTags.LEAVES);
					}
					return InteractionResult.PASS;
				});
	}

	@Override
	public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity damaged, @NotNull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
		//Shear the block instead of breaking it if it supports shearing (and has drops to give) instead of actually breaking it normally
		return ToolHelper.shearBlock(stack, pos, player).consumesAction();
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		//Shear entities
		return ItemHelper.actionResultFromType(ToolHelper.shearEntityAOE(player, hand, 0), player.getItemInHand(hand));
	}

	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, InteractionHand hand) {
		if (player.getAttackStrengthScale(0F) == 1) {
			ToolHelper.attackAOE(stack, player, getMode(stack) == 1, ProjectEConfig.server.difficulty.katarDeathAura.get(), 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		}
		return false;
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(@NotNull ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack) {
		return 72_000;
	}

	@NotNull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot slot, ItemStack stack) {
		return attributeCache.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
	}

	/**
	 * Copy of {@link net.minecraft.world.item.ShearsItem#interactLivingEntity(ItemStack, Player, LivingEntity, InteractionHand)}
	 */
	@NotNull
	@Override
	public InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
		if (entity instanceof IForgeShearable target) {
			BlockPos pos = entity.blockPosition();
			if (target.isShearable(stack, entity.level(), pos)) {
				if (!entity.level().isClientSide) {
					List<ItemStack> drops = target.onSheared(player, stack, entity.level(), pos, stack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE));
					Random rand = new Random();
					drops.forEach(d -> {
						ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
						if (ent != null) {
							ent.setDeltaMovement(ent.getDeltaMovement().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F,
									(rand.nextFloat() - rand.nextFloat()) * 0.1F));
						}
					});
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}
}