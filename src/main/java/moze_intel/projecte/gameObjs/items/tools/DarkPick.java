package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class DarkPick extends PEToolBase {

	public DarkPick(Properties props) {
		this(props, (byte) 2, EnumMatterType.DARK_MATTER, new String[]{"pe.darkpick.mode1", "pe.darkpick.mode2", "pe.darkpick.mode3", "pe.darkpick.mode4"});
	}

	protected DarkPick(Properties props, byte numCharges, EnumMatterType matterType, String[] modeDesc) {
		super(props, numCharges, modeDesc);
		this.peToolMaterial = matterType;
		this.harvestMaterials.add(Material.IRON);
		this.harvestMaterials.add(Material.ANVIL);
		this.harvestMaterials.add(Material.ROCK);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return ActionResult.newResult(ActionResultType.SUCCESS, stack);
		}
		if (ProjectEConfig.items.pickaxeAoeVeinMining.get()) {
			ToolHelper.mineOreVeinsInAOE(stack, player, hand);
		} else {
			RayTraceResult mop = rayTrace(world, player, RayTraceContext.FluidMode.NONE);
			if (mop instanceof BlockRayTraceResult) {
				if (ItemHelper.isOre(world.getBlockState(((BlockRayTraceResult) mop).getPos()).getBlock())) {
					ToolHelper.tryVeinMine(stack, player, (BlockRayTraceResult) mop);
				}
			}
		}
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public boolean onBlockDestroyed(@Nonnull ItemStack stack, @Nonnull World world, BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity eLiving) {
		ToolHelper.digBasedOnMode(stack, world, state.getBlock(), pos, eLiving, Item::rayTrace);
		return true;
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		Block block = state.getBlock();
		if (block == ObjHandler.dmBlock || block == ObjHandler.dmFurnace) {
			return 1_200_000;
		}
		return super.getDestroySpeed(stack, state);
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		if (slot != EquipmentSlotType.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		}
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this instanceof RedPick ? 8 : 7, AttributeModifier.Operation.ADDITION));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.8, AttributeModifier.Operation.ADDITION));
		return multimap;
	}
}