package moze_intel.projecte.gameObjs.items.tools;

import cpw.mods.fml.common.eventhandler.Event.Result;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SwingItemPKT;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedKatar extends PEToolBase
{
	public RedKatar() 
	{
		super("rm_katar", (byte)4, new String[] {
				StatCollector.translateToLocal("pe.katar.mode1"), StatCollector.translateToLocal("pe.katar.mode2"),
				StatCollector.translateToLocal("pe.katar.mode3"), StatCollector.translateToLocal("pe.katar.mode4")});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "katar";
		this.harvestMaterials.add(Material.wood);
		this.harvestMaterials.add(Material.plants);
		this.harvestMaterials.add(Material.vine);
		this.harvestMaterials.add(Material.web);

		this.secondaryClasses.add("sword");
		this.secondaryClasses.add("axe");
		this.secondaryClasses.add("shears");
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		switch (this.getMode(stack))
		{
			case 0:
				return block.getMaterial() == Material.wood || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine;
			case 2:
				return block.getMaterial() == Material.web;
			default:
				return false;
		}
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		if (!(damager instanceof EntityPlayer) || this.getMode(stack) != 3)
		{
			return false;
		}
		
		DamageSource dmg = DamageSource.causePlayerDamage((EntityPlayer) damager);
		byte charge = this.getCharge(stack);
		float totalDmg = 15.0f;
		
		if (charge > 0)
		{
			dmg.setDamageBypassesArmor();
			totalDmg += charge;
		}
		
		damaged.attackEntityFrom(dmg, totalDmg);
		return true;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		if (this.getMode(stack) == 1)
		{
			return tillSoil(stack, player, world, x, y, z, par7);
		}
		
		return false;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player)
	{
		if (player.worldObj.isRemote)
		{
			return false;
		}

		Block block = player.worldObj.getBlock(x, y, z);

		if (block instanceof IShearable)
		{
			IShearable target = (IShearable) block;

			if (target.isShearable(itemstack, player.worldObj, x, y, z))
			{
				ArrayList<ItemStack> drops = target.onSheared(itemstack, player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
				Random rand = new Random();

				for(ItemStack stack : drops)
				{
					float f = 0.7F;
					double d = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
					entityitem.delayBeforeCanPickup = 10;
					player.worldObj.spawnEntityInWorld(entityitem);
				}

				itemstack.damageItem(1, player);
				player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
			}
		}

		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			return stack;
		}
		
		byte mode = this.getMode(stack);
		byte charge = this.getCharge(stack);
		
		if (mode == 0)
		{
			deforest(world, stack, player, charge);
		}
		else if (mode == 2)
		{
			shear(world, stack, player, charge);
		}
		else if (mode == 3)
		{
			attackNearby(world, stack, player, charge);
		}
		
		return stack;
	}
	
	private void shear(World world, ItemStack stack, EntityPlayer player, byte charge)
	{
		int offset = 0;
		
		switch (charge)
		{
			case 0:
				offset = 4;
				break;
			case 1:
				offset = 8;
				break;
			case 2:
				offset = 16;
				break;
			case 3:
				offset = 32;
				break;
			case 4:
				offset = 40;
				break;
		}
		
		AxisAlignedBB bBox = player.boundingBox.expand(offset, offset / 2, offset);
		List<Entity> list = world.getEntitiesWithinAABB(IShearable.class, bBox);
		
		if (list.isEmpty())
		{
			return;
		}
		
		List<ItemStack> drops = new ArrayList<ItemStack>();
		
		for (Entity ent : list)
		{
			IShearable target = (IShearable) ent;
			
			if (target.isShearable(stack, ent.worldObj, (int) ent.posX, (int) ent.posY, (int) ent.posZ))
			{
				ArrayList<ItemStack> entDrops = target.onSheared(stack, ent.worldObj, (int) ent.posX, (int) ent.posY, (int) ent.posZ, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
				
				if (entDrops.isEmpty())
				{
					continue;
				}
				
				for (ItemStack drop : entDrops)
				{
					drop.stackSize += Utils.randomIntInRange(6, 3);
				}
				
				drops.addAll(entDrops);
			}
		}
		
		if (!drops.isEmpty())
		{
			world.spawnEntityInWorld(new EntityLootBall(world, drops, player.posX, player.posY, player.posZ));
			PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
		}
	}
	
	private void attackNearby(World world, ItemStack stack, EntityPlayer player, byte charge)
	{
		int offset = 0;
		
		switch (charge)
		{
			case 0:
				offset = 4;
				break;
			case 1:
				offset = 5;
				break;
			case 2:
				offset = 6;
				break;
			case 3:
				offset = 7;
				break;
			case 4:
				offset = 8;
				break;
		}
		
		AxisAlignedBB bBox = player.boundingBox.expand(offset, offset / 2, offset);
		List<EntityLiving> list = world.getEntitiesWithinAABB(EntityLiving.class, bBox);
		
		if (list.isEmpty())
		{
			return;
		}
		
		List<ItemStack> drops = new ArrayList<ItemStack>();
		
		for (EntityLiving ent : list)
		{
			if (ent.getHealth() <= 0)
			{
				continue;
			}
			
			DamageSource dmg = DamageSource.causePlayerDamage(player);
			float totalDmg = 15.0f;
			
			if (charge > 0)
			{
				dmg.setDamageBypassesArmor();
				totalDmg += charge;
			}
			
			ent.attackEntityFrom(dmg, totalDmg);
			List<EntityItem> entDrops = ent.capturedDrops;
			
			if (entDrops.isEmpty())
			{
				continue;
			}
			
			for (EntityItem item : entDrops)
			{
				drops.add(item.getEntityItem());
				item.setDead();
			}
		}
		
		PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
		
		if (!drops.isEmpty())
		{
			world.spawnEntityInWorld(new EntityLootBall(world, drops, player.posX, player.posY, player.posZ));
		}
	}
}
