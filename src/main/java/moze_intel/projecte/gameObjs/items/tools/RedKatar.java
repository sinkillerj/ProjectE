package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.api.IExtraFunction;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SwingItemPKT;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedKatar extends PEToolBase implements IExtraFunction
{
	public RedKatar() 
	{
		super("rm_katar", (byte)4, new String[] {
				StatCollector.translateToLocal("pe.katar.mode1"), StatCollector.translateToLocal("pe.katar.mode2"),
		});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "katar";
		this.harvestMaterials.add(Material.wood);
		this.harvestMaterials.add(Material.web);
		this.harvestMaterials.add(Material.cloth);
		this.harvestMaterials.add(Material.plants);
		this.harvestMaterials.add(Material.leaves);
		this.harvestMaterials.add(Material.vine);

		this.secondaryClasses.add("sword");
		this.secondaryClasses.add("axe");
		this.secondaryClasses.add("shears");
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		// Sword
		attackWithCharge(stack, damaged, damager, 23.0F);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		// Shear
		shearBlock(stack, x, y, z, player);
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			return stack;
		}
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
		if (mop != null)
		{
			if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				Block blockHit = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
				if (blockHit instanceof BlockGrass || blockHit instanceof BlockDirt)
				{
					// Hoe
					tillAOE(stack, player, world, mop.blockX, mop.blockY, mop.blockZ, world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ));
				}
				else if (blockHit instanceof BlockLog)
				{
					// Axe
					deforestAOE(world, stack, player);
				}
			}
			else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.MISS)
			{
				player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
			}
		}
		else
		{
			// Shear
			shearEntityAOE(stack, player);
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		}
		
		return stack;
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player)
	{
		attackAOE(stack, player, getMode(stack) == 1, 1000.0F);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.block;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

}
