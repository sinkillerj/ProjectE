package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.GravelBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RedStar extends PEToolBase
{
	public RedStar(Properties props)
	{
		super(props, (byte) 4, new String[]{
				"pe.morningstar.mode1", "pe.morningstar.mode2",
				"pe.morningstar.mode3", "pe.morningstar.mode4",
		});
		this.peToolMaterial = EnumMatterType.RED_MATTER;

		this.harvestMaterials.add(Material.GRASS);
		this.harvestMaterials.add(Material.GROUND);
		this.harvestMaterials.add(Material.SAND);
		this.harvestMaterials.add(Material.SNOW);
		this.harvestMaterials.add(Material.CLAY);
		
		this.harvestMaterials.add(Material.IRON);
		this.harvestMaterials.add(Material.ANVIL);
		this.harvestMaterials.add(Material.ROCK);

		this.harvestMaterials.add(Material.WOOD);
		this.harvestMaterials.add(Material.PLANTS);
		this.harvestMaterials.add(Material.VINE);
	}

	@Override
	public boolean hitEntity(ItemStack stack, LivingEntity damaged, LivingEntity damager)
	{
		attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity eLiving)
	{
		digBasedOnMode(stack, world, state.getBlock(), pos, eLiving);
		return true;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote)
		{
			if (ProjectEConfig.items.pickaxeAoeVeinMining.get())
			{
				mineOreVeinsInAOE(stack, player, hand);
			}

			RayTraceResult mop = this.rayTrace(world, player, true);

			if (mop == null)
			{
				return ActionResult.newResult(ActionResultType.FAIL, stack);
			}
			else if (mop.type == Type.BLOCK)
			{
				BlockState state = world.getBlockState(mop.getBlockPos());
				Block block = state.getBlock();

				if (block instanceof GravelBlock || block instanceof BlockClay)
				{
					if (ProjectEConfig.items.pickaxeAoeVeinMining.get())
					{
						digAOE(stack, world, player, false, 0, hand);
					}
					else
					{
						tryVeinMine(stack, player, mop);
					}
				}
				else if (ItemHelper.isOre(state.getBlock()))
				{
					if (!ProjectEConfig.items.pickaxeAoeVeinMining.get())
					{
						tryVeinMine(stack, player, mop);
					}
				}
				else if (block instanceof GrassBlock
						|| BlockTags.SAND.contains(block)
						|| BlockTags.getCollection().getOrCreate(new ResourceLocation("forge", "dirt")).contains(block))
				{
					digAOE(stack, world, player, false, 0, hand);
				}
				else
				{
					digAOE(stack, world, player, true, 0, hand);
				}
			}
		}
		
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		Block block = state.getBlock();
		if (block instanceof MatterBlock || block == ObjHandler.dmFurnaceOff || block == ObjHandler.rmFurnaceOff)
		{
			return 1200000.0F;
		}
		
		return super.getDestroySpeed(stack, state) + 48.0F;
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
		float damage = STAR_BASE_ATTACK + charge;

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, 0));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3, 0));
		return multimap;
	}
}
