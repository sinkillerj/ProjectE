package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class RedStar extends PEToolBase
{
	public RedStar() 
	{
		super("rm_morning_star", (byte) 4, new String[]{
				StatCollector.translateToLocal("pe.morningstar.mode1"), StatCollector.translateToLocal("pe.morningstar.mode2"),
				StatCollector.translateToLocal("pe.morningstar.mode3"), StatCollector.translateToLocal("pe.morningstar.mode4"),
		});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "morning_star";

		this.harvestMaterials.add(Material.grass);
		this.harvestMaterials.add(Material.ground);
		this.harvestMaterials.add(Material.sand);
		this.harvestMaterials.add(Material.snow);

		this.harvestMaterials.add(Material.iron);
		this.harvestMaterials.add(Material.anvil);
		this.harvestMaterials.add(Material.rock);

		this.harvestMaterials.add(Material.wood);
		this.harvestMaterials.add(Material.plants);
		this.harvestMaterials.add(Material.vine);

		this.secondaryClasses.add("pickaxe");
		this.secondaryClasses.add("chisel");
		this.secondaryClasses.add("shovel");
		this.secondaryClasses.add("axe");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		attackWithCharge(stack, damaged, damager, STAR_BASE_ATTACK);
		return true;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, BlockPos pos, EntityLivingBase eLiving)
	{
		digBasedOnMode(stack, world, block, pos, eLiving);
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (ProjectEConfig.pickaxeAoeVeinMining)
			{
				mineOreVeinsInAOE(stack, player);
			}

			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);

			if (mop == null)
			{
				return stack;
			}
			else if (mop.typeOfHit == MovingObjectType.BLOCK)
			{
				Block block = world.getBlockState(mop.getBlockPos()).getBlock();

				if (block instanceof BlockGravel)
				{
					if (ProjectEConfig.pickaxeAoeVeinMining)
					{
						digAOE(stack, world, player, false, 0);
					}
					else
					{
						tryVeinMine(stack, player, mop);
					}
				}
				else if (ItemHelper.isOre(block))
				{
					if (!ProjectEConfig.pickaxeAoeVeinMining)
					{
						tryVeinMine(stack, player, mop);
					}
				}
				else if (block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockSand)
				{
					digAOE(stack, world, player, false, 0);
				}
				else
				{
					digAOE(stack, world, player, true, 0);
				}
			}
		}
		
		return stack;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, IBlockState state)
	{
		Block block = state.getBlock();
		if (block == ObjHandler.matterBlock || block == ObjHandler.dmFurnaceOff || block == ObjHandler.dmFurnaceOn || block == ObjHandler.rmFurnaceOff || block == ObjHandler.rmFurnaceOn)
		{
			return 1200000.0F;
		}
		
		return super.getDigSpeed(stack, state) + 48.0F;
	}
}
