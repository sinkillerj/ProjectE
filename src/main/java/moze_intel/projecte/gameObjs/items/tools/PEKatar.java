package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class PEKatar extends PETool implements IItemMode, IExtraFunction {

	private final String[] modeDesc;

	public PEKatar(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, 19, -2.4F, numCharges, props
				.addToolType(ToolType.AXE, matterType.getHarvestLevel())
				.addToolType(ToolHelper.TOOL_TYPE_SHEARS, matterType.getHarvestLevel())
				.addToolType(ToolHelper.TOOL_TYPE_HOE, matterType.getHarvestLevel())
				.addToolType(ToolHelper.TOOL_TYPE_KATAR, matterType.getHarvestLevel()));
		modeDesc = new String[]{"pe.katar.mode1", "pe.katar.mode2"};
		addItemCapability(new ModeChangerItemCapabilityWrapper());
		addItemCapability(new ExtraFunctionItemCapabilityWrapper());
	}

	@Override
	public String[] getModeTranslationKeys() {
		return modeDesc;
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
	public boolean canHarvestBlock(BlockState state) {
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
			if (material == Material.WOOD || material == Material.PLANTS || material == Material.TALL_PLANTS || material == Material.BAMBOO) {
				return efficiency;
			}
			//Shear destroy speed type shortcut check (we do not need to check cobwebs as that is covered by the canHarvestBlock override)
			if (state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.WOOL)) {
				//Note: We just return our efficiency here even though vanilla shears are hardcoded to 15 for leaves, and 5 for wool
				return efficiency;
			}
			//Note: We do not need to bother with a shortcut check for hoes as they do not have any
		}
		return destroySpeed;
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		//TODO: Allow for mass stripping of logs
		//Copied from AxeItem#onItemUse
		World world = context.getWorld();
		BlockPos blockpos = context.getPos();
		BlockState blockstate = world.getBlockState(blockpos);
		Block block = AxeItem.BLOCK_STRIPPING_MAP.get(blockstate.getBlock());
		if (block == null) {
			return ActionResultType.PASS;
		}
		PlayerEntity playerentity = context.getPlayer();
		world.playSound(playerentity, blockpos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
		if (!world.isRemote) {
			world.setBlockState(blockpos, block.getDefaultState().with(RotatedPillarBlock.AXIS, blockstate.get(RotatedPillarBlock.AXIS)), 11);
			if (playerentity != null) {
				context.getItem().damageItem(1, playerentity, onBroken -> onBroken.sendBreakAnimation(context.getHand()));
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
		//Shear the block instead of breaking it if it supports shearing
		ToolHelper.shearBlock(stack, pos, player);
		return false;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return ActionResult.newResult(ActionResultType.SUCCESS, stack);
		}
		RayTraceResult mop = rayTrace(world, player, RayTraceContext.FluidMode.NONE);
		if (mop instanceof BlockRayTraceResult) {
			BlockRayTraceResult rtr = (BlockRayTraceResult) mop;
			BlockState state = world.getBlockState(rtr.getPos());
			Block blockHit = state.getBlock();
			if (blockHit instanceof GrassBlock || blockHit == Blocks.DIRT) {
				// Hoe
				//TODO: FIXME, when it gets hoed the block blinks
				ToolHelper.tillAOE(hand, player, world, rtr.getPos(), rtr.getFace(), 0);
			} else if (BlockTags.LOGS.contains(blockHit)) {
				// Axe
				ToolHelper.clearTagAOE(world, stack, player, BlockTags.LOGS, 0, hand);
			} else if (BlockTags.LEAVES.contains(blockHit)) {
				// Shear leaves
				ToolHelper.clearTagAOE(world, stack, player, BlockTags.LEAVES, 0, hand);
			}
		} else {
			// Shear
			ToolHelper.shearEntityAOE(stack, player, 0, hand);
		}
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand) {
		if (player.getCooledAttackStrength(0F) == 1) {
			ToolHelper.attackAOE(stack, player, getMode(stack) == 1, ProjectEConfig.difficulty.katarDeathAura.get().floatValue(), 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72_000;
	}

	//TODO: Decide if this impl or the one in PESword is better
	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		Multimap<String, AttributeModifier> attributes = super.getAttributeModifiers(slot, stack);
		if (slot == EquipmentSlotType.MAINHAND) {
			int charge = getCharge(stack);
			if (charge > 0) {
				//If we have any charge take it into account for calculating the damage
				attributes.remove(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "DUMMY", 0, Operation.ADDITION));
				attributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", attackDamage + charge, Operation.ADDITION));
			}
		}
		return attributes;
	}
}