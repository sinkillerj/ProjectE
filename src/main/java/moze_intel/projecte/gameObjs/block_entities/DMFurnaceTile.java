package moze_intel.projecte.gameObjs.block_entities;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.capability.managing.ICapabilityResolver;
import moze_intel.projecte.capability.managing.SidedItemHandlerResolver;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class DMFurnaceTile extends CapabilityTileEMC implements MenuProvider {

	private static final long EMC_CONSUMPTION = 2;
	private final CompactableStackHandler inputInventory = new CompactableStackHandler(getInvSize());
	private final CompactableStackHandler outputInventory = new CompactableStackHandler(getInvSize());
	private final ItemStackHandler fuelInv = new StackHandler(1);
	protected final int ticksBeforeSmelt;
	private final int efficiencyBonus;
	private final RecipeWrapper dummyFurnace = new RecipeWrapper(new ItemStackHandler());
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int furnaceCookTime;

	public DMFurnaceTile(BlockPos pos, BlockState state) {
		this(PEBlockEntityTypes.DARK_MATTER_FURNACE, pos, state, 10, 3);
	}

	protected DMFurnaceTile(BlockEntityTypeRegistryObject<? extends DMFurnaceTile> type, BlockPos pos, BlockState state, int ticksBeforeSmelt, int efficiencyBonus) {
		super(type, pos, state, 64);
		this.ticksBeforeSmelt = ticksBeforeSmelt;
		this.efficiencyBonus = efficiencyBonus;
		itemHandlerResolver = new DMFurnaceItemHandlerProvider();
	}

	@Override
	protected boolean canProvideEmc() {
		return false;
	}

	@Override
	protected long getEmcInsertLimit() {
		return EMC_CONSUMPTION;
	}

	protected int getInvSize() {
		return 9;
	}

	protected float getOreDoubleChance() {
		return 0.5F;
	}

	public int getCookProgressScaled(int value) {
		return furnaceCookTime * value / ticksBeforeSmelt;
	}

	@Nonnull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInv, @Nonnull Player playerIn) {
		return new DMFurnaceContainer(windowId, playerInv, this);
	}

	@Nonnull
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

	@Override
	protected void tick() {
		boolean wasBurning = isBurning();
		int lastFurnaceBurnTime = furnaceBurnTime;
		int lastFurnaceCookTime = furnaceCookTime;
		if (isBurning()) {
			--furnaceBurnTime;
		}

		if (level != null && !level.isClientSide) {
			inputInventory.compact();
			outputInventory.compact();
			pullFromInventories();
			ItemStack fuelItem = getFuelItem();
			if (canSmelt() && !fuelItem.isEmpty()) {
				fuelItem.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(emcHolder -> {
					long simulatedExtraction = emcHolder.extractEmc(fuelItem, EMC_CONSUMPTION, EmcAction.SIMULATE);
					if (simulatedExtraction == EMC_CONSUMPTION) {
						forceInsertEmc(emcHolder.extractEmc(fuelItem, simulatedExtraction, EmcAction.EXECUTE), EmcAction.EXECUTE);
					}
					markDirty(false);
				});
			}

			if (this.getStoredEmc() >= EMC_CONSUMPTION) {
				furnaceBurnTime = 1;
				forceExtractEmc(EMC_CONSUMPTION, EmcAction.EXECUTE);
			}

			if (furnaceBurnTime == 0 && canSmelt()) {
				currentItemBurnTime = furnaceBurnTime = getItemBurnTime(fuelItem);
				if (isBurning() && !fuelItem.isEmpty()) {
					ItemStack copy = fuelItem.copy();
					fuelItem.shrink(1);
					if (fuelItem.isEmpty()) {
						fuelInv.setStackInSlot(0, copy.getItem().getContainerItem(copy));
					}
					markDirty(false);
				}
			}

			if (isBurning() && canSmelt()) {
				++furnaceCookTime;
				if (furnaceCookTime == ticksBeforeSmelt) {
					furnaceCookTime = 0;
					smeltItem();
				}
			}
			if (wasBurning != isBurning()) {
				BlockState state = getBlockState();
				if (state.getBlock() instanceof MatterFurnace) {
					//Should always be true, but validate it just in case
					level.setBlockAndUpdate(worldPosition, state.setValue(MatterFurnace.LIT, isBurning()));
				}
				setChanged();
			}
			pushToInventories();
		}
		if (lastFurnaceBurnTime != furnaceBurnTime || lastFurnaceCookTime != furnaceCookTime) {
			markDirty(false);
		}
		super.tick();
	}

	public boolean isBurning() {
		return furnaceBurnTime > 0;
	}

	private void pullFromInventories() {
		BlockEntity tile = WorldHelper.getTileEntity(level, worldPosition.above());
		if (tile == null || tile instanceof HopperBlockEntity || tile instanceof DropperBlockEntity) {
			return;
		}
		IItemHandler handler = WorldHelper.getItemHandler(tile, Direction.DOWN);
		if (handler == null) {
			return;
		}
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack extractTest = handler.extractItem(i, Integer.MAX_VALUE, true);
			if (!extractTest.isEmpty()) {
				IItemHandler targetInv = AbstractFurnaceBlockEntity.isFuel(extractTest) || extractTest.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent()
										 ? fuelInv : inputInventory;
				transferItem(targetInv, i, extractTest, handler);
			}
		}
	}

	private void pushToInventories() {
		if (outputInventory.isEmpty()) {
			return;
		}
		BlockEntity tile = WorldHelper.getTileEntity(level, worldPosition.below());
		if (tile == null || tile instanceof HopperBlockEntity) {
			return;
		}
		IItemHandler targetInv = WorldHelper.getItemHandler(tile, Direction.UP);
		if (targetInv == null) {
			return;
		}
		for (int i = 0; i < outputInventory.getSlots(); i++) {
			ItemStack extractTest = outputInventory.extractItem(i, Integer.MAX_VALUE, true);
			if (!extractTest.isEmpty()) {
				transferItem(targetInv, i, extractTest, outputInventory);
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
		Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, dummyFurnace, level);
		dummyFurnace.setItem(0, ItemStack.EMPTY);
		return recipe.map(Recipe::getResultItem).orElse(ItemStack.EMPTY);
	}

	private void smeltItem() {
		ItemStack toSmelt = inputInventory.getStackInSlot(0);
		ItemStack smeltResult = getSmeltingResult(toSmelt).copy();
		if (level != null && level.random.nextFloat() < getOreDoubleChance() && ItemHelper.isOre(toSmelt.getItem())) {
			smeltResult.grow(smeltResult.getCount());
		}
		ItemHandlerHelper.insertItemStacked(outputInventory, smeltResult, false);
		toSmelt.shrink(1);
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
		}
		if (!smeltResult.sameItem(currentSmelted)) {
			return false;
		}
		int result = currentSmelted.getCount() + smeltResult.getCount();
		return result <= currentSmelted.getMaxStackSize();
	}

	private int getItemBurnTime(ItemStack stack) {
		return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) * ticksBeforeSmelt / 200 * efficiencyBonus;
	}

	public int getBurnTimeRemainingScaled(int value) {
		//Only used on the client
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = ticksBeforeSmelt;
		}
		return furnaceBurnTime * value / currentItemBurnTime;
	}

	@Override
	public void load(@Nonnull CompoundTag nbt) {
		super.load(nbt);
		furnaceBurnTime = nbt.getInt("BurnTime");
		furnaceCookTime = nbt.getInt("CookTime");
		inputInventory.deserializeNBT(nbt.getCompound("Input"));
		outputInventory.deserializeNBT(nbt.getCompound("Output"));
		fuelInv.deserializeNBT(nbt.getCompound("Fuel"));
		currentItemBurnTime = getItemBurnTime(getFuelItem());
	}

	@Override
	protected void saveAdditional(@Nonnull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("BurnTime", furnaceBurnTime);
		tag.putInt("CookTime", furnaceCookTime);
		tag.put("Input", inputInventory.serializeNBT());
		tag.put("Output", outputInventory.serializeNBT());
		tag.put("Fuel", fuelInv.serializeNBT());
	}

	private class DMFurnaceItemHandlerProvider extends SidedItemHandlerResolver {

		private final ICapabilityResolver<IItemHandler> joined;
		private final ICapabilityResolver<IItemHandler> automationInput;
		private final ICapabilityResolver<IItemHandler> automationOutput;
		private final ICapabilityResolver<IItemHandler> automationSides;

		protected DMFurnaceItemHandlerProvider() {
			NonNullLazy<IItemHandler> automationInput = NonNullLazy.of(() -> new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN) {
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
					return !getSmeltingResult(stack).isEmpty() ? super.insertItem(slot, stack, simulate) : stack;
				}
			});
			NonNullLazy<IItemHandlerModifiable> automationFuel = NonNullLazy.of(() -> new WrappedItemHandler(fuelInv, WrappedItemHandler.WriteMode.IN) {
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
					return SlotPredicates.FURNACE_FUEL.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
				}
			});
			NonNullLazy<IItemHandler> automationOutput = NonNullLazy.of(() -> new WrappedItemHandler(outputInventory, WrappedItemHandler.WriteMode.OUT));
			this.joined = BasicCapabilityResolver.getBasicItemHandlerResolver(() -> new CombinedInvWrapper((IItemHandlerModifiable) automationInput.get(),
					automationFuel.get(), (IItemHandlerModifiable) automationOutput.get()));
			this.automationInput = BasicCapabilityResolver.getBasicItemHandlerResolver(automationInput);
			this.automationOutput = BasicCapabilityResolver.getBasicItemHandlerResolver(automationOutput);
			this.automationSides = BasicCapabilityResolver.getBasicItemHandlerResolver(() -> new CombinedInvWrapper(automationFuel.get(),
					(IItemHandlerModifiable) automationOutput.get()));
		}

		@Override
		protected ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction side) {
			if (side == null) {
				return joined;
			} else if (side == Direction.UP) {
				return automationInput;
			} else if (side == Direction.DOWN) {
				return automationOutput;
			}
			return automationSides;
		}

		@Override
		public void invalidateAll() {
			joined.invalidateAll();
			automationInput.invalidateAll();
			automationOutput.invalidateAll();
			automationSides.invalidateAll();
		}
	}
}