package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolType;

public class PEKatar extends PETool implements IItemMode, IExtraFunction {

	private final ILangEntry[] modeDesc;

	public PEKatar(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, 19, -2.4F, numCharges, props
				.addToolType(ToolType.AXE, matterType.getHarvestLevel())
				.addToolType(ToolHelper.TOOL_TYPE_SHEARS, matterType.getHarvestLevel())
				.addToolType(ToolType.HOE, matterType.getHarvestLevel())
				.addToolType(ToolHelper.TOOL_TYPE_KATAR, matterType.getHarvestLevel()));
		modeDesc = new ILangEntry[]{PELang.MODE_KATAR_1, PELang.MODE_KATAR_2};
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modeDesc;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags) {
		list.add(getToolTip(stack));
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
		PlayerEntity player = context.getPlayer();
		if (player == null) {
			return ActionResultType.PASS;
		}
		Hand hand = context.getHand();
		World world = context.getWorld();
		BlockState state = world.getBlockState(context.getPos());
		//Order that it attempts to use the item:
		// Strip logs, hoe ground, AOE remove logs, AOE remove leaves
		return ToolHelper.performActions(AxeItem.BLOCK_STRIPPING_MAP.get(state.getBlock()) == null ? ActionResultType.PASS : ToolHelper.stripLogsAOE(context, 0),
				() -> HoeItem.HOE_LOOKUP.get(state.getBlock()) == null ? ActionResultType.PASS : ToolHelper.tillHoeAOE(context, 0),
				() -> {
					if (state.isIn(BlockTags.LOGS)) {
						//Mass clear (acting as an axe)
						//Note: We already tried to strip the log in an earlier action
						return ToolHelper.clearTagAOE(world, player, hand, 0, BlockTags.LOGS);
					}
					return ActionResultType.PASS;
				}, () -> {
					if (state.isIn(BlockTags.LEAVES)) {
						//Mass clear (acting as shears)
						return ToolHelper.clearTagAOE(world, player, hand, 0, BlockTags.LEAVES);
					}
					return ActionResultType.PASS;
				});
	}

	@Override
	public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
		//Shear the block instead of breaking it if it supports shearing (and has drops to give) instead of actually breaking it normally
		return ToolHelper.shearBlock(stack, pos, player) == ActionResultType.SUCCESS;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		//Shear entities
		return ItemHelper.actionResultFromType(ToolHelper.shearEntityAOE(player, hand, 0), player.getHeldItem(hand));
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand) {
		if (player.getCooledAttackStrength(0F) == 1) {
			ToolHelper.attackAOE(stack, player, getMode(stack) == 1, ProjectEConfig.server.difficulty.katarDeathAura.get().floatValue(), 0, hand);
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

	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		return ToolHelper.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
	}

	/**
	 * Copy of {@link net.minecraft.item.ShearsItem#itemInteractionForEntity(ItemStack, PlayerEntity, LivingEntity, Hand)}
	 */
	@Nonnull
	@Override
	public ActionResultType itemInteractionForEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, LivingEntity entity, @Nonnull Hand hand) {
		if (entity instanceof IForgeShearable) {
			IForgeShearable target = (IForgeShearable) entity;
			BlockPos pos = entity.getPosition();
			if (target.isShearable(stack, entity.world, pos)) {
				if (!entity.world.isRemote) {
					List<ItemStack> drops = target.onSheared(player, stack, entity.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
					Random rand = new Random();
					drops.forEach(d -> {
						ItemEntity ent = entity.entityDropItem(d, 1.0F);
						ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F,
								(rand.nextFloat() - rand.nextFloat()) * 0.1F));
					});
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}
}