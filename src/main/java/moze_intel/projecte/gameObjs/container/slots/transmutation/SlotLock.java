package moze_intel.projecte.gameObjs.container.slots.transmutation;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage.EmcAction;
import java.math.BigInteger;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
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
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return SlotPredicates.RELAY_INV.test(stack);
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
			long storedEmc = emcHolder.getStoredEmc(stack);
			inv.addEmc(BigInteger.valueOf(storedEmc));
			emcHolder.extractEmc(stack, storedEmc, EmcAction.EXECUTE);
		}
		
		if (EMCHelper.doesItemHaveEmc(stack)) {
			inv.handleKnowledge(stack.copy());
		}
	}
	
	@Nonnull
	@Override
	public ItemStack onTake(@Nonnull PlayerEntity player, @Nonnull ItemStack stack)
	{
		stack = super.onTake(player, stack);
		inv.updateClientTargets();
		return stack;
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
