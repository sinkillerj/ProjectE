package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;

public class PEMorningStar extends PETool implements IItemMode {

	private final String[] modeDesc;

	public PEMorningStar(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, 16, -3, numCharges, props
				.addToolType(ToolType.PICKAXE, matterType.getHarvestLevel())
				.addToolType(ToolType.SHOVEL, matterType.getHarvestLevel())
				.addToolType(ToolHelper.TOOL_TYPE_HAMMER, matterType.getHarvestLevel())
				.addToolType(ToolHelper.TOOL_TYPE_MORNING_STAR, matterType.getHarvestLevel()));
		modeDesc = new String[]{"pe.morningstar.mode1", "pe.morningstar.mode2", "pe.morningstar.mode3", "pe.morningstar.mode4"};
	}

	@Override
	public String[] getModeTranslationKeys() {
		return modeDesc;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags) {
		list.add(getToolTip(stack));
	}

	/**
	 * Simple copy of {@link net.minecraft.item.PickaxeItem#canHarvestBlock(BlockState)}'s and {@link net.minecraft.item.ShovelItem#canHarvestBlock(BlockState)}
	 * fallback/shortcut to allow the morning star to also mine all blocks of that type.
	 *
	 * This does not need any special overrides for {@link PEHammer}'s check as that check is the same as the one for {@link net.minecraft.item.PickaxeItem}.
	 *
	 * @implNote This method is overridden instead of {@link net.minecraftforge.common.extensions.IForgeItem#canHarvestBlock(ItemStack, BlockState)} so that it is used as
	 * a fallback if {@link PETool#canHarvestBlock(ItemStack, BlockState)} does not find a matching tool/required level for the tool. As the default implementation that
	 * gets used is one where the stack does not matter (which would be this)
	 */
	@Override
	public boolean canHarvestBlock(BlockState state) {
		//Note: These checks cover the need of overriding/shortcutting the destroy speed
		//Shovel
		Block block = state.getBlock();
		if (block == Blocks.SNOW || block == Blocks.SNOW_BLOCK) {
			return true;
		}
		//Pickaxe
		Material material = state.getMaterial();
		return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
	}

	@Override
	public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockDestroyed(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity living) {
		ToolHelper.digBasedOnMode(stack, world, pos, living, Item::rayTrace);
		return true;
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
		BlockPos pos = context.getPos();
		Direction sideHit = context.getFace();
		BlockState state = world.getBlockState(pos);
		//Order that it attempts to use the item:
		// Till (Shovel), Vein (or AOE) mine gravel/clay, vein mine ore, AOE dig (if it is sand, dirt, or grass don't do depth)
		return ToolHelper.performActions(ToolHelper.tillShovelAOE(context, 0),
				() -> {
					if (state.isIn(Tags.Blocks.GRAVEL) || state.getBlock() == Blocks.CLAY) {
						if (ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
							return ToolHelper.digAOE(world, player, hand, pos, sideHit, false, 0);
						}
						return ToolHelper.tryVeinMine(hand, player, pos, sideHit);
					}
					return ActionResultType.PASS;
				}, () -> {
					if (ItemHelper.isOre(state) && !ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
						return ToolHelper.tryVeinMine(hand, player, pos, sideHit);
					}
					return ActionResultType.PASS;
				}, () -> ToolHelper.digAOE(world, player, hand, pos, sideHit,
						!(state.getBlock() instanceof GrassBlock) && !state.isIn(BlockTags.SAND) && !state.isIn(Tags.Blocks.DIRT), 0));
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
			return ActionResult.newResult(ToolHelper.mineOreVeinsInAOE(player, hand), stack);
		}
		return ActionResult.newResult(ActionResultType.PASS, stack);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		if (ToolHelper.canMatterMine(matterType, state.getBlock())) {
			return 1_200_000;
		}
		return super.getDestroySpeed(stack, state) + 48.0F;
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		return ToolHelper.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
	}
}