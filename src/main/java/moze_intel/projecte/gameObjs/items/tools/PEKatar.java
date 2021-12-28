package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

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
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		//TODO - 1.18: Validate this is the proper way and if we need to provide access to any other things
		// Also do we need one for our tool itself when digging?
		return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction) || ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction) ||
			   ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction) || ToolActions.DEFAULT_HOE_ACTIONS.contains(toolAction);
	}

	/*@Override//TODO - 1.18: Evaluate using this as the sword handling (we add cobwebs to the tag so we already adjust it in super)
	protected float getShortCutDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		float destroySpeed = super.getShortCutDestroySpeed(stack, state);
		if (destroySpeed == 1) {
			if (state.is(Blocks.COBWEB)) {
				return 15.0F;
			} else {
				Material material = state.getMaterial();
				if (material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !state.is(BlockTags.LEAVES) && material != Material.VEGETABLE) {
					return 1;
				}
				return 1.5F;
			}
		}
		return destroySpeed;
	}*/

	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		InteractionHand hand = context.getHand();
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		ItemStack stack = context.getItemInHand();
		BlockState state = world.getBlockState(pos);
		//Order that it attempts to use the item:
		// Strip logs, hoe ground, carve pumpkin, shear beehive, AOE remove logs, AOE remove leaves
		return ToolHelper.performActions(ToolHelper.stripLogsAOE(context, 0),
				() -> ToolHelper.tillHoeAOE(context, 0),
				() -> {
					if (state.is(Blocks.PUMPKIN)) {
						//Carve pumpkin - copy from Pumpkin Block's onBlockActivated
						if (!world.isClientSide) {
							Direction direction = context.getClickedFace();
							Direction side = direction.getAxis() == Direction.Axis.Y ? context.getHorizontalDirection().getOpposite() : direction;
							world.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1, 1);
							world.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, side), Block.UPDATE_ALL_IMMEDIATE);
							ItemEntity itementity = new ItemEntity(world, pos.getX() + 0.5 + side.getStepX() * 0.65, pos.getY() + 0.1,
									pos.getZ() + 0.5 + side.getStepZ() * 0.65, new ItemStack(Items.PUMPKIN_SEEDS, 4));
							itementity.setDeltaMovement(0.05 * side.getStepX() + world.random.nextDouble() * 0.02, 0.05,
									0.05 * side.getStepZ() + world.random.nextDouble() * 0.02D);
							world.addFreshEntity(itementity);
						}
						return InteractionResult.sidedSuccess(world.isClientSide);
					}
					return InteractionResult.PASS;
				},
				() -> {
					if (state.is(BlockTags.BEEHIVES) && state.getBlock() instanceof BeehiveBlock beehive && state.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
						//Act as shears on beehives
						world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1, 1);
						BeehiveBlock.dropHoneycomb(world, pos);
						if (!CampfireBlock.isSmokeyPos(world, pos)) {
							if (beehive.hiveContainsBees(world, pos)) {
								beehive.angerNearbyBees(world, pos);
							}
							beehive.releaseBeesAndResetHoneyLevel(world, state, pos, player, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
						} else {
							beehive.resetHoneyLevel(world, state, pos);
						}
						return InteractionResult.sidedSuccess(world.isClientSide);
					}
					return InteractionResult.PASS;
				},
				() -> {
					if (state.is(BlockTags.LOGS)) {
						//Mass clear (acting as an axe)
						//Note: We already tried to strip the log in an earlier action
						return ToolHelper.clearTagAOE(world, player, hand, stack, 0, BlockTags.LOGS);
					}
					return InteractionResult.PASS;
				}, () -> {
					if (state.is(BlockTags.LEAVES)) {
						//Mass clear (acting as shears)
						return ToolHelper.clearTagAOE(world, player, hand, stack, 0, BlockTags.LEAVES);
					}
					return InteractionResult.PASS;
				});
	}

	@Override
	public boolean hurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
		//Shear the block instead of breaking it if it supports shearing (and has drops to give) instead of actually breaking it normally
		return ToolHelper.shearBlock(stack, pos, player) == InteractionResult.SUCCESS;
	}

	@Override
	public boolean mineBlock(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity entity) {
		if (state.is(Blocks.TRIPWIRE) && !state.getValue(TripWireBlock.DISARMED)) {
			//Deactivate tripwire
			BlockState deactivated = state.setValue(TripWireBlock.DISARMED, true);
			world.setBlock(pos, deactivated, Block.UPDATE_INVISIBLE);
			return super.mineBlock(stack, world, deactivated, pos, entity);
		}
		return super.mineBlock(stack, world, state, pos, entity);
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
		//Shear entities
		return ItemHelper.actionResultFromType(ToolHelper.shearEntityAOE(player, hand, 0), player.getItemInHand(hand));
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull Player player, InteractionHand hand) {
		if (player.getAttackStrengthScale(0F) == 1) {
			ToolHelper.attackAOE(stack, player, getMode(stack) == 1, ProjectEConfig.server.difficulty.katarDeathAura.get(), 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public int getUseDuration(@Nonnull ItemStack stack) {
		return 72_000;
	}

	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot, ItemStack stack) {
		return attributeCache.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
	}

	/**
	 * Copy of {@link net.minecraft.world.item.ShearsItem#interactLivingEntity(ItemStack, Player, LivingEntity, InteractionHand)}
	 */
	@Nonnull
	@Override
	public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity entity, @Nonnull InteractionHand hand) {
		if (entity instanceof IForgeShearable target) {
			BlockPos pos = entity.blockPosition();
			if (target.isShearable(stack, entity.level, pos)) {
				if (!entity.level.isClientSide) {
					List<ItemStack> drops = target.onSheared(player, stack, entity.level, pos, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack));
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