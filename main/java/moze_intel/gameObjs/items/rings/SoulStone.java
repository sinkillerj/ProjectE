package moze_intel.gameObjs.items.rings;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SoulStone extends RingToggle
{
	public SoulStone() 
	{
		super("soul_stone");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5)
	{
		if (world.isRemote || par4 > 8 || !(entity instanceof EntityPlayer)) return;
		
		super.onUpdate(stack, world, entity, par4, par5);
		
		EntityPlayer player = (EntityPlayer) entity;
		
		if (stack.getItemDamage() != 0)
		{
			if (this.getEmc(stack) < 128 && !this.consumeFuel(player, stack, 128, false))
			{
				stack.setItemDamage(0);
			}
			else if (player.getHealth() < player.getMaxHealth())
			{
				if (isReady(stack))
				{
					player.setHealth(player.getHealth() + 2);
					this.removeEmc(stack, 128);
					this.increaseTickCounter(stack);
				}
				else
				{
					this.increaseTickCounter(stack);
				}
			}
		}
		else if (this.getTickCount(stack) != 0)
		{
			this.setTickCounter(stack, (byte) 0);
		}
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			if (this.getEmc(stack) < 128 && !this.consumeFuel(player, stack, 128, false))
			{
				//NOOP (used to be sounds)
			}
			else
			{
				stack.setItemDamage(1);
			}
		}
		else
		{
			stack.setItemDamage(0);
		}
	}
}
