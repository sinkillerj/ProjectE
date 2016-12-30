package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nonnull;

public class DarkShears extends PEToolBase
{
	public DarkShears()
	{
		super("dm_shears", (byte)2, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.toolClasses.add("shears");
		this.harvestMaterials.add(Material.WEB);
		this.harvestMaterials.add(Material.CLOTH);
		this.harvestMaterials.add(Material.PLANTS);
		this.harvestMaterials.add(Material.LEAVES);
		this.harvestMaterials.add(Material.VINE);
	}

	// Only for RedShears
	protected DarkShears(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase ent)
	{
		Block block = state.getBlock();
		if (state.getMaterial() != Material.LEAVES && block != Blocks.WEB && block != Blocks.TALLGRASS && block != Blocks.VINE && block != Blocks.TRIPWIRE && !(block instanceof IShearable))
		{
			return super.onBlockDestroyed(stack, world, state, pos, ent);
		}
		else
		{
			return true;
		}
	}
	
	@Override
	public boolean canHarvestBlock(@Nonnull IBlockState state, ItemStack stack)
	{
		return super.canHarvestBlock(state, stack) || state.getBlock() == Blocks.REDSTONE_WIRE || state.getBlock() == Blocks.TRIPWIRE;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		shearEntityAOE(stack, player, 0, hand);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		shearBlock(stack, pos, player);
		return false;
	}
}
