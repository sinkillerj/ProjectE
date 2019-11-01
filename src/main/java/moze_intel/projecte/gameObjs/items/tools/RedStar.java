package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.GravelBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class RedStar extends PEToolBase {

	public RedStar(Properties props) {
		super(props, (byte) 4, new String[]{
				"pe.morningstar.mode1", "pe.morningstar.mode2",
				"pe.morningstar.mode3", "pe.morningstar.mode4",
				});
		this.peToolMaterial = EnumMatterType.RED_MATTER;

		this.harvestMaterials.add(Material.ORGANIC);
		this.harvestMaterials.add(Material.EARTH);
		this.harvestMaterials.add(Material.SAND);
		this.harvestMaterials.add(Material.SNOW);
		this.harvestMaterials.add(Material.CLAY);

		this.harvestMaterials.add(Material.IRON);
		this.harvestMaterials.add(Material.ANVIL);
		this.harvestMaterials.add(Material.ROCK);

		this.harvestMaterials.add(Material.WOOD);
		this.harvestMaterials.add(Material.PLANTS);
		this.harvestMaterials.add(Material.TALL_PLANTS);
		this.harvestMaterials.add(Material.BAMBOO);
	}

	@Override
	public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockDestroyed(@Nonnull ItemStack stack, @Nonnull World world, BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity eLiving) {
		digBasedOnMode(stack, world, state.getBlock(), pos, eLiving);
		return true;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			if (ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
				mineOreVeinsInAOE(stack, player, hand);
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
					digAOE(stack, world, player, false, 0, hand);
				} else {
					tryVeinMine(stack, player, rtr);
				}
			} else if (ItemHelper.isOre(state.getBlock())) {
				if (!ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
					tryVeinMine(stack, player, rtr);
				}
			} else if (block instanceof GrassBlock || BlockTags.SAND.contains(block)
					   || BlockTags.getCollection().getOrCreate(new ResourceLocation("forge", "dirt")).contains(block)) {
				digAOE(stack, world, player, false, 0, hand);
			} else {
				digAOE(stack, world, player, true, 0, hand);
			}
		}
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		Block block = state.getBlock();
		if (block instanceof MatterBlock || block == ObjHandler.dmFurnace || block == ObjHandler.rmFurnace) {
			return 1_200_000;
		}
		return super.getDestroySpeed(stack, state) + 48.0F;
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		if (slot != EquipmentSlotType.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		}
		int charge = getCharge(stack);
		float damage = STAR_BASE_ATTACK + charge;
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3, AttributeModifier.Operation.ADDITION));
		return multimap;
	}
}