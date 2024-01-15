package moze_intel.projecte.gameObjs.block_entities;

import java.util.Optional;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class DMFurnaceBlockEntity extends EmcBlockEntity implements MenuProvider {

	public static final ICapabilityProvider<DMFurnaceBlockEntity, @Nullable Direction, IItemHandler> INVENTORY_PROVIDER = (furnace, side) -> {
		if (side == null) {
			return furnace.joined;
		} else if (side == Direction.UP) {
			return furnace.automationInput;
		} else if (side == Direction.DOWN) {
			return furnace.automationOutput;
		}
		return furnace.automationSides;
	};
	private static final long EMC_CONSUMPTION = 2;

	private final CompactableStackHandler inputInventory = new CompactableStackHandler(getInvSize());
	private final CompactableStackHandler outputInventory = new CompactableStackHandler(getInvSize());
	private final StackHandler fuelInv = new StackHandler(1);

	private final IItemHandler joined;
	private final IItemHandlerModifiable automationInput;
	private final IItemHandlerModifiable automationOutput;
	private final IItemHandler automationSides;

	protected final int ticksBeforeSmelt;
	private final int efficiencyBonus;
	private final RecipeWrapper dummyFurnace = new RecipeWrapper(new ItemStackHandler());

	@Nullable
	private BlockCapabilityCache<IItemHandler, @Nullable Direction> pullTarget;
	@Nullable
	private BlockCapabilityCache<IItemHandler, @Nullable Direction> pushTarget;

	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int furnaceCookTime;

	public DMFurnaceBlockEntity(BlockPos pos, BlockState state) {
		this(PEBlockEntityTypes.DARK_MATTER_FURNACE, pos, state, 10, 3);
	}

	protected DMFurnaceBlockEntity(BlockEntityTypeRegistryObject<? extends DMFurnaceBlockEntity> type, BlockPos pos, BlockState state, int ticksBeforeSmelt, int efficiencyBonus) {
		super(type, pos, state, 64);
		this.ticksBeforeSmelt = ticksBeforeSmelt;
		this.efficiencyBonus = efficiencyBonus;

		this.automationInput = new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN) {
			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				return getSmeltingResult(stack).isEmpty() ? stack : super.insertItem(slot, stack, simulate);
			}
		};
		this.automationOutput = new WrappedItemHandler(outputInventory, WrappedItemHandler.WriteMode.OUT);
		IItemHandlerModifiable automationFuel = new WrappedItemHandler(fuelInv, WrappedItemHandler.WriteMode.IN) {
			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				return SlotPredicates.FURNACE_FUEL.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
			}
		};
		this.automationSides = new CombinedInvWrapper(automationFuel, automationOutput);
		this.joined = new CombinedInvWrapper(automationInput, automationFuel, automationOutput);
	}

	@Override
	public void setLevel(@NotNull Level level) {
		super.setLevel(level);
		if (level instanceof ServerLevel serverLevel) {
			pullTarget = BlockCapabilityCache.create(ItemHandler.BLOCK, serverLevel, worldPosition.above(), Direction.DOWN);
			pushTarget = BlockCapabilityCache.create(ItemHandler.BLOCK, serverLevel, worldPosition.below(), Direction.UP);
		}
	}

	@Override
	protected boolean canProvideEmc() {
		return false;
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	protected long getEmcInsertLimit() {
		return EMC_CONSUMPTION;
	}

	protected int getInvSize() {
		return 9;
	}

	protected float getOreDoubleChance() {
		return 0.5F;
	}

	protected float getRawOreDoubleChance() {
		//Base rate for raw ore doubling chance is: 1 -> 1.333 which means we multiply our ore double chance by 2/3
		return getOreDoubleChance() * 2 / 3;
	}

	public int getCookProgressScaled(int value) {
		return furnaceCookTime * value / ticksBeforeSmelt;
	}

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInv, @NotNull Player playerIn) {
		return new DMFurnaceContainer(windowId, playerInv, this);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return PELang.GUI_DARK_MATTER_FURNACE.translate();
	}

	public IItemHandler getFuel() {
		return fuelInv;
	}

	private ItemStack getFuelItem() {
		return fuelInv.getStackInSlot(0);
	}

	public IItemHandler getInput() {
		return inputInventory;
	}

	public IItemHandler getOutput() {
		return outputInventory;
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, DMFurnaceBlockEntity furnace) {
		boolean wasBurning = furnace.isBurning();
		int lastFurnaceBurnTime = furnace.furnaceBurnTime;
		int lastFurnaceCookTime = furnace.furnaceCookTime;
		if (furnace.isBurning()) {
			--furnace.furnaceBurnTime;
		}
		furnace.inputInventory.compact();
		furnace.outputInventory.compact();
		furnace.pullFromInventories();
		boolean canSmelt = furnace.canSmelt();
		ItemStack fuelItem = furnace.getFuelItem();
		if (canSmelt && !fuelItem.isEmpty()) {
			IItemEmcHolder emcHolder = fuelItem.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				long simulatedExtraction = emcHolder.extractEmc(fuelItem, EMC_CONSUMPTION, EmcAction.SIMULATE);
				if (simulatedExtraction == EMC_CONSUMPTION) {
					furnace.forceInsertEmc(emcHolder.extractEmc(fuelItem, simulatedExtraction, EmcAction.EXECUTE), EmcAction.EXECUTE);
				}
				furnace.markDirty(false);
			}
		}

		if (furnace.getStoredEmc() >= EMC_CONSUMPTION) {
			furnace.furnaceBurnTime = 1;
			furnace.forceExtractEmc(EMC_CONSUMPTION, EmcAction.EXECUTE);
		}

		if (canSmelt) {
			if (furnace.furnaceBurnTime == 0) {
				furnace.currentItemBurnTime = furnace.furnaceBurnTime = furnace.getItemBurnTime(fuelItem);
				if (furnace.isBurning() && !fuelItem.isEmpty()) {
					ItemStack copy = fuelItem.copy();
					fuelItem.shrink(1);
					furnace.fuelInv.onContentsChanged(0);
					if (fuelItem.isEmpty()) {
						furnace.fuelInv.setStackInSlot(0, copy.getItem().getCraftingRemainingItem(copy));
					}
					furnace.markDirty(false);
				}
			}
			if (furnace.isBurning() && ++furnace.furnaceCookTime == furnace.ticksBeforeSmelt) {
				furnace.furnaceCookTime = 0;
				furnace.smeltItem();
			}
		}
		if (wasBurning != furnace.isBurning()) {
			if (state.getBlock() instanceof MatterFurnace) {
				//Should always be true, but validate it just in case
				level.setBlockAndUpdate(pos, state.setValue(MatterFurnace.LIT, furnace.isBurning()));
			}
			furnace.setChanged();
		}
		furnace.pushToInventories();
		if (lastFurnaceBurnTime != furnace.furnaceBurnTime || lastFurnaceCookTime != furnace.furnaceCookTime) {
			furnace.markDirty(false);
		}
		furnace.updateComparators();
	}

	public boolean isBurning() {
		return furnaceBurnTime > 0;
	}

	private void pullFromInventories() {
		if (pullTarget == null) {
			return;
		}
		//TODO - 1.20.4: Re-evaluate this
		BlockEntity blockEntity = WorldHelper.getBlockEntity(level, worldPosition.above());
		if (blockEntity instanceof HopperBlockEntity || blockEntity instanceof DropperBlockEntity) {
			return;
		}
		IItemHandler handler = pullTarget.getCapability();
		if (handler != null) {
			for (int i = 0, slots = handler.getSlots(); i < slots; i++) {
				ItemStack extractTest = handler.extractItem(i, Integer.MAX_VALUE, true);
				if (!extractTest.isEmpty()) {
					IItemHandler targetInv = SlotPredicates.FURNACE_FUEL.test(extractTest) ? fuelInv : inputInventory;
					transferItem(targetInv, i, extractTest, handler);
				}
			}
		}
	}

	private void pushToInventories() {
		if (outputInventory.isEmpty() || pushTarget == null) {
			return;
		}
		//TODO - 1.20.4: Re-evaluate this
		BlockEntity blockEntity = WorldHelper.getBlockEntity(level, worldPosition.below());
		if (blockEntity instanceof HopperBlockEntity) {
			return;
		}
		IItemHandler targetInv = pushTarget.getCapability();
		if (targetInv != null) {
			for (int i = 0, slots = outputInventory.getSlots(); i < slots; i++) {
				ItemStack extractTest = outputInventory.extractItem(i, Integer.MAX_VALUE, true);
				if (!extractTest.isEmpty()) {
					transferItem(targetInv, i, extractTest, outputInventory);
				}
			}
		}
	}

	private void transferItem(IItemHandler targetInv, int i, ItemStack extractTest, IItemHandler outputInventory) {
		ItemStack remainderTest = ItemHandlerHelper.insertItemStacked(targetInv, extractTest, true);
		int successfullyTransferred = extractTest.getCount() - remainderTest.getCount();
		if (successfullyTransferred > 0) {
			ItemStack toInsert = outputInventory.extractItem(i, successfullyTransferred, false);
			ItemStack result = ItemHandlerHelper.insertItemStacked(targetInv, toInsert, false);
			assert result.isEmpty();
		}
	}

	public ItemStack getSmeltingResult(ItemStack in) {
		dummyFurnace.setItem(0, in);
		Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, dummyFurnace, level);
		dummyFurnace.setItem(0, ItemStack.EMPTY);
		return recipe
				.map(RecipeHolder::value)
				.map(r -> r.getResultItem(level.registryAccess()))
				.orElse(ItemStack.EMPTY);
	}

	private void smeltItem() {
		ItemStack toSmelt = inputInventory.getStackInSlot(0);
		ItemStack smeltResult = getSmeltingResult(toSmelt).copy();
		if (toSmelt.is(Tags.Items.ORES)) {
			if (level != null && level.random.nextFloat() < getOreDoubleChance()) {
				smeltResult.grow(smeltResult.getCount());
			}
		} else if (toSmelt.is(Tags.Items.RAW_MATERIALS)) {
			if (level != null && level.random.nextFloat() < getRawOreDoubleChance()) {
				smeltResult.grow(smeltResult.getCount());
			}
		}
		ItemHandlerHelper.insertItemStacked(outputInventory, smeltResult, false);
		toSmelt.shrink(1);
		inputInventory.onContentsChanged(0);
	}

	protected boolean canSmelt() {
		ItemStack toSmelt = inputInventory.getStackInSlot(0);
		if (toSmelt.isEmpty()) {
			return false;
		}
		ItemStack smeltResult = getSmeltingResult(toSmelt);
		if (smeltResult.isEmpty()) {
			return false;
		}
		ItemStack currentSmelted = outputInventory.getStackInSlot(outputInventory.getSlots() - 1);
		if (currentSmelted.isEmpty()) {
			return true;
		} else if (!ItemHandlerHelper.canItemStacksStack(smeltResult, currentSmelted)) {
			return false;
		}
		int result = currentSmelted.getCount() + smeltResult.getCount();
		return result <= currentSmelted.getMaxStackSize();
	}

	private int getItemBurnTime(ItemStack stack) {
		return CommonHooks.getBurnTime(stack, RecipeType.SMELTING) * ticksBeforeSmelt / 200 * efficiencyBonus;
	}

	public int getBurnTimeRemainingScaled(int value) {
		//Only used on the client
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = ticksBeforeSmelt;
		}
		return furnaceBurnTime * value / currentItemBurnTime;
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		furnaceBurnTime = nbt.getInt("BurnTime");
		furnaceCookTime = nbt.getInt("CookTime");
		inputInventory.deserializeNBT(nbt.getCompound("Input"));
		outputInventory.deserializeNBT(nbt.getCompound("Output"));
		fuelInv.deserializeNBT(nbt.getCompound("Fuel"));
		currentItemBurnTime = getItemBurnTime(getFuelItem());
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("BurnTime", furnaceBurnTime);
		tag.putInt("CookTime", furnaceCookTime);
		tag.put("Input", inputInventory.serializeNBT());
		tag.put("Output", outputInventory.serializeNBT());
		tag.put("Fuel", fuelInv.serializeNBT());
	}
}