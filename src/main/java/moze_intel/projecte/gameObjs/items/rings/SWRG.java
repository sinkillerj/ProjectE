package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class SWRG extends ItemPE implements IBauble
{
	@SideOnly(Side.CLIENT)
	private IIcon ringOff;
	@SideOnly(Side.CLIENT)
	private IIcon[] ringOn;

	public SWRG()
	{
		this.setUnlocalizedName("swrg");
		this.setMaxStackSize(1);
		this.setMaxDamage(3);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeldItem) 
	{
		if (world.isRemote || invSlot > 8 || !(entity instanceof EntityPlayer))
		{
			return;
		}

		if (!stack.hasTagCompound())
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		EntityPlayerMP playerMP = (EntityPlayerMP) entity;
		
		if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false))
		{
			if (stack.getItemDamage() > 0)
			{
				changeMode(player, stack, 0);
			}
			
			if (playerMP.capabilities.allowFlying)
			{
				disableFlight(playerMP);
			}
			
			return;
		}
		
		if (!playerMP.capabilities.allowFlying)
		{
			enableFlight(playerMP);
		}

		if (playerMP.capabilities.isFlying)
		{
			if (!isFlyingEnabled(stack))
			{
				changeMode(player, stack, stack.getItemDamage() == 0 ? 1 : 3);
			}
		}
		else
		{
			if (isFlyingEnabled(stack))
			{
				changeMode(player, stack, stack.getItemDamage() == 1 ? 0 : 2);
			}
		}
		
		if (stack.getItemDamage() > 1)
		{
			Utils.repellEntities(player);
		}
		
		float toRemove = 0;
		
		if (playerMP.capabilities.isFlying)
		{
			toRemove = 0.32F;
		}
		
		if (stack.getItemDamage() == 2)
		{
			toRemove = 0.32F;
		}
		else if (stack.getItemDamage() == 3)
		{
			toRemove = 0.64F;
		}
		
		removeEmc(stack, toRemove);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			int newMode = 0;
			
			switch (stack.getItemDamage())
			{
				case 0:
					newMode = 2;
					break;
				case 1:
					newMode = 3;
					break;
				case 2:
					newMode = 0;
					break;
				case 3:
					newMode = 1;
					break;
			}
			
			if (newMode > 1)
			{
				if (getEmc(stack) > 0)
				{
					changeMode(player, stack, newMode);
				}
				else 
				{
					if (consumeFuel(player, stack, 64, false))
					{
						changeMode(player, stack, newMode);
					}
				}
			}
			else 
			{
				changeMode(player, stack, newMode);
			}
		}
		return stack;
	}
	
	public void ToggleFlight(EntityPlayer player, ItemStack ring)
	{
		if (getEmc(ring) == 0 && !consumeFuel(player, ring, 64, false))
		{
			return;
		}
		
		switch (ring.getItemDamage())
		{
			case 0:
				changeMode(player, ring, 1);
				break;
			case 1:
				changeMode(player, ring, 0);
				break;
			case 2:
				changeMode(player, ring, 3);
				break;
			case 3:
				changeMode(player, ring, 2);
				break;
		}
	}
	
	public void enableFlight(EntityPlayerMP playerMP)
	{
		if (playerMP.capabilities.isCreativeMode)
		{
			return;
		}
		
		if (!playerMP.capabilities.allowFlying)
		{
			Utils.setPlayerFlight(playerMP, true);
			PlayerChecks.addPlayerFlyChecks(playerMP);
		}
	}
	
	public void disableFlight(EntityPlayerMP playerMP)
	{
		if (playerMP.capabilities.isCreativeMode)
		{
			return;
		}
		
		if (playerMP.capabilities.allowFlying)
		{
			Utils.setPlayerFlight(playerMP, false);
			PlayerChecks.removePlayerFlyChecks(playerMP);
		}
	}
	
	public void enableFlightNoChecks(EntityPlayerMP playerMP)
	{
		if (playerMP.capabilities.isCreativeMode)
		{
			return;
		}
		
		if (!playerMP.capabilities.allowFlying)
		{
			Utils.setPlayerFlight(playerMP, true);
		}
	}
	
	public void disableFlightNoChecks(EntityPlayerMP playerMP)
	{
		if (playerMP.capabilities.isCreativeMode)
		{
			return;
		}
		
		if (playerMP.capabilities.allowFlying)
		{
			Utils.setPlayerFlight(playerMP, false);
		}
	}
	
	/**
	 * Change the mode of SWRG. Modes:<p>
	 * 0 = Ring Off<p>  
	 * 1 = Flight<p>
	 * 2 = Shield<p>
	 * 3 = Flight + Shield<p>
	 */
	public void changeMode(EntityPlayer player, ItemStack stack, int mode)
	{
		stack.setItemDamage(mode);
	}
	
	public boolean isFlyingEnabled(ItemStack stack)
	{
		return stack.getItemDamage() == 1 || stack.getItemDamage() == 3;
	}
	
	public float getEmcToRemove(ItemStack stack)
	{
		int damage = stack.getItemDamage();
		
		if (damage == 0)
		{
			return 0;
		}
		else if (damage < 3)
		{
			return 0.32F;
		}
		else
		{
			return 0.64F;
		}
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg)
	{
		if (dmg == 0)
		{
			return ringOff;
		}
		
		else
		{
			return ringOn[MathHelper.clamp_int(dmg - 1, 0, 2)];
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		ringOff = register.registerIcon(this.getTexture("rings", "swrg_off"));
		ringOn = new IIcon[3];
		
		for (int i = 0; i < 3; i++)
		{
			ringOn[i] = register.registerIcon(this.getTexture("rings", "swrg_on"+(i+1)));
		}
	}
	
	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.RING;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase ent) 
	{
		if (ent.worldObj.isRemote || !(ent instanceof EntityPlayer))
		{
			return;
		}
		
		if (!stack.hasTagCompound())
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
		
		EntityPlayer player = (EntityPlayer) ent;
		EntityPlayerMP playerMP = (EntityPlayerMP) player;
		
		if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false))
		{
			if (stack.getItemDamage() > 0)
			{
				changeMode(player, stack, 0);
			}
			
			if (playerMP.capabilities.allowFlying)
			{
				disableFlightNoChecks(playerMP);
			}
			
			return;
		}
		
		if (!playerMP.capabilities.allowFlying)
		{
			enableFlightNoChecks(playerMP);
		}
			
		if (playerMP.capabilities.isFlying)
		{
			if (!isFlyingEnabled(stack))
			{
				changeMode(player, stack, stack.getItemDamage() == 0 ? 1 : 3);
			}
		}
		else
		{
			if (isFlyingEnabled(stack))
			{
				changeMode(player, stack, stack.getItemDamage() == 1 ? 0 : 2);
			}
		}
		
		if (stack.getItemDamage() > 1)
		{
			Utils.repellEntities(player);
		}
		
		float toRemove = 0;
		
		if (playerMP.capabilities.isFlying)
		{
			toRemove = 0.32F;
		}
		
		if (stack.getItemDamage() == 2)
		{
			toRemove = 0.32F;
		}
		else if (stack.getItemDamage() == 3)
		{
			toRemove = 0.64F;
		}
		
		removeEmc(stack, toRemove);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack stack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) 
	{
		if (player.worldObj.isRemote || !(player instanceof EntityPlayer))
		{
			return;
		}

		disableFlightNoChecks((EntityPlayerMP) player);
	}

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
