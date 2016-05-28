package moze_intel.projecte.gameObjs.container.slots.transmutation;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotLock extends SlotItemHandler
{
	private final TransmutationInventory inv;
	
	public SlotLock(TransmutationInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
		this.inv = inv;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return EMCHelper.doesItemHaveEmc(stack);
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		if (stack == null)
		{
			return;
		}
		
		super.putStack(stack);
		
		if (stack.getItem() instanceof IItemEmc)
		{
			IItemEmc itemEmc = ((IItemEmc) stack.getItem());
			int remainEmc = Constants.TILE_MAX_EMC - (int) Math.ceil(inv.provider.getEmc());
			
			if (itemEmc.getStoredEmc(stack) >= remainEmc)
			{
				inv.addEmc(remainEmc);
				itemEmc.extractEmc(stack, remainEmc);
			}
			else
			{
				inv.addEmc(itemEmc.getStoredEmc(stack));
				itemEmc.extractEmc(stack, itemEmc.getStoredEmc(stack));
			}
		}
		
		inv.handleKnowledge(stack.copy());
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
	{
		super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
		
		inv.updateClientTargets();
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
