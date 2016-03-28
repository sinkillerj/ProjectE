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

public class DarkShears extends PEToolBase
{
	public DarkShears()
	{
		super("dm_shears", (byte)2, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.pePrimaryToolClass = "shears";
		this.harvestMaterials.add(Material.web);
		this.harvestMaterials.add(Material.cloth);
		this.harvestMaterials.add(Material.plants);
		this.harvestMaterials.add(Material.leaves);
		this.harvestMaterials.add(Material.vine);
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
		if (state.getMaterial() != Material.leaves && block != Blocks.web && block != Blocks.tallgrass && block != Blocks.vine && block != Blocks.tripwire && !(block instanceof IShearable))
		{
			return super.onBlockDestroyed(stack, world, state, pos, ent);
		}
		else
		{
			return true;
		}
	}
	
	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack)
	{
		return super.canHarvestBlock(state, stack) || state.getBlock() == Blocks.redstone_wire || state.getBlock() == Blocks.tripwire;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		shearEntityAOE(stack, player, 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		shearBlock(stack, pos, player);
		return false;
	}
}
