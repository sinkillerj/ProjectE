package moze_intel.projecte.gameObjs.items.tools;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SwingItemPKT;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class RedKatar extends ItemMode
{
	public RedKatar() 
	{
		super("rm_katar", (byte) 4, new String[] {"De-Foresting", "Hoe", "Shear", "Attack"});
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		if (this.getMode(stack) != 0)
		{
			return false;
		}
		
		return block.getMaterial() == Material.wood || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine;
	}
	
	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass) 
	{
		if (toolClass.equals("axe"))
		{
			return 4;
		}
		
		return -1;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if (this.getMode(stack) != 0)
		{
			return 1.0f;
		}
		
		if(canHarvestBlock(block, stack) || ForgeHooks.canToolHarvestBlock(block, metadata, stack))
		{
			return 16.0f + (14.0f * this.getCharge(stack));
		}
		
		return 1.0f;
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
		if (this.getMode(stack) != 1)
		{
			return false;
		}
		
		return tillSoil(world, stack, player, x, y, z, par7, this.getCharge(stack));
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
	
	private void deforest(World world, ItemStack stack, EntityPlayer player, byte charge)
	{
		if (charge == 0)
		{
			return;
		}
		
		List<ItemStack> drops = new ArrayList<ItemStack>();
		
		for (int x = (int) player.posX - (5 * charge); x <= player.posX + (5 * charge); x++)
			for (int y = (int) player.posY - (10 * charge); y <= player.posY + (10 * charge); y++)
				for (int z = (int) player.posZ - (5 * charge); z <= player.posZ + (5 * charge); z++)
				{
					Block block = world.getBlock(x, y, z);
					
					if (block == Blocks.air)
					{
						continue;
					}
					
					ItemStack s = new ItemStack(block);
					int[] oreIds = OreDictionary.getOreIDs(s);
					
					if (oreIds.length == 0)
					{
						continue;
					}
					
					String oreName = OreDictionary.getOreName(oreIds[0]);
					
					if (oreName.equals("logWood") || oreName.equals("treeLeaves"))
					{
						ArrayList<ItemStack> blockDrops = Utils.getBlockDrops(world, player, block, stack, x, y, z);
					
						if (!blockDrops.isEmpty())
						{
							drops.addAll(blockDrops);
						}
					
						world.setBlockToAir(x, y, z);
					}
				}
		
		if (!drops.isEmpty())
		{
			world.spawnEntityInWorld(new EntityLootBall(world, drops, player.posX, player.posY, player.posZ));
			PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
		}
	}
	
	private boolean tillSoil(World world, ItemStack stack, EntityPlayer player, int x, int y, int z, int param, byte charge)
	{
		if (this.getMode(stack) != 1 || !player.canPlayerEdit(x, y, z, param, stack))
		{
			return false;
		}
		else
		{
			UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);

			if (MinecraftForge.EVENT_BUS.post(event))
			{
				return false;
	        }

			if (event.getResult() == Result.ALLOW)
			{
				return true;
			}

			boolean hasAction = false;
			boolean hasSoundPlayed = false;
	            
			for (int i = x - charge; i <= x + charge; i++)
				for (int j = z - charge; j <= z + charge; j++)
				{
					Block block = world.getBlock(i, y, j);
					
					if (world.getBlock(i, y + 1, j).isAir(world, i, y + 1, j) && (block == Blocks.grass || block == Blocks.dirt))
					{
						Block block1 = Blocks.farmland;
	            			
						if (!hasSoundPlayed)
						{
							world.playSoundEffect((double)((float)i + 0.5F), (double)((float)y + 0.5F), (double)((float)j + 0.5F), block1.stepSound.getStepResourcePath(), (block1.stepSound.getVolume() + 1.0F) / 2.0F, block1.stepSound.getPitch() * 0.8F);
							hasSoundPlayed = true;
						}
	                        
						if (world.isRemote)
						{
							return true;
						}
						else
						{
							world.setBlock(i, y, j, block1);
							
							if (!hasAction)
							{
								hasAction = true;
							}
						}
					}
				}
			return hasAction;
		}
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
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
		return true;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("rm_tools", "katar"));
	}
}
