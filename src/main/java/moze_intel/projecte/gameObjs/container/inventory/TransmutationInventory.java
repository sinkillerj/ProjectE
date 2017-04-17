package moze_intel.projecte.gameObjs.container.inventory;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.ItemSearchHelper;
import moze_intel.projecte.utils.NBTWhitelist;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TransmutationInventory extends CombinedInvWrapper
{
	public final EntityPlayer player;
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
	public final List<ItemStack> knowledge = Lists.newArrayList();
	
	public TransmutationInventory(EntityPlayer player)
	{
		super((IItemHandlerModifiable) player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).getInputAndLocks(),
				new ItemStackHandler(2), new ItemStackHandler(16));

		this.player = player;
		this.provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null);

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
		if (stack.stackSize > 1)
		{
			stack.stackSize = 1;
		}
		
		if (ItemHelper.isDamageable(stack))
		{
			stack.setItemDamage(0);
		}
		
		if (!provider.hasKnowledge(stack))
		{
			learnFlag = 300;

			if (stack.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(stack))
			{
				stack.setTagCompound(null);
			}

			provider.addKnowledge(stack);
			
			if (!player.getEntityWorld().isRemote)
			{
				provider.sync(((EntityPlayerMP) player));
			}
		}
		
		updateClientTargets();
	}

	public void handleUnlearn(ItemStack stack)
	{
		if (stack.stackSize > 1)
		{
			stack.stackSize = 1;
		}

		if (ItemHelper.isDamageable(stack))
		{
			stack.setItemDamage(0);
		}

		if (provider.hasKnowledge(stack))
		{
			unlearnFlag = 300;

			if (stack.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(stack))
			{
				stack.setTagCompound(null);
			}

			provider.removeKnowledge(stack);
			
			if (!player.getEntityWorld().isRemote)
			{
				provider.sync(((EntityPlayerMP) player));
			}
		}
		
		updateClientTargets();
	}
	
	public void checkForUpdates()
	{
		int matterEmc = EMCHelper.getEmcValue(outputs.getStackInSlot(0));
		int fuelEmc = EMCHelper.getEmcValue(outputs.getStackInSlot(FUEL_START));
		
		if (Math.max(matterEmc, fuelEmc) > provider.getEmc())
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
			outputs.setStackInSlot(i, null);
		}

		ItemStack lockCopy = null;

		Collections.sort(knowledge, Collections.reverseOrder(Comparator.comparing(EMCHelper::getEmcValue)));
		ItemSearchHelper searchHelper = ItemSearchHelper.create(filter);
		if (inputLocks.getStackInSlot(LOCK_INDEX) != null)
		{
			lockCopy = ItemHelper.getNormalizedStack(inputLocks.getStackInSlot(LOCK_INDEX));

			if (ItemHelper.isDamageable(lockCopy))
			{
				lockCopy.setItemDamage(0);
			}

			int reqEmc = EMCHelper.getEmcValue(inputLocks.getStackInSlot(LOCK_INDEX));
			
			if (provider.getEmc() < reqEmc)
			{
				return;
			}

			if (lockCopy.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(lockCopy))
			{
				lockCopy.setTagCompound(new NBTTagCompound());
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

				if (ItemHelper.basicAreStacksEqual(lockCopy, stack))
				{
					iter.remove();
					continue;
				}

				if (!searchHelper.doesItemMatchFilter(stack)) {
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
				
				if (provider.getEmc() < EMCHelper.getEmcValue(stack))
				{
					iter.remove();
					continue;
				}

				if (!searchHelper.doesItemMatchFilter(stack)) {
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

		if (lockCopy != null)
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

	public void writeIntoOutputSlot(int slot, ItemStack item)
	{

		if (EMCHelper.doesItemHaveEmc(item)
				&& EMCHelper.getEmcValue(item) <= provider.getEmc()
				&& provider.hasKnowledge(item))
		{
			outputs.setStackInSlot(slot, item);
		}
		else
		{
			outputs.setStackInSlot(slot, null);
		}
	}

	public void addEmc(double value)
	{
		provider.setEmc(provider.getEmc() + value);
		
		if (provider.getEmc() >= Constants.TILE_MAX_EMC || provider.getEmc() < 0)
		{
			provider.setEmc(Constants.TILE_MAX_EMC);
		}

		if (!player.getEntityWorld().isRemote)
		{
			PlayerHelper.updateScore((EntityPlayerMP) player, AchievementHandler.SCOREBOARD_EMC, MathHelper.floor_double(provider.getEmc()));
		}
	}
	
	public void removeEmc(double value) 
	{
		provider.setEmc(provider.getEmc() - value);
		
		if (provider.getEmc() < 0)
		{
			provider.setEmc(0);
		}

		if (!player.getEntityWorld().isRemote)
		{
			PlayerHelper.updateScore((EntityPlayerMP) player, AchievementHandler.SCOREBOARD_EMC, MathHelper.floor_double(provider.getEmc()));
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

}
