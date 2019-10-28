package moze_intel.projecte.gameObjs.container.slots.transmutation;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage.EmcAction;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.SlotItemHandler;

public class SlotInput extends SlotItemHandler
{
	private final TransmutationInventory inv;
	
	public SlotInput(TransmutationInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
		this.inv = inv;
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return SlotPredicates.RELAY_INV.test(stack);
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int amount)
	{
		ItemStack stack = super.decrStackSize(amount);
		//Decrease the size of the stack
		if (stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent()) {
			//If it was an EMC storing item then check for updates,
			// so that the right hand side shows the proper items
			inv.checkForUpdates();
		}
		return stack;
	}
	
	@Override
	public void putStack(@Nonnull ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return;
		}
		
		super.putStack(stack);

		LazyOptional<IItemEmcHolder> holderCapability = stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY);
		if (holderCapability.isPresent()) {
			IItemEmcHolder emcHolder = holderCapability.orElse(null);
			long remainingEmc = emcHolder.getNeededEmc(stack);
			long availableEMC = inv.getAvailableEMC();

			if (availableEMC >= remainingEmc)
			{
				emcHolder.insertEmc(stack, remainingEmc, EmcAction.EXECUTE);
				inv.removeEmc(remainingEmc);
			}
			else
			{
				emcHolder.insertEmc(stack, availableEMC, EmcAction.EXECUTE);
				inv.removeEmc(availableEMC);
			}
		}

		if (EMCHelper.doesItemHaveEmc(stack)) {
			inv.handleKnowledge(stack.copy());
		}
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
