package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.UseAction;
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

import javax.annotation.Nonnull;

public class RedKatar extends PEToolBase implements IExtraFunction
{
	public RedKatar(Properties props)
	{
		super(props, (byte)4, new String[] {
				"pe.katar.mode1", "pe.katar.mode2",
		});
		this.peToolMaterial = EnumMatterType.RED_MATTER;
		this.harvestMaterials.add(Material.WOOD);
		this.harvestMaterials.add(Material.WEB);
		this.harvestMaterials.add(Material.WOOL);
		this.harvestMaterials.add(Material.PLANTS);
		this.harvestMaterials.add(Material.LEAVES);
		this.harvestMaterials.add(Material.TALL_PLANTS);
	}

	@Override
	public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager)
	{
		attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player)
	{
		// Shear
		shearBlock(stack, pos, player);
		return false;
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote)
		{
			return ActionResult.newResult(ActionResultType.SUCCESS, stack);
		}
		RayTraceResult mop = rayTrace(world, player, RayTraceContext.FluidMode.NONE);
		if (mop instanceof BlockRayTraceResult)
		{
			BlockRayTraceResult rtr = (BlockRayTraceResult) mop;
			BlockState state = world.getBlockState(rtr.getPos());
			Block blockHit = state.getBlock();
			if (blockHit instanceof GrassBlock || blockHit == Blocks.DIRT)
			{
				// Hoe
				tillAOE(hand, player, world, rtr.getPos(), rtr.getFace(), 0);
			}
			else if (BlockTags.LOGS.contains(blockHit))
			{
				// Axe
				clearTagAOE(world, stack, player, BlockTags.LOGS, 0, hand);
			}
			else if (BlockTags.LEAVES.contains(blockHit)) {
				// Shear leaves
				clearTagAOE(world, stack, player, BlockTags.LEAVES, 0, hand);
			}
		}
		else
		{
			// Shear
			shearEntityAOE(stack, player, 0, hand);
		}
		
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand)
	{
		if (player.getCooledAttackStrength(0F) == 1)
		{
			attackAOE(stack, player, getMode(stack) == 1, ProjectEConfig.difficulty.katarDeathAura.get().floatValue(), 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Nonnull
	@Override
	public UseAction getUseAction(ItemStack par1ItemStack)
	{
		return UseAction.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack)
	{
		if (slot != EquipmentSlotType.MAINHAND)
		{
			return super.getAttributeModifiers(slot, stack);
		}

		int charge = getCharge(stack);
		float damage = KATAR_BASE_ATTACK + charge; // Sword

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.4, AttributeModifier.Operation.ADDITION));
		return multimap;
	}

}
