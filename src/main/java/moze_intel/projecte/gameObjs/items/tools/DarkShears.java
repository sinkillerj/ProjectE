package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nonnull;

public class DarkShears extends PEToolBase
{
	public DarkShears(Properties props)
	{
		super(props, (byte)2, new String[]{});
		this.peToolMaterial = EnumMatterType.DARK_MATTER;
		this.harvestMaterials.add(Material.WEB);
		this.harvestMaterials.add(Material.WOOL);
		this.harvestMaterials.add(Material.PLANTS);
		this.harvestMaterials.add(Material.LEAVES);
		this.harvestMaterials.add(Material.TALL_PLANTS);
	}

	// Only for RedShears
	protected DarkShears(Properties props, byte numCharges, String[] modeDesc)
	{
		super(props, numCharges, modeDesc);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity ent)
	{
		Block block = state.getBlock();
		if (state.getMaterial() != Material.LEAVES && block != Blocks.COBWEB && block != Blocks.TALL_GRASS && block != Blocks.VINE && block != Blocks.TRIPWIRE && !(block instanceof IShearable))
		{
			return super.onBlockDestroyed(stack, world, state, pos, ent);
		}
		else
		{
			return true;
		}
	}
	
	@Override
	public boolean canHarvestBlock(ItemStack stack, @Nonnull BlockState state)
	{
		return super.canHarvestBlock(stack, state) || state.getBlock() == Blocks.REDSTONE_WIRE || state.getBlock() == Blocks.TRIPWIRE;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		shearEntityAOE(stack, player, 0, hand);
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player)
	{
		shearBlock(stack, pos, player);
		return false;
	}
}
