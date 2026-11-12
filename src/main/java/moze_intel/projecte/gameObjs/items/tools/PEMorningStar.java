package moze_intel.projecte.gameObjs.items.tools;

import java.util.function.Consumer;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.IMatterType;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.items.IHasConditionalAttributes;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.tools.PEPickaxe.PickaxeMode;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.NotNull;

public class PEMorningStar extends PETool implements IItemMode<PickaxeMode>, IHasConditionalAttributes {

	public PEMorningStar(IMatterType matterType, int numCharges, Properties props) {
		super(matterType, PETags.Blocks.MINEABLE_WITH_PE_MORNING_STAR, numCharges, props.attributes(PETool.createAttributes(matterType, 16, -3))
				.component(PEDataComponentTypes.PICKAXE_MODE, PickaxeMode.STANDARD)
		);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, display, tooltip, flags);
		tooltip.accept(getToolTip(stack));
	}

	@Override
	public boolean canPerformAction(@NotNull ItemInstance stack, @NotNull ItemAbility toolAction) {
		return ToolHelper.MORNING_STAR_TOOL_ACTIONS.contains(toolAction);
	}

	@Override
	public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity damaged, @NotNull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
	}

	@Override
	public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity living) {
		ToolHelper.digBasedOnMode(stack, level, pos, living, Item::getPlayerPOVHitResult, getMode(stack));
		return true;
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		BlockState blockState = level.getBlockState(context.getClickedPos());
		//Order that it attempts to use the item:
		// Till (Shovel), Vein (or AOE) mine gravel/clay, vein mine ore, AOE dig (if it is sand, dirt, or grass don't do depth)
		return ToolHelper.performActions(context, blockState, ToolHelper.flattenAOE(context, blockState, 0),
				ToolHelper::dowseCampfire,
				(ctx, state) -> {
					if (state.is(PETags.Blocks.VEIN_SHOVEL)) {
						if (ProjectEConfig.server.items.pickaxeAoeVeinMining.get()) {
							return ToolHelper.digAOE(ctx.getLevel(), ctx.getPlayer(), ctx.getHand(), ctx.getItemInHand(), ctx.getClickedPos(), ctx.getClickedFace(), false, 0);
						}
						return ToolHelper.tryVeinMine(ctx.getPlayer(), ctx.getItemInHand(), ctx.getClickedPos(), ctx.getClickedFace());
					}
					return InteractionResult.PASS;
				}, (ctx, state) -> {
					if (state.is(Tags.Blocks.ORES) && !ProjectEConfig.server.items.pickaxeAoeVeinMining.get()) {
						return ToolHelper.tryVeinMine(ctx.getPlayer(), ctx.getItemInHand(), ctx.getClickedPos(), ctx.getClickedFace());
					}
					return InteractionResult.PASS;
				}, (ctx, state) -> ToolHelper.digAOE(ctx.getLevel(), ctx.getPlayer(), ctx.getHand(), ctx.getItemInHand(), ctx.getClickedPos(), ctx.getClickedFace(),
						!(state.getBlock() instanceof GrassBlock) && !state.is(BlockTags.SAND) && !state.is(BlockTags.DIRT), 0));
	}

	@NotNull
	@Override
	public InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (ProjectEConfig.server.items.pickaxeAoeVeinMining.get()) {
			return ItemHelper.actionResultFromType(ToolHelper.mineOreVeinsInAOE(player, hand), stack);
		}
		return InteractionResult.PASS;
	}

	@Override
	public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
		return ToolHelper.canMatterMine(matterType, state.getBlock()) ? 1_200_000 : super.getDestroySpeed(stack, state) + 48.0F;
	}

	@Override
	public void adjustAttributes(ItemAttributeModifierEvent event) {
		ToolHelper.applyChargeAttributes(event);
	}

	@Override
	public DataComponentType<PickaxeMode> getDataComponentType() {
		return PEDataComponentTypes.PICKAXE_MODE.get();
	}

	@Override
	public PickaxeMode getDefaultMode() {
		return PickaxeMode.STANDARD;
	}
}
