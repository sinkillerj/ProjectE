package moze_intel.projecte.gameObjs.container.inventory;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.*;

public class TransmutationInventory extends CombinedInvWrapper
{
	public final PlayerEntity player;
	public final IKnowledgeProvider provider;
	private final IItemHandlerModifiable inputLocks;
	private final IItemHandlerModifiable learning;
	public final IItemHandlerModifiable outputs;

	private static final int LOCK_INDEX = 8;
	private static final int FUEL_START = 12;
	public int learnFlag = 0;
	public int unlearnFlag = 0;
	public String filter = "";
	public int searchpage = 0;
	public final List<ItemStack> knowledge = new ArrayList<>();
	
	public TransmutationInventory(PlayerEntity player)
	{
		super((IItemHandlerModifiable) player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY)
						.orElseThrow(NullPointerException::new)
						.getInputAndLocks(),
				new ItemStackHandler(2), new ItemStackHandler(16));

		this.player = player;
		this.provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).orElseThrow(NullPointerException::new);

		this.inputLocks = itemHandler[0];
		this.learning = itemHandler[1];
		this.outputs = itemHandler[2];

		if (player.getEntityWorld().isRemote)
		{
			updateClientTargets();
		}
	}
	
	public void handleKnowledge(ItemStack stack)
	{
		if (stack.getCount() > 1)
		{
			stack.setCount(1);
		}
		
		if (ItemHelper.isDamageable(stack))
		{
			stack.setDamage(0);
		}
		
		if (!provider.hasKnowledge(stack))
		{
			if (stack.hasTag() && !ItemHelper.shouldDupeWithNBT(stack))
			{
				stack.setTag(null);
			}

			if (!MinecraftForge.EVENT_BUS.post(new PlayerAttemptLearnEvent(player, stack))) //Only show the "learned" text if the knowledge was added
			{
				learnFlag = 300;
				unlearnFlag = 0;
				provider.addKnowledge(stack);
			}

			if (!player.getEntityWorld().isRemote)
			{
				provider.sync(((ServerPlayerEntity) player));
			}
		}
		
		updateClientTargets();
	}

	public void handleUnlearn(ItemStack stack)
	{
		if (stack.getCount() > 1)
		{
			stack.setCount(1);
		}

		if (ItemHelper.isDamageable(stack))
		{
			stack.setDamage(0);
		}

		if (provider.hasKnowledge(stack))
		{
			unlearnFlag = 300;
			learnFlag = 0;

			if (stack.hasTag() && !ItemHelper.shouldDupeWithNBT(stack))
			{
				stack.setTag(null);
			}

			provider.removeKnowledge(stack);
			
			if (!player.getEntityWorld().isRemote)
			{
				provider.sync(((ServerPlayerEntity) player));
			}
		}
		
		updateClientTargets();
	}
	
	public void checkForUpdates()
	{
		long matterEmc = EMCHelper.getEmcValue(outputs.getStackInSlot(0));
		long fuelEmc = EMCHelper.getEmcValue(outputs.getStackInSlot(FUEL_START));
		
		if (Math.max(matterEmc, fuelEmc) > getAvailableEMC())
		{
			updateClientTargets();
		}
	}

	public void updateClientTargets()
	{
		if (!this.player.getEntityWorld().isRemote)
		{
			return;
		}

		knowledge.clear();
		knowledge.addAll(provider.getKnowledge());

		for (int i = 0; i < outputs.getSlots(); i++)
		{
			outputs.setStackInSlot(i, ItemStack.EMPTY);
		}

		ItemStack lockCopy = ItemStack.EMPTY;

		knowledge.sort(Collections.reverseOrder(Comparator.comparing(EMCHelper::getEmcValue)));
		if (!inputLocks.getStackInSlot(LOCK_INDEX).isEmpty())
		{
			lockCopy = ItemHelper.getNormalizedStack(inputLocks.getStackInSlot(LOCK_INDEX));

			if (ItemHelper.isDamageable(lockCopy))
			{
				lockCopy.setDamage(0);
			}

			long reqEmc = EMCHelper.getEmcValue(inputLocks.getStackInSlot(LOCK_INDEX));
			
			if (getAvailableEMC() < reqEmc)
			{
				return;
			}

			if (lockCopy.hasTag() && !ItemHelper.shouldDupeWithNBT(lockCopy))
			{
				lockCopy.setTag(null);
			}
			
			Iterator<ItemStack> iter = knowledge.iterator();
			int pagecounter = 0;
			
			while (iter.hasNext())
			{
				ItemStack stack = iter.next();
				
				if (EMCHelper.getEmcValue(stack) > reqEmc)
				{
					iter.remove();
					continue;
				}

                if (lockCopy.getItem() == stack.getItem())
				{
					iter.remove();
					continue;
				}

				if (!doesItemMatchFilter(stack)) {
					iter.remove();
					continue;
				}

				if (pagecounter < (searchpage * 12))
				{
					pagecounter++;
					iter.remove();
				}
			}
		}
		else
		{
			Iterator<ItemStack> iter = knowledge.iterator();
			int pagecounter = 0;
			
			while (iter.hasNext())
			{
				ItemStack stack = iter.next();
				
				if (getAvailableEMC() < EMCHelper.getEmcValue(stack))
				{
					iter.remove();
					continue;
				}

				if (!doesItemMatchFilter(stack)) {
					iter.remove();
					continue;
				}

				if (pagecounter < (searchpage * 12))
				{
					pagecounter++;
					iter.remove();
				}
			}
		}
		
		int matterCounter = 0;
		int fuelCounter = 0;

		if (!lockCopy.isEmpty() && provider.hasKnowledge(lockCopy))
		{
			if (FuelMapper.isStackFuel(lockCopy))
			{
				outputs.setStackInSlot(FUEL_START, lockCopy);
				fuelCounter++;
			}
			else
			{
				outputs.setStackInSlot(0, lockCopy);
				matterCounter++;
			}
		}
		
		for (ItemStack stack : knowledge)
		{
			if (FuelMapper.isStackFuel(stack))
			{
				if (fuelCounter < 4)
				{
					outputs.setStackInSlot(FUEL_START + fuelCounter, stack);

					fuelCounter++;
				}
			}
			else
			{
				if (matterCounter < 12)
				{
					outputs.setStackInSlot(matterCounter, stack);

					matterCounter++;
 				}
			}
		}
	}

	private boolean doesItemMatchFilter(ItemStack stack)
	{
		String displayName;

		try
		{
			displayName = stack.getDisplayName().getUnformattedComponentText().toLowerCase(Locale.ROOT);
		} catch (Exception e)
		{
			e.printStackTrace();
			//From old code... Not sure if intended to not remove items that crash on getDisplayName
			return true;
		}

		if (displayName == null)
		{
			return false;
		}
		else if (filter.length() > 0 && !displayName.contains(filter))
		{
			return false;
		}
		return true;
	}

	public void writeIntoOutputSlot(int slot, ItemStack item)
	{

		if (EMCHelper.doesItemHaveEmc(item)
				&& EMCHelper.getEmcValue(item) <= getAvailableEMC()
				&& provider.hasKnowledge(item))
		{
			outputs.setStackInSlot(slot, item);
		}
		else
		{
			outputs.setStackInSlot(slot, ItemStack.EMPTY);
		}
	}

	public void addEmc(long value)
	{
		if (value == 0)
		{
			//Optimization to not look at the items if nothing will happen anyways
			return;
		}
		if (value < 0)
		{
			//Make sure it is using the correct method so that it handles the klein stars properly
			removeEmc(-value);
		}
		//Start by trying to add it to the EMC items on the left
		for (int i = 0; i < inputLocks.getSlots(); i++)
		{
			if (i == LOCK_INDEX)
			{
				continue;
			}
			ItemStack stack = inputLocks.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) stack.getItem());
				long neededEmc = itemEmc.getMaximumEmc(stack) - itemEmc.getStoredEmc(stack);
				if (value <= neededEmc)
				{
					//This item can store all of the amount being added
					itemEmc.addEmc(stack, value);
					return;
				}
				//else more than this item can fit, so fill the item and then continue going
				itemEmc.addEmc(stack, neededEmc);
				value -= neededEmc;
			}
		}
		long emcToMax = Constants.TILE_MAX_EMC - provider.getEmc();
		if (value > emcToMax)
		{
			long excessEMC = value - emcToMax;
			value = emcToMax;
			//Will finish filling provider
			//Now with excess EMC we can check against the lock slot as that is the last spot that has its EMC used.
			ItemStack stack = inputLocks.getStackInSlot(LOCK_INDEX);
			if (!stack.isEmpty() && stack.getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) stack.getItem());
				long neededEmc = itemEmc.getMaximumEmc(stack) - itemEmc.getStoredEmc(stack);
				itemEmc.addEmc(stack, Math.min(excessEMC, neededEmc));
			}
		}

		provider.setEmc(provider.getEmc() + value);
		
		if (provider.getEmc() >= Constants.TILE_MAX_EMC || provider.getEmc() < 0)
		{
			provider.setEmc(Constants.TILE_MAX_EMC);
		}

		if (!player.getEntityWorld().isRemote)
		{
			PlayerHelper.updateScore((ServerPlayerEntity) player, PlayerHelper.SCOREBOARD_EMC, MathHelper.floor(provider.getEmc()));
		}
	}
	
	public void removeEmc(long value) 
	{
		if (value == 0)
		{
			//Optimization to not look at the items if nothing will happen anyways
			return;
		}
		if (value < 0)
		{
			//Make sure it is using the correct method so that it handles the klein stars properly
			addEmc(-value);
		}
		if (hasMaxedEmc())
		{
			//If the EMC is maxed, check and try to remove from the lock slot if it is IItemEMC
			//This is the only case if the provider is full when the IItemEMC was put in the lock slot
			ItemStack stack = inputLocks.getStackInSlot(LOCK_INDEX);
			if (!stack.isEmpty() && stack.getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) stack.getItem());
				long storedEmc = itemEmc.getStoredEmc(stack);
				if (storedEmc >= value)
				{
					//All of it can be removed from the lock item
					itemEmc.extractEmc(stack, value);
					return;
				}
				itemEmc.extractEmc(stack, storedEmc);
				value -= storedEmc;
			}
		}
		if (value > provider.getEmc())
		{
			//Remove from provider first
			//This code runs first to simplify the logic
			//But it simulates removal first by extracting the amount from value and then removing that excess from items
			long toRemove = value - provider.getEmc();
			value = provider.getEmc();
			for (int i = 0; i < inputLocks.getSlots(); i++)
			{
				if (i == LOCK_INDEX)
				{
					continue;
				}
				ItemStack stack = inputLocks.getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() instanceof IItemEmc)
				{
					IItemEmc itemEmc = ((IItemEmc) stack.getItem());
					long storedEmc = itemEmc.getStoredEmc(stack);
					if (toRemove <= storedEmc)
					{
						//The EMC that is being removed that the provider does not contain is satisfied by this IItemEMC
						//Remove it and then
						itemEmc.extractEmc(stack, toRemove);
						break;
					}
					//Removes all the emc from this item
					itemEmc.extractEmc(stack, storedEmc);
					toRemove -= storedEmc;
				}
			}
		}
		provider.setEmc(provider.getEmc() - value);
		
		if (provider.getEmc() < 0)
		{
			provider.setEmc(0);
		}

		if (!player.getEntityWorld().isRemote)
		{
			PlayerHelper.updateScore((ServerPlayerEntity) player, PlayerHelper.SCOREBOARD_EMC, MathHelper.floor(provider.getEmc()));
		}
	}

	public boolean hasMaxedEmc()
	{
		return provider.getEmc() >= Constants.TILE_MAX_EMC;
	}

	public IItemHandlerModifiable getHandlerForSlot(int slot)
	{
		return super.getHandlerFromIndex(super.getIndexForSlot(slot));
	}

	public int getIndexFromSlot(int slot)
	{
		for (IItemHandlerModifiable h : itemHandler)
		{
			if (slot >= h.getSlots())
			{
				slot -= h.getSlots();
			}
		}

		return slot;
	}

	/**
	 * @return EMC available from the Provider + any klein stars in the input slots.
	 */
	public long getAvailableEMC()
	{
		//TODO: Cache this value somehow, or at least cache which slots have IItemEMC in them?
		if (hasMaxedEmc())
		{
			return Constants.TILE_MAX_EMC;
		}

		long emc = provider.getEmc();
		long emcToMax = Constants.TILE_MAX_EMC - emc;
		for (int i = 0; i < inputLocks.getSlots(); i++)
		{
			if (i == LOCK_INDEX)
			{
				//Skip it even though this technically could add to available EMC.
				//This is because this case can only happen if the provider is already at max EMC
				continue;
			}
			ItemStack stack = inputLocks.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) stack.getItem());
				long storedEmc = itemEmc.getStoredEmc(stack);
				if (storedEmc >= emcToMax)
				{
					return Constants.TILE_MAX_EMC;
				}
				emc += storedEmc;
				emcToMax -= storedEmc;
			}
		}
		return emc;
	}

}
