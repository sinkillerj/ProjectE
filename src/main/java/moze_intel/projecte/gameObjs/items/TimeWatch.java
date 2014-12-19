package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import moze_intel.projecte.api.IModeChanger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class TimeWatch extends ItemCharge implements IModeChanger, IBauble
{
	@SideOnly(Side.CLIENT)
	private IIcon ringOff;
	@SideOnly(Side.CLIENT)
	private IIcon ringOn;
	
	public TimeWatch() 
	{
		super("time_watch", (byte) 3);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (!stack.hasTagCompound())
			{
				stack.stackTagCompound = new NBTTagCompound();
			}

			byte current = getTimeBoost(stack);

			setTimeBoost(stack, (byte) (current == 2 ? 0 : current + 1));

			player.addChatComponentMessage(new ChatComponentText("Change time control mode to: " + getTimeDescription(stack)));
		}

		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeld) 
	{
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		if (world.isRemote || !(entity instanceof EntityPlayer) || invSlot > 8)
		{
			return;
		}

		byte timeControl = getTimeBoost(stack);

		if (timeControl == 1)
		{
			if (world.getWorldTime() + ((getCharge(stack) + 1) * 4) > Long.MAX_VALUE)
			{
				world.setWorldTime(Long.MAX_VALUE);
			}
			else
			{
				world.setWorldTime((world.getWorldTime() + ((getCharge(stack) + 1) * 4)));
			}
		}
		else if (timeControl == 2)
		{
			if (world.getWorldTime() - ((getCharge(stack) + 1) * 4) < 0)
			{
				world.setWorldTime(0);
			}
			else
			{
				world.setWorldTime((world.getWorldTime() - ((getCharge(stack) + 1) * 4)));
			}
		}

		if (stack.getItemDamage() == 0)
		{
			return;
		}

		EntityPlayer player = (EntityPlayer) entity;
		double reqEmc = getEmcPerTick(this.getCharge(stack));
		
		if (!consumeFuel(player, stack, reqEmc, true))
		{
			return;
		}
		
		int charge = this.getCharge(stack);
		int bonusTicks = 0;
		float mobSlowdown = 0;
		
		if (charge == 0)
		{
			bonusTicks = 8;
			mobSlowdown = 0.25F;
		}
		else if (charge == 1)
		{
			bonusTicks = 12;
			mobSlowdown = 0.16F;
		}
		else
		{
			bonusTicks = 16;
			mobSlowdown = 0.12F;
		}
			
		AxisAlignedBB bBox = player.boundingBox.expand(8, 8, 8);
		
		for (TileEntity tile : Utils.getTileEntitiesWithinAABB(world, bBox))
		{
			for (int i = 0; i < bonusTicks; i++)
			{
				tile.updateEntity();
			}
		}
		
		for (int x = (int) bBox.minX; x <= bBox.maxX; x++)
			for (int y = (int) bBox.minY; y <= bBox.maxY; y++)
				for (int z = (int) bBox.minZ; z <= bBox.maxZ; z++)
				{
					Block block = world.getBlock(x, y, z);
					
					if (block.getTickRandomly())
					{
						for (int i = 0; i < bonusTicks; i++)
						{
							block.updateTick(world, x, y, z, itemRand);
						}
					}
				}
		
		for (Object obj : world.getEntitiesWithinAABB(EntityLiving.class, bBox))
		{
			Entity ent = (Entity) obj;
			
			if (ent.motionX != 0)
			{
				ent.motionX *= mobSlowdown;
			}
			
			if (ent.motionZ != 0)
			{
				ent.motionZ *= mobSlowdown;
			}
		}
	}

	private String getTimeDescription(ItemStack stack)
	{
		byte mode = getTimeBoost(stack);

		switch (mode)
		{
			case 0:
				return "Off";
			case 1:
				return "Fast-Forward";
			case 2:
				return "Rewind";
			default:
				return "ERROR_INVALID_MODE";
		}
	}

	private byte getTimeBoost(ItemStack stack)
	{
		return stack.stackTagCompound.getByte("TimeMode");
	}

	private void setTimeBoost(ItemStack stack, byte time)
	{
		stack.stackTagCompound.setByte("TimeMode", (byte) MathHelper.clamp_int(time, 0, 2));
	}

	public double getEmcPerTick(int charge)
	{
		int actualCharge = charge + 1;
		return (10.0D * actualCharge) / 20.0D;
	}

	@Override
	public byte getMode(ItemStack stack)
	{
		return (byte) stack.getItemDamage();
	}

	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			stack.setItemDamage(1);
			playChargeSound(player);
		}
		else 
		{
			stack.setItemDamage(0);
			playUnChargeSound(player);
		}
	}
	
	public void playChargeSound(EntityPlayer player)
	{
		player.worldObj.playSoundAtEntity(player, "projecte:clock", 0.8F, 1.25F);
	}
	
	public void playUnChargeSound(EntityPlayer player)
	{
		player.worldObj.playSoundAtEntity(player, "projecte:clock", 0.8F, 0.85F);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg)
	{
		if (dmg == 0)
		{
			return ringOff;
		}
		
		return ringOn;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		ringOff = register.registerIcon(this.getTexture("rings", "time_watch_off"));
		ringOn = register.registerIcon(this.getTexture("rings", "time_watch_on"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		list.add("Become the master of time.");
		list.add("Right click to change time control mode.");

		if (stack.hasTagCompound())
		{
			list.add("Time control mode: " + getTimeDescription(stack));
		}
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.BELT;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) 
	{
		this.onUpdate(stack, player.worldObj, player, 0, false);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}
}
