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
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.ToolHelper.ChargeAttributeCache;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.TripWireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants.BlockFlags;

public class PEKatar extends PETool implements IItemMode, IExtraFunction {

	private final ChargeAttributeCache attributeCache = new ChargeAttributeCache();
	private final ILangEntry[] modeDesc;

	public PEKatar(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, 19, -2.4F, numCharges, props
				.addToolType(ToolType.AXE, matterType.getLevel())
				.addToolType(ToolHelper.TOOL_TYPE_SHEARS, matterType.getLevel())
				.addToolType(ToolType.HOE, matterType.getLevel())
				.addToolType(ToolHelper.TOOL_TYPE_KATAR, matterType.getLevel()));
		modeDesc = new ILangEntry[]{PELang.MODE_KATAR_1, PELang.MODE_KATAR_2};
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
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

	/**
	 * Simple copy of {@link net.minecraft.item.ShearsItem#canHarvestBlock(BlockState)}'s fallback/shortcut to allow the katar to also mine all blocks of that type.
	 *
	 * Note: We do not also include {@link net.minecraft.item.AxeItem}'s check or {@link net.minecraft.item.HoeItem}'s check as it does not have any specific
	 * overrides/shortcuts for checking harvest-ability.
	 *
	 * @implNote This method is overridden instead of {@link net.minecraftforge.common.extensions.IForgeItem#canHarvestBlock(ItemStack, BlockState)} so that it is used as
	 * a fallback if {@link PETool#canHarvestBlock(ItemStack, BlockState)} does not find a matching tool/required level for the tool. As the default implementation that
	 * gets used is one where the stack does not matter (which would be this)
	 */
	@Override
	public boolean isCorrectToolForDrops(BlockState state) {
		Block block = state.getBlock();
		//Shears check
		return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
	}

	@Override
	protected float getShortCutDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		float destroySpeed = super.getShortCutDestroySpeed(stack, state);
		if (destroySpeed == 1) {
			Material material = state.getMaterial();
			//Axe destroy speed type shortcut check
			if (material == Material.WOOD || material == Material.PLANT || material == Material.REPLACEABLE_PLANT || material == Material.BAMBOO) {
				return speed;
			}
			//Shear destroy speed type shortcut check (we do not need to check cobwebs as that is covered by the canHarvestBlock override)
			if (state.is(BlockTags.LEAVES) || state.is(BlockTags.WOOL)) {
				//Note: We just return our efficiency here even though vanilla shears are hardcoded to 15 for leaves, and 5 for wool
				return speed;
			}
			//Note: We do not need to bother with a shortcut check for hoes as they do not have any
		}
		return destroySpeed;
	}

	@Nonnull
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if (player == null) {
			return ActionResultType.PASS;
		}
		Hand hand = context.getHand();
		World world = context.getLevel();
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
							world.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundCategory.BLOCKS, 1, 1);
							world.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, side), BlockFlags.DEFAULT_AND_RERENDER);
							ItemEntity itementity = new ItemEntity(world, pos.getX() + 0.5 + side.getStepX() * 0.65, pos.getY() + 0.1,
									pos.getZ() + 0.5 + side.getStepZ() * 0.65, new ItemStack(Items.PUMPKIN_SEEDS, 4));
							itementity.setDeltaMovement(0.05 * side.getStepX() + world.random.nextDouble() * 0.02, 0.05,
									0.05 * side.getStepZ() + world.random.nextDouble() * 0.02D);
							world.addFreshEntity(itementity);
						}
						return ActionResultType.sidedSuccess(world.isClientSide);
					}
					return ActionResultType.PASS;
				},
				() -> {
					if (state.is(BlockTags.BEEHIVES) && state.getBlock() instanceof BeehiveBlock && state.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
						//Act as shears on beehives
						BeehiveBlock beehive = (BeehiveBlock) state.getBlock();
						world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1, 1);
						BeehiveBlock.dropHoneycomb(world, pos);
						if (!CampfireBlock.isSmokeyPos(world, pos)) {
							if (beehive.hiveContainsBees(world, pos)) {
								beehive.angerNearbyBees(world, pos);
							}
							beehive.releaseBeesAndResetHoneyLevel(world, state, pos, player, BeehiveTileEntity.State.EMERGENCY);
						} else {
							beehive.resetHoneyLevel(world, state, pos);
						}
						return ActionResultType.sidedSuccess(world.isClientSide);
					}
					return ActionResultType.PASS;
				},
				() -> {
					if (state.is(BlockTags.LOGS)) {
						//Mass clear (acting as an axe)
						//Note: We already tried to strip the log in an earlier action
						return ToolHelper.clearTagAOE(world, player, hand, stack, 0, BlockTags.LOGS);
					}
					return ActionResultType.PASS;
				}, () -> {
					if (state.is(BlockTags.LEAVES)) {
						//Mass clear (acting as shears)
						return ToolHelper.clearTagAOE(world, player, hand, stack, 0, BlockTags.LEAVES);
					}
					return ActionResultType.PASS;
				});
	}

	@Override
	public boolean hurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
		//Shear the block instead of breaking it if it supports shearing (and has drops to give) instead of actually breaking it normally
		return ToolHelper.shearBlock(stack, pos, player) == ActionResultType.SUCCESS;
	}

	@Override
	public boolean mineBlock(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity entity) {
		if (state.is(Blocks.TRIPWIRE) && !state.getValue(TripWireBlock.DISARMED)) {
			//Deactivate tripwire
			BlockState deactivated = state.setValue(TripWireBlock.DISARMED, true);
			world.setBlock(pos, deactivated, BlockFlags.NO_RERENDER);
			return super.mineBlock(stack, world, deactivated, pos, entity);
		}
		return super.mineBlock(stack, world, state, pos, entity);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> use(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
		//Shear entities
		return ItemHelper.actionResultFromType(ToolHelper.shearEntityAOE(player, hand, 0), player.getItemInHand(hand));
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand) {
		if (player.getAttackStrengthScale(0F) == 1) {
			ToolHelper.attackAOE(stack, player, getMode(stack) == 1, ProjectEConfig.server.difficulty.katarDeathAura.get(), 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public UseAction getUseAnimation(@Nonnull ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getUseDuration(@Nonnull ItemStack stack) {
		return 72_000;
	}

	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		return attributeCache.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
	}

	/**
	 * Copy of {@link net.minecraft.item.ShearsItem#itemInteractionForEntity(ItemStack, PlayerEntity, LivingEntity, Hand)}
	 */
	@Nonnull
	@Override
	public ActionResultType interactLivingEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nonnull LivingEntity entity, @Nonnull Hand hand) {
		if (entity instanceof IForgeShearable) {
			IForgeShearable target = (IForgeShearable) entity;
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
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}
}