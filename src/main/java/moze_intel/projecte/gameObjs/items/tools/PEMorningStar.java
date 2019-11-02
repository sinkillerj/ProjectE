package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.blocks.IMatterBlock;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.GravelBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
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
	public boolean onBlockDestroyed(@Nonnull ItemStack stack, @Nonnull World world, BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity eLiving) {
		ToolHelper.digBasedOnMode(stack, world, state.getBlock(), pos, eLiving, Item::rayTrace);
		return true;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			if (ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
				ToolHelper.mineOreVeinsInAOE(stack, player, hand);
			}
			RayTraceResult mop = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
			if (!(mop instanceof BlockRayTraceResult)) {
				return ActionResult.newResult(ActionResultType.FAIL, stack);
			}
			BlockRayTraceResult rtr = (BlockRayTraceResult) mop;
			BlockState state = world.getBlockState(rtr.getPos());
			Block block = state.getBlock();

			if (block instanceof GravelBlock || block == Blocks.CLAY) {
				if (ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
					ToolHelper.digAOE(stack, world, player, false, 0, hand, Item::rayTrace);
				} else {
					ToolHelper.tryVeinMine(stack, player, rtr);
				}
			} else if (ItemHelper.isOre(state.getBlock())) {
				if (!ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
					ToolHelper.tryVeinMine(stack, player, rtr);
				}
			} else if (block instanceof GrassBlock || BlockTags.SAND.contains(block) || Tags.Blocks.DIRT.contains(block)) {
				ToolHelper.digAOE(stack, world, player, false, 0, hand, Item::rayTrace);
			} else {
				ToolHelper.digAOE(stack, world, player, true, 0, hand, Item::rayTrace);
			}
		}
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		Block block = state.getBlock();
		if (block instanceof IMatterBlock && ((IMatterBlock) block).getMatterType().getMatterTier() <= matterType.getMatterTier()) {
			return 1_200_000;
		}
		return super.getDestroySpeed(stack, state) + 48.0F;
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