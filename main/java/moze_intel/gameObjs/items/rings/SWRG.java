package moze_intel.gameObjs.items.rings;

import moze_intel.events.PlayerChecksEvent;
import moze_intel.gameObjs.items.ItemBase;
import moze_intel.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SWRG extends ItemBase
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
		
		if (this.getEmc(stack) == 0 && !this.consumeFuel(player, stack, 64, false))
		{
			if (stack.getItemDamage() > 0)
			{
				ChangeMode(player, stack, 0);
			}
			
			if (playerMP.capabilities.allowFlying)
			{
				DisableFlight(playerMP);
			}
			
			return;
		}
		
		if (!playerMP.capabilities.allowFlying)
		{
			EnableFlight(playerMP);
		}
			
		if (playerMP.capabilities.isFlying)
		{
			if (!isFlyingEnabled(stack))
			{
				ChangeMode(player, stack, stack.getItemDamage() == 0 ? 1 : 3);
			}
		}
		else
		{
			if (isFlyingEnabled(stack))
			{
				ChangeMode(player, stack, stack.getItemDamage() == 1 ? 0 : 2);
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
		
		this.removeEmc(stack, toRemove);
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
				if (this.getEmc(stack) > 0)
					ChangeMode(player, stack, newMode);
				else 
				{
					if (this.consumeFuel(player, stack, 64, false))
						ChangeMode(player, stack, newMode);
					else PlayUnChargeSound(player);
				}
			}
			else ChangeMode(player, stack, newMode);
		}
		return stack;
	}
	
	public void ToggleFlight(EntityPlayer player, ItemStack ring)
	{
		if (this.getEmc(ring) == 0 && !this.consumeFuel(player, ring, 64, false))
		{
			PlayUnChargeSound(player);
			return;
		}
		
		switch (ring.getItemDamage())
		{
			case 0:
				ChangeMode(player, ring, 1);
				break;
			case 1:
				ChangeMode(player, ring, 0);
				break;
			case 2:
				ChangeMode(player, ring, 3);
				break;
			case 3:
				ChangeMode(player, ring, 2);
				break;
		}
	}
	
	public void EnableFlight(EntityPlayerMP playerMP)
	{
		if (playerMP.capabilities.isCreativeMode)
		{
			return;
		}
		
		if (!playerMP.capabilities.allowFlying)
		{
			Utils.setPlayerFlight(playerMP, true);
			PlayerChecksEvent.addPlayerFlyChecks(playerMP);
		}
	}
	
	public void DisableFlight(EntityPlayerMP playerMP)
	{
		if (playerMP.capabilities.isCreativeMode)
		{
			return;
		}
		
		if (playerMP.capabilities.allowFlying)
		{
			Utils.setPlayerFlight(playerMP, false);
			PlayerChecksEvent.removePlayerFlyChecks(playerMP);
		}
	}
	
	/**
	 * Change the mode of SWRG. Modes:<p>
	 * 0 = Ring Off<p>  
	 * 1 = Flight<p>
	 * 2 = Shield<p>
	 * 3 = Flight + Shield<p>
	 */
	public void ChangeMode(EntityPlayer player, ItemStack stack, int mode)
	{
		stack.setItemDamage(mode);
		
		if (mode == 0) 
		{
			PlayUnChargeSound(player);
		}
		else if (mode >= 1) 
		{
			PlayChargeSound(player);
		}
	}
	
	public boolean isFlyingEnabled(ItemStack stack)
	{
		return stack.getItemDamage() == 1 || stack.getItemDamage() == 3;
	}
	
	public float getEmcToRemove(ItemStack stack)
	{
		int damage = stack.getItemDamage();
		if (damage == 0)
			return 0;
		else if (damage < 3)
			return 0.32F;
		else return 0.64F;
	}
	
	public void PlayChargeSound(EntityPlayer player)
	{
		player.worldObj.playSoundAtEntity(player, "projecte:heal", 0.8F, 1.0F / (Item.itemRand.nextFloat() * 0.4F + 0.8F));
	}
	
	public void PlayUnChargeSound(EntityPlayer player)
	{
		player.worldObj.playSoundAtEntity(player, "projecte:break", 0.8F, 1.0F / (Item.itemRand.nextFloat() * 0.4F + 0.8F));
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
			return ringOff;
		else return ringOn[MathHelper.clamp_int(dmg - 1, 0, 2)];
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
}
