package moze_intel.projecte.gameObjs.container.inventory;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.text.SearchQueryParser;
import moze_intel.projecte.utils.text.SearchQueryParser.ISearchQuery;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import moze_intel.projecte.utils.PECombinedItemStacks;

public class TransmutationInventory extends PECombinedItemStacks {

	public final Player player;
	public final IKnowledgeProvider provider;
	private final PEItemStacksHandler inputLocks;
	private final PEItemStacksHandler learning;
	public final PEItemStacksHandler outputs;

	private static final int MAX_MATTER_DISPLAY = 12;
	private static final int MAX_FUEL_DISPLAY = 4;

	private static final int LOCK_INDEX = 8;
	private static final int FUEL_START = MAX_MATTER_DISPLAY;
	public int learnFlag = 0;
	public int unlearnFlag = 0;
	public ISearchQuery filter = ISearchQuery.INVALID;
	private int searchPage = 0;
	private boolean hasNextPage;
	private long lastAvailableEmc;

	public TransmutationInventory(Player player) {
		super((PEItemStacksHandler) Objects.requireNonNull(player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)).getInputAndLocks(),
				new PEItemStacksHandler(2), new PEItemStacksHandler(16));
		this.player = player;
		this.provider = Objects.requireNonNull(player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY));
		this.inputLocks = getStackHandler(0);
		this.learning = getStackHandler(1);
		this.outputs = getStackHandler(2);
		if (isClient()) {
			//Update all targets so that we display the items to the player
			updateClientTargets(false);
		}
	}

	public boolean isServer() {
		return !isClient();
	}

	public boolean isClient() {
		return player.level().isClientSide();
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
		ItemInfo cleanedInfo = IEMCProxy.INSTANCE.getPersistentInfo(info);
		//Pass both stacks to the Attempt Learn Event in case a mod cares about the data component/damage difference when comparing
		if (!provider.hasKnowledge(cleanedInfo) && !NeoForge.EVENT_BUS.post(new PlayerAttemptLearnEvent(player, info, cleanedInfo)).isCanceled()) {
			if (provider.addKnowledge(cleanedInfo)) {
				//Only sync the knowledge changed if the provider successfully added it
				provider.syncKnowledgeChange((ServerPlayer) player, cleanedInfo, true);
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	public void itemLearned(ItemInfo learnedItem) {
		learnFlag = 300;
		unlearnFlag = 0;
		if (isServer()) {
			return;
		}
		long learnedItemEmc = IEMCProxy.INSTANCE.getValue(learnedItem);
		if (learnedItemEmc == 0 || learnedItem.getItem().is(PEItems.TOME_OF_KNOWLEDGE.getKey())) {
			//The learned item has no EMC. As the most likely case that would happen is if it was a tome of knowledge
			// we want to just update the client targets to be safe
			// Note: We also reset the search page, as while the page we are on should still exist, it is likely now meaningless
			resetSearchPage();
			updateClientTargets(false);
		} else if (doesItemMatchFilter(learnedItem)) {
			//Ensure the item matches the filter we currently are displaying, as if it doesn't then we don't have to update the display
			boolean learnedFuel = learnedItem.getItem().is(PETags.Items.COLLECTOR_FUEL);
			//Check if our newly learned item is on a later page than the one we currently are on. We don't have to update it,
			// but we do update the fact that we have a next page, in case we previously were on the last page
			if (learnedFuel) {
				long lastFuelEmc = IEMCProxy.INSTANCE.getValue(outputs.getStackInSlot(outputs.getSlots() - 1));
				if (learnedItemEmc < lastFuelEmc) {
					hasNextPage = true;
					return;
				}
			} else if (learnedItemEmc < IEMCProxy.INSTANCE.getValue(outputs.getStackInSlot(MAX_MATTER_DISPLAY - 1))) {
				hasNextPage = true;
				return;
			}
			final long availableEmc = getAvailableEmcAsLong();
			//Ensure the learned item is in the emc range that we are even trying to display. If it requires more emc than we have available,
			// we can't possibly have it end up in the targets after an update
			if (learnedItemEmc <= availableEmc) {
				ItemStack lockStack = inputLocks.getStackInSlot(LOCK_INDEX);
				if (!lockStack.isEmpty()) {
					ItemInfo lockInfo = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(lockStack));
					long lockEmc = IEMCProxy.INSTANCE.getValue(lockInfo);
					if (lockEmc != 0 && lockEmc < availableEmc && learnedItemEmc > lockEmc) {
						//If we have a lock stack, and that lock stack limits the emc to an even lower level than the available emc,
						// and the learned item has an emc value that is above the emc value that the lock is limiting it to:
						// it doesn't get displayed because of the lock, so we can skip updating the displayed items
						return;
					}
				}
				updateClientTargets(availableEmc);
			}
		}
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
		ItemInfo cleanedInfo = IEMCProxy.INSTANCE.getPersistentInfo(info);
		if (provider.hasKnowledge(cleanedInfo) && provider.removeKnowledge(cleanedInfo)) {
			//Only sync the knowledge changed if the provider successfully removed it
			provider.syncKnowledgeChange((ServerPlayer) player, cleanedInfo, false);
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	public void itemUnlearned(ItemInfo unlearnedItem) {
		unlearnFlag = 300;
		learnFlag = 0;
		if (isServer()) {
			return;
		}
		long unlearnedItemEmc = IEMCProxy.INSTANCE.getValue(unlearnedItem);
		if (unlearnedItemEmc == 0 || unlearnedItem.getItem().is(PEItems.TOME_OF_KNOWLEDGE.getKey())) {
			//The removed item has no EMC. As the most likely case that would happen is if it was a tome of knowledge
			// we want to just update the client targets to be safe
			// Note: We also reset the search page so that we don't accidentally end up on a page that no longer exists
			resetSearchPage();
			updateClientTargets(false);
		} else if (doesItemMatchFilter(unlearnedItem)) {
			//Ensure the item matches the filter we currently are displaying, as if it doesn't then we don't have to update the display
			if (unlearnedItem.getItem().is(PETags.Items.COLLECTOR_FUEL)) {
				maybeUpdateClientTargets(unlearnedItem, unlearnedItemEmc, FUEL_START, outputs.getSlots());
			} else {
				maybeUpdateClientTargets(unlearnedItem, unlearnedItemEmc, 0, MAX_MATTER_DISPLAY);
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	private void maybeUpdateClientTargets(ItemInfo unlearnedItem, long unlearnedItemEmc, int slot, int slots) {
		final long availableEmc = getAvailableEmcAsLong();
		if (unlearnedItemEmc <= availableEmc) {
			//Validate that the item has a chance of being displayed. If it costs more than the emc we have available, there is no chance it is being displayed
			int firstNonLockSlot = slot;
			ItemStack lockStack = inputLocks.getStackInSlot(LOCK_INDEX);
			long lockEmc = 0;
			if (!lockStack.isEmpty()) {
				ItemInfo lockInfo = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(lockStack));
				lockEmc = IEMCProxy.INSTANCE.getValue(lockInfo);
				if (lockEmc > availableEmc) {//We don't have enough emc to display the lock stack, so it isn't in the gui
					lockStack = ItemStack.EMPTY;
				} else {
					firstNonLockSlot++;
				}
			}
			for (; slot < slots; slot++) {
				ItemStack stack = outputs.getStackInSlot(slot);
				if (stack.isEmpty()) {//We know later slots will also be empty
					break;
				} else if (stack.is(unlearnedItem.getItem()) && stack.getComponentsPatch().equals(unlearnedItem.getComponentsPatch())) {
					//If the item was in our list of displayed stacks, then we definitely need to update the targets
					if (hasPreviousPage()) {
						if (!lockStack.isEmpty() && slot == firstNonLockSlot - 1) {
							//We are removing the lock, just reset the search page
							resetSearchPage();
						} else if (slot == firstNonLockSlot && slot + 1 < slots) {
							//We are removing the item in the first slot index that isn't the lock
							ItemStack next = outputs.getStackInSlot(slot + 1);
							if (next.isEmpty()) {
								// if the next slot is empty, that means there are no more of this type, and we should go to the previous page
								searchPage--;
							}
						}
					}
					updateClientTargets(availableEmc);
					return;
				}
			}
			if (lockStack.isEmpty()) {
				//Just use the max emc value that we have being displayed
				long maxDisplayedEmc = getMaxDisplayedEmc();
				if (unlearnedItemEmc >= maxDisplayedEmc) {//If the removed item was potentially on an earlier page, update the targets
					if (hasPreviousPage()) {
						//Subtract one from the current slot as we break out after doing a slot++
						// and this will let us know the last slot that had an item in it
						if (slot - 1 == firstNonLockSlot) {
							//If it was the first slot that didn't have a lock in it (our initial slot), we need to lower our search page,
							// as this one will no longer have anything on it
							searchPage--;
						}
					}
					updateClientTargets(availableEmc);
				}
			} else if (lockEmc > 0 && unlearnedItemEmc == lockEmc) {
				//If the item was potentially on an earlier page, we need to update our targets
				// We only need to check exact equals rather than <=, as if the value was smaller than the lock
				// and not on our current page, then it would be on a later page
				if (hasPreviousPage()) {
					//Subtract one from the current slot as we break out after doing a slot++
					// and this will let us know the last slot that had an item in it
					if (slot - 1 == firstNonLockSlot) {
						//If it was the first slot that didn't have a lock in it, we need to lower our search page,
						// as this one will no longer have anything on it
						searchPage--;
					}
				}
				updateClientTargets(availableEmc);
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	public void checkForUpdates() {
		long availableEmc = getAvailableEmcAsLong();
		if (getMaxDisplayedEmc() > availableEmc) {
			//Available EMC is lower than what we are displaying, we need to update the targets
			updateClientTargets(availableEmc);
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	private long getMaxDisplayedEmc() {
		long matterEmc = IEMCProxy.INSTANCE.getValue(outputs.getStackInSlot(0));
		long fuelEmc = IEMCProxy.INSTANCE.getValue(outputs.getStackInSlot(FUEL_START));
		return Math.max(matterEmc, fuelEmc);
	}

	public void updateClientTargets(boolean checkForEmcChange) {
		if (isClient()) {
			long availableEmc = getAvailableEmcAsLong();
			if (!checkForEmcChange) {
				updateClientTargets(availableEmc);
			} else if (lastAvailableEmc != availableEmc) {
				//If the amount of emc we have available has changed, we want to recheck to see if any of the targets have shifted
				//TODO: Can we do any other checks for emc value changes to further optimize if we are updating the targets?
				updateClientTargets(availableEmc);
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	private void updateClientTargets(long availableEMC) {
		lastAvailableEmc = availableEMC;
		for (int i = 0, slots = outputs.getSlots(); i < slots; i++) {
			outputs.setStackInSlot(i, ItemStack.EMPTY);
		}
		record EmcData(ItemInfo info, long emc) {
		}
		Predicate<EmcData> filterPredicate;
		ItemStack lockStack = inputLocks.getStackInSlot(LOCK_INDEX);
		int matterCounter = 0;
		int fuelCounter = 0;
		if (lockStack.isEmpty()) {
			filterPredicate = data -> data.emc() > 0 && data.emc() <= availableEMC;
		} else {
			ItemInfo lockInfo = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(lockStack));
			//Note: We look up using only the persistent information here, instead of all the data as
			// we cannot replicate the extra data anyway since it cannot be learned. So we need to make
			// sure that we only go off of the data that can be matched
			long reqEmc = IEMCProxy.INSTANCE.getValue(lockInfo);
			if (availableEMC < reqEmc || reqEmc == 0) {
				//If we have less emc available than the item we are filtering by (or somehow our lock has no emc), just do the normal filtering as if we didn't have a lock
				filterPredicate = data -> data.emc() > 0 && data.emc() <= availableEMC;
			} else if (provider.hasKnowledge(lockInfo)) {
				//Note: We can just check the tag, as we know it has an emc value
				if (lockInfo.getItem().is(PETags.Items.COLLECTOR_FUEL)) {
					outputs.setStackInSlot(FUEL_START + fuelCounter++, lockInfo.createStack());
				} else {
					outputs.setStackInSlot(matterCounter++, lockInfo.createStack());
				}
				//Otherwise, we need to filter based on the lower value emc target the user is filtering by,
				// and exclude the lock item itself as we have already manually included that ourselves at the start
				filterPredicate = data -> {
					long emc = data.emc();
					if (emc > 0) {
						if (emc < reqEmc) {
							return true;
						} else if (emc == reqEmc) {
							return !data.info().equals(lockInfo);
						}
					}
					return false;
				};
			} else {
				//Otherwise, we need to filter based on the lower value emc target the user is filtering by
				filterPredicate = data -> data.emc() > 0 && data.emc() <= reqEmc;
			}
		}

		List<ItemInfo> knowledge = provider.getKnowledge().stream()
				.map(info -> new EmcData(info, IEMCProxy.INSTANCE.getValue(info)))
				.filter(filterPredicate)
				.sorted(Comparator.comparingLong(EmcData::emc).reversed())
				.map(EmcData::info)
				.toList();

		int fuelPageCounter = 0;
		int matterPageCounter = 0;
		//Take into account if the lock is being displayed at the top of each page for how many fuel/matter fits on a singular page
		int desiredFuelPage = searchPage * (MAX_FUEL_DISPLAY - fuelCounter);
		int desiredMatterPage = searchPage * (MAX_MATTER_DISPLAY - matterCounter);
		hasNextPage = false;
		for (ItemInfo info : knowledge) {
			//Note: We can just check the tag, as we know it has an emc value
			if (info.getItem().is(PETags.Items.COLLECTOR_FUEL)) {
				if (fuelCounter < MAX_FUEL_DISPLAY) {
					if (doesItemMatchFilter(info)) {
						if (fuelPageCounter == desiredFuelPage) {
							outputs.setStackInSlot(FUEL_START + fuelCounter++, info.createStack());
						} else {
							fuelPageCounter++;
						}
					}
				} else {
					hasNextPage = true;
					if (matterCounter == MAX_MATTER_DISPLAY) {//We have all the fuel and matter that we need to display
						break;
					}
				}
			} else if (matterCounter < MAX_MATTER_DISPLAY) {
				if (doesItemMatchFilter(info)) {
					if (matterPageCounter == desiredMatterPage) {
						outputs.setStackInSlot(matterCounter++, info.createStack());
					} else {
						//Increment the page counter if the item would be in our filter
						matterPageCounter++;
					}
				}
			} else {
				hasNextPage = true;
				if (fuelCounter == MAX_FUEL_DISPLAY) {//We have all the fuel and matter that we need to display
					break;
				}
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	private boolean doesItemMatchFilter(ItemInfo info) {
		return filter.isInvalid() || filter.test(player.level(), player, info.createStack());
	}

	/**
	 * @apiNote Call on server only
	 */
	public void writeIntoOutputSlot(int slot, ItemStack item) {
		long emcValue = IEMCProxy.INSTANCE.getValue(item);
		if (emcValue > 0 && emcValue <= getAvailableEmcAsLong() && provider.hasKnowledge(item)) {
			outputs.setStackInSlot(slot, item);
		} else {
			outputs.setStackInSlot(slot, ItemStack.EMPTY);
		}
	}

	/**
	 * @apiNote Call on server only
	 */
	public void addEmc(BigInteger value) {
		if (value.signum() == 0) {//value == 0
			//Optimization to not look at the items if nothing will happen anyway
			return;
		} else if (value.signum() == -1) {//value < 0
			//Make sure it is using the correct method so that it handles the klein stars properly
			removeEmc(value.negate());
			return;
		}
		IntList inputLocksChanged = new IntArrayList();
		//Start by trying to add it to the EMC items on the left
		for (int slotIndex = 0, slots = inputLocks.getSlots(); slotIndex < slots; slotIndex++) {
			if (slotIndex == LOCK_INDEX) {
				continue;
			}
			ItemStack stack = inputLocks.getStackInSlot(slotIndex);
			IItemEmcHolder emcHolder = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				long shrunkenValue = MathUtils.clampToLong(value);
				long actualInserted = emcHolder.insertEmc(stack, shrunkenValue, EmcAction.EXECUTE);
				if (actualInserted > 0) {
					inputLocksChanged.add(slotIndex);
					value = value.subtract(BigInteger.valueOf(actualInserted));
					if (value.signum() == 0) {//value == 0
						//If we fit it all then sync the changes to the client and exit
						syncChangedSlots(inputLocksChanged, TargetUpdateType.ALL);
						return;
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
		if (value.signum() == 0) {//value == 0
			//Optimization to not look at the items if nothing will happen anyway
			return;
		} else if (value.signum() == -1) {//value < 0
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
			IntList inputLocksChanged = new IntArrayList();
			BigInteger toRemove = value.subtract(currentEmc);
			value = currentEmc;
			for (int slotIndex = 0, slots = inputLocks.getSlots(); slotIndex < slots; slotIndex++) {
				if (slotIndex == LOCK_INDEX) {
					continue;
				}
				ItemStack stack = inputLocks.getStackInSlot(slotIndex);
				IItemEmcHolder emcHolder = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
				if (emcHolder != null) {
					long shrunkenToRemove = MathUtils.clampToLong(toRemove);
					long actualExtracted = emcHolder.extractEmc(stack, shrunkenToRemove, EmcAction.EXECUTE);
					if (actualExtracted > 0) {
						inputLocksChanged.add(slotIndex);
						toRemove = toRemove.subtract(BigInteger.valueOf(actualExtracted));
						if (toRemove.signum() == 0) {//toRemove == 0
							//The EMC that is being removed that the provider does not contain is satisfied by this IItemEMC
							//Remove it and then stop checking other input slots as we were able to provide all that was needed
							syncChangedSlots(inputLocksChanged, TargetUpdateType.IF_NEEDED);
							if (currentEmc.signum() == 1) {//currentEmc > 0
								updateEmcAndSync(BigInteger.ZERO);
							}
							return;
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
	public void syncChangedSlots(IntList slotsChanged, TargetUpdateType updateTargets) {
		provider.syncInputAndLocks((ServerPlayer) player, slotsChanged, updateTargets);
	}

	/**
	 * @apiNote Call on server only
	 */
	private void updateEmcAndSync(BigInteger emc) {
		if (emc.signum() == -1) {//emc < 0
			//Clamp the emc, should never be less than zero but just in case make sure to fix it
			emc = BigInteger.ZERO;
		}
		provider.setEmc(emc);
		provider.syncEmc((ServerPlayer) player);
		PlayerHelper.updateScore((ServerPlayer) player, PlayerHelper.SCOREBOARD_EMC, emc);
	}

	public PEItemStacksHandler getHandlerForSlot(int slot) {
		int handlerIndex = 0;
		for (int i = 0; i < 3; i++) {
			PEItemStacksHandler h = getStackHandler(i);
			if (slot < h.getSlots()) {
				handlerIndex = i;
				break;
			}
			slot -= h.getSlots();
		}
		return getStackHandler(handlerIndex);
	}

	public int getIndexFromSlot(int slot) {
		for (int i = 0; i < 3; i++) {
			PEItemStacksHandler h = getStackHandler(i);
			if (slot < h.getSlots()) {
				return slot;
			}
			slot -= h.getSlots();
		}
		return slot;
	}

	/**
	 * @return EMC available from the Provider + any klein stars in the input slots clamped to max long.
	 */
	public long getAvailableEmcAsLong() {
		long emc = MathUtils.clampToLong(provider.getEmc());
		if (emc == Long.MAX_VALUE || inputLocks.getSlots() == 0) {
			//If we already are at max or somehow don't have any slots
			return emc;
		}
		long emcToMax = Long.MAX_VALUE - emc;
		for (int i = 0, slots = inputLocks.getSlots(); i < slots; i++) {
			if (i == LOCK_INDEX) {
				//Skip it even though this technically could add to available EMC.
				//This is because this case can only happen if the provider is already at max EMC
				continue;
			}
			ItemStack stack = inputLocks.getStackInSlot(i);
			IItemEmcHolder emcHolder = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				long storedEmc = emcHolder.getStoredEmc(stack);
				if (storedEmc >= emcToMax) {
					return Long.MAX_VALUE;
				}
				emcToMax -= storedEmc;
			}
		}
		return Long.MAX_VALUE - emcToMax;
	}

	/**
	 * @return EMC available from the Provider + any klein stars in the input slots.
	 */
	public BigInteger getAvailableEmc() {
		BigInteger emc = provider.getEmc();
		for (int i = 0, slots = inputLocks.getSlots(); i < slots; i++) {
			if (i == LOCK_INDEX) {
				//Skip it even though this technically could add to available EMC.
				//This is because this case can only happen if the provider is already at max EMC
				continue;
			}
			ItemStack stack = inputLocks.getStackInSlot(i);
			IItemEmcHolder emcHolder = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				emc = emc.add(BigInteger.valueOf(emcHolder.getStoredEmc(stack)));
			}
		}
		return emc;
	}

	public void updateFilter(String text) {
		String search = text.trim().toLowerCase(Locale.ROOT);
		ISearchQuery query = SearchQueryParser.parse(search);
		if (!filter.equals(query)) {
			filter = query;
			resetSearchPage();
			updateClientTargets(false);
		}
	}

	public boolean hasPreviousPage() {
		return searchPage > 0;
	}

	public boolean hasNextPage() {
		return hasNextPage;
	}

	private void resetSearchPage() {
		searchPage = 0;
	}

	public void previousPage() {
		if (hasPreviousPage()) {
			searchPage--;
			//TODO: Can we optimize updating the targets based on what are currently displaying? Probably not in a way that is worth it
			updateClientTargets(false);
		}
	}

	public void nextPage() {
		if (hasNextPage()) {
			searchPage++;
			//TODO: Can we optimize updating the targets based on what are currently displaying? Probably not in a way that is worth it
			updateClientTargets(false);
		}
	}
}
