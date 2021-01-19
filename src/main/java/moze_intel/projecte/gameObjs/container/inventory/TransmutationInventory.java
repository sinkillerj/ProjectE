package moze_intel.projecte.gameObjs.container.inventory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage.EmcAction;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class TransmutationInventory extends CombinedInvWrapper {

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
	private final List<ItemInfo> knowledge = new ArrayList<>();

	public TransmutationInventory(PlayerEntity player) {
		super((IItemHandlerModifiable) player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).orElseThrow(NullPointerException::new).getInputAndLocks(),
				new ItemStackHandler(2), new ItemStackHandler(16));
		this.player = player;
		this.provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).orElseThrow(NullPointerException::new);
		this.inputLocks = itemHandler[0];
		this.learning = itemHandler[1];
		this.outputs = itemHandler[2];
		if (!isServer()) {
			updateClientTargets();
		}
	}

	public boolean isServer() {
		return !player.getEntityWorld().isRemote;
	}

	/**
	 * @apiNote Call on server only
	 * @implNote The passed stack will not be directly modified by this method.
	 */
	public void handleKnowledge(ItemStack stack) {
		if (!stack.isEmpty()) {
			handleKnowledge(ItemInfo.fromStack(stack));
		}
	}

	/**
	 * @apiNote Call on server only
	 */
	public void handleKnowledge(ItemInfo info) {
		ItemInfo cleanedInfo = NBTManager.getPersistentInfo(info);
		//Pass both stacks to the Attempt Learn Event in case a mod cares about the NBT/damage difference when comparing
		if (!provider.hasKnowledge(cleanedInfo) && !MinecraftForge.EVENT_BUS.post(new PlayerAttemptLearnEvent(player, info, cleanedInfo))) {
			if (provider.addKnowledge(cleanedInfo)) {
				//Only sync the knowledge changed if the provider successfully added it
				provider.syncKnowledgeChange((ServerPlayerEntity) player, cleanedInfo, true);
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	public void itemLearned() {
		learnFlag = 300;
		unlearnFlag = 0;
		updateClientTargets();
	}

	/**
	 * @apiNote Call on server only
	 * @implNote The passed stack will not be directly modified by this method.
	 */
	public void handleUnlearn(ItemStack stack) {
		if (!stack.isEmpty()) {
			handleUnlearn(ItemInfo.fromStack(stack));
		}
	}

	/**
	 * @apiNote Call on server only
	 */
	public void handleUnlearn(ItemInfo info) {
		ItemInfo cleanedInfo = NBTManager.getPersistentInfo(info);
		if (provider.hasKnowledge(cleanedInfo) && provider.removeKnowledge(cleanedInfo)) {
			//Only sync the knowledge changed if the provider successfully removed it
			provider.syncKnowledgeChange((ServerPlayerEntity) player, cleanedInfo, false);
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	public void itemUnlearned() {
		unlearnFlag = 300;
		learnFlag = 0;
		updateClientTargets();
	}

	/**
	 * @apiNote Call on client only
	 */
	public void checkForUpdates() {
		long matterEmc = EMCHelper.getEmcValue(outputs.getStackInSlot(0));
		long fuelEmc = EMCHelper.getEmcValue(outputs.getStackInSlot(FUEL_START));
		if (BigInteger.valueOf(Math.max(matterEmc, fuelEmc)).compareTo(getAvailableEmc()) > 0) {
			updateClientTargets();
		}
	}

	public void updateClientTargets() {
		if (isServer()) {
			return;
		}
		knowledge.clear();
		knowledge.addAll(provider.getKnowledge());
		knowledge.sort(Collections.reverseOrder(Comparator.comparing(EMCHelper::getEmcValue)));
		for (int i = 0; i < outputs.getSlots(); i++) {
			outputs.setStackInSlot(i, ItemStack.EMPTY);
		}

		int pagecounter = 0;
		int desiredPage = searchpage * 12;
		ItemInfo lockInfo = null;
		BigInteger availableEMC = getAvailableEmc();
		if (!inputLocks.getStackInSlot(LOCK_INDEX).isEmpty()) {
			lockInfo = NBTManager.getPersistentInfo(ItemInfo.fromStack(inputLocks.getStackInSlot(LOCK_INDEX)));
			//Note: We look up using only the persistent information here, instead of all the data as
			// we cannot replicate the extra data anyways since it cannot be learned. So we need to make
			// sure that we only go off of the data that can be matched
			long reqEmc = EMCHelper.getEmcValue(lockInfo);
			if (availableEMC.compareTo(BigInteger.valueOf(reqEmc)) < 0) {
				return;
			}

			Iterator<ItemInfo> iter = knowledge.iterator();
			while (iter.hasNext()) {
				ItemInfo info = iter.next();
				if (EMCHelper.getEmcValue(info) > reqEmc || info.equals(lockInfo) || !doesItemMatchFilter(info)) {
					iter.remove();
				} else if (pagecounter < desiredPage) {
					pagecounter++;
					iter.remove();
				}
			}
		} else {
			Iterator<ItemInfo> iter = knowledge.iterator();
			while (iter.hasNext()) {
				ItemInfo info = iter.next();
				if (availableEMC.compareTo(BigInteger.valueOf(EMCHelper.getEmcValue(info))) < 0 || !doesItemMatchFilter(info)) {
					iter.remove();
				} else if (pagecounter < desiredPage) {
					pagecounter++;
					iter.remove();
				}
			}
		}

		int matterCounter = 0;
		int fuelCounter = 0;

		if (lockInfo != null && provider.hasKnowledge(lockInfo)) {
			ItemStack lockStack = lockInfo.createStack();
			if (FuelMapper.isStackFuel(lockStack)) {
				outputs.setStackInSlot(FUEL_START, lockStack);
				fuelCounter++;
			} else {
				outputs.setStackInSlot(0, lockStack);
				matterCounter++;
			}
		}

		for (ItemInfo info : knowledge) {
			ItemStack stack = info.createStack();
			if (FuelMapper.isStackFuel(stack)) {
				if (fuelCounter < 4) {
					outputs.setStackInSlot(FUEL_START + fuelCounter, stack);
					fuelCounter++;
				}
			} else if (matterCounter < 12) {
				outputs.setStackInSlot(matterCounter, stack);
				matterCounter++;
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	private boolean doesItemMatchFilter(ItemInfo info) {
		if (filter.isEmpty()) {
			return true;
		}
		try {
			return info.createStack().getDisplayName().getString().toLowerCase(Locale.ROOT).contains(filter);
		} catch (Exception e) {
			e.printStackTrace();
			//From old code... Not sure if intended to not remove items that crash on getDisplayName
			return true;
		}
	}

	/**
	 * @apiNote Call on server only
	 */
	public void writeIntoOutputSlot(int slot, ItemStack item) {
		long emcValue = EMCHelper.getEmcValue(item);
		if (emcValue > 0 && BigInteger.valueOf(emcValue).compareTo(getAvailableEmc()) <= 0 && provider.hasKnowledge(item)) {
			outputs.setStackInSlot(slot, item);
		} else {
			outputs.setStackInSlot(slot, ItemStack.EMPTY);
		}
	}

	/**
	 * @apiNote Call on server only
	 */
	public void addEmc(BigInteger value) {
		int compareToZero = value.compareTo(BigInteger.ZERO);
		if (compareToZero == 0) {
			//Optimization to not look at the items if nothing will happen anyways
			return;
		} else if (compareToZero < 0) {
			//Make sure it is using the correct method so that it handles the klein stars properly
			removeEmc(value.negate());
			return;
		}
		List<Integer> inputLocksChanged = new ArrayList<>();
		//Start by trying to add it to the EMC items on the left
		for (int slotIndex = 0; slotIndex < inputLocks.getSlots(); slotIndex++) {
			if (slotIndex == LOCK_INDEX) {
				continue;
			}
			ItemStack stack = inputLocks.getStackInSlot(slotIndex);
			if (!stack.isEmpty()) {
				Optional<IItemEmcHolder> holderCapability = stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
				if (holderCapability.isPresent()) {
					IItemEmcHolder emcHolder = holderCapability.get();
					long shrunkenValue = MathUtils.clampToLong(value);
					long actualInserted = emcHolder.insertEmc(stack, shrunkenValue, EmcAction.EXECUTE);
					if (actualInserted > 0) {
						inputLocksChanged.add(slotIndex);
						value = value.subtract(BigInteger.valueOf(actualInserted));
						if (value.compareTo(BigInteger.ZERO) == 0) {
							//If we fit it all then sync the changes to the client and exit
							syncChangedSlots(inputLocksChanged, TargetUpdateType.ALL);
							return;
						}
					}
				}
			}
		}
		syncChangedSlots(inputLocksChanged, TargetUpdateType.NONE);
		//Note: We act as if there is no "max" EMC for the player given we use a BigInteger
		// This means we don't have to try to put the overflow into the lock slot if there is an EMC storage item there
		updateEmcAndSync(provider.getEmc().add(value));
	}

	/**
	 * @apiNote Call on server only
	 */
	public void removeEmc(BigInteger value) {
		int compareToZero = value.compareTo(BigInteger.ZERO);
		if (compareToZero == 0) {
			//Optimization to not look at the items if nothing will happen anyways
			return;
		} else if (compareToZero < 0) {
			//Make sure it is using the correct method so that it handles the klein stars properly
			addEmc(value.negate());
			return;
		}
		BigInteger currentEmc = provider.getEmc();
		//Note: We act as if there is no "max" EMC for the player given we use a BigInteger
		// This means we don't need to first try removing it from the lock slot as it will auto drain from the lock slot
		if (value.compareTo(currentEmc) > 0) {
			//Remove from provider first
			//This code runs first to simplify the logic
			//But it simulates removal first by extracting the amount from value and then removing that excess from items
			List<Integer> inputLocksChanged = new ArrayList<>();
			BigInteger toRemove = value.subtract(currentEmc);
			value = currentEmc;
			for (int slotIndex = 0; slotIndex < inputLocks.getSlots(); slotIndex++) {
				if (slotIndex == LOCK_INDEX) {
					continue;
				}
				ItemStack stack = inputLocks.getStackInSlot(slotIndex);
				if (!stack.isEmpty()) {
					Optional<IItemEmcHolder> holderCapability = stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
					if (holderCapability.isPresent()) {
						IItemEmcHolder emcHolder = holderCapability.get();
						long shrunkenToRemove = MathUtils.clampToLong(toRemove);
						long actualExtracted = emcHolder.extractEmc(stack, shrunkenToRemove, EmcAction.EXECUTE);
						if (actualExtracted > 0) {
							inputLocksChanged.add(slotIndex);
							toRemove = toRemove.subtract(BigInteger.valueOf(actualExtracted));
							if (toRemove.compareTo(BigInteger.ZERO) == 0) {
								//The EMC that is being removed that the provider does not contain is satisfied by this IItemEMC
								//Remove it and then stop checking other input slots as we were able to provide all that was needed
								syncChangedSlots(inputLocksChanged, TargetUpdateType.IF_NEEDED);
								return;
							}
						}
					}
				}
			}
			//Sync the changed slots if any have changed
			syncChangedSlots(inputLocksChanged, TargetUpdateType.NONE);
		}
		updateEmcAndSync(currentEmc.subtract(value));
	}

	/**
	 * @apiNote Call on server only
	 */
	public void syncChangedSlots(List<Integer> slotsChanged, TargetUpdateType updateTargets) {
		provider.syncInputAndLocks((ServerPlayerEntity) player, slotsChanged, updateTargets);
	}

	/**
	 * @apiNote Call on server only
	 */
	private void updateEmcAndSync(BigInteger emc) {
		if (emc.compareTo(BigInteger.ZERO) < 0) {
			//Clamp the emc, should never be less than zero but just in case make sure to fix it
			emc = BigInteger.ZERO;
		}
		provider.setEmc(emc);
		provider.syncEmc((ServerPlayerEntity) player);
		PlayerHelper.updateScore((ServerPlayerEntity) player, PlayerHelper.SCOREBOARD_EMC, emc);
	}

	public IItemHandlerModifiable getHandlerForSlot(int slot) {
		return super.getHandlerFromIndex(super.getIndexForSlot(slot));
	}

	public int getIndexFromSlot(int slot) {
		for (IItemHandlerModifiable h : itemHandler) {
			if (slot >= h.getSlots()) {
				slot -= h.getSlots();
			}
		}
		return slot;
	}

	/**
	 * @return EMC available from the Provider + any klein stars in the input slots.
	 */
	public BigInteger getAvailableEmc() {
		BigInteger emc = provider.getEmc();
		for (int i = 0; i < inputLocks.getSlots(); i++) {
			if (i == LOCK_INDEX) {
				//Skip it even though this technically could add to available EMC.
				//This is because this case can only happen if the provider is already at max EMC
				continue;
			}
			ItemStack stack = inputLocks.getStackInSlot(i);
			if (!stack.isEmpty()) {
				Optional<IItemEmcHolder> emcHolder = stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
				if (emcHolder.isPresent()) {
					emc = emc.add(BigInteger.valueOf(emcHolder.get().getStoredEmc(stack)));
				}
			}
		}
		return emc;
	}

	public int getKnowledgeSize() {
		return knowledge.size();
	}
}