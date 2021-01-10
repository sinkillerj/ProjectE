package moze_intel.projecte.gameObjs.tiles;

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
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class DMFurnaceTile extends CapabilityTileEMC implements INamedContainerProvider {

	private static final long EMC_CONSUMPTION = 2;
	private final ItemStackHandler inputInventory = new StackHandler(getInvSize());
	private final ItemStackHandler outputInventory = new StackHandler(getInvSize());
	private final ItemStackHandler fuelInv = new StackHandler(1);
	protected final int ticksBeforeSmelt;
	private final int efficiencyBonus;
	private final FurnaceTileEntity dummyFurnace = new FurnaceTileEntity();
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int furnaceCookTime;
	private boolean outputEmpty = true;
	/**
	 * Used to make sure we don't have an infinite loop of mark dirty
	 */
	private boolean isCompacting;

	public DMFurnaceTile() {
		this(PETileEntityTypes.DARK_MATTER_FURNACE.get(), 10, 3);
	}

	protected DMFurnaceTile(TileEntityType<?> type, int ticksBeforeSmelt, int efficiencyBonus) {
		super(type, 64);
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
	public Container createMenu(int windowId, @Nonnull PlayerInventory playerInv, @Nonnull PlayerEntity playerIn) {
		return new DMFurnaceContainer(windowId, playerInv, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(PEBlocks.DARK_MATTER_FURNACE.getBlock().getTranslationKey());
	}

	@Override
	public void setPos(@Nonnull BlockPos pos) {
		super.setPos(pos);
		dummyFurnace.setPos(pos);
	}

	@Override
	public void setWorldAndPos(@Nonnull World world, @Nonnull BlockPos pos) {
		super.setWorldAndPos(world, pos);
		dummyFurnace.setWorldAndPos(world, pos);
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
	public void tick() {
		boolean wasBurning = isBurning();
		boolean shouldSave = false;

		if (isBurning()) {
			--furnaceBurnTime;
		}

		if (!world.isRemote) {
			pullFromInventories();
			ItemStack fuelItem = getFuelItem();
			if (canSmelt() && !fuelItem.isEmpty()) {
				fuelItem.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(emcHolder -> {
					long simulatedExtraction = emcHolder.extractEmc(fuelItem, EMC_CONSUMPTION, EmcAction.SIMULATE);
					if (simulatedExtraction == EMC_CONSUMPTION) {
						forceInsertEmc(emcHolder.extractEmc(fuelItem, simulatedExtraction, EmcAction.EXECUTE), EmcAction.EXECUTE);
					}
				});
			}

			if (this.getStoredEmc() >= EMC_CONSUMPTION) {
				furnaceBurnTime = 1;
				forceExtractEmc(EMC_CONSUMPTION, EmcAction.EXECUTE);
			}

			if (furnaceBurnTime == 0 && canSmelt()) {
				currentItemBurnTime = furnaceBurnTime = getItemBurnTime(fuelItem);
				if (isBurning()) {
					shouldSave = true;
					if (!fuelItem.isEmpty()) {
						ItemStack copy = fuelItem.copy();
						fuelItem.shrink(1);
						if (fuelItem.isEmpty()) {
							fuelInv.setStackInSlot(0, copy.getItem().getContainerItem(copy));
						}
					}
				}
			}

			if (isBurning() && canSmelt()) {
				++furnaceCookTime;
				if (furnaceCookTime == ticksBeforeSmelt) {
					furnaceCookTime = 0;
					smeltItem();
					shouldSave = true;
				}
			}
			if (wasBurning != isBurning()) {
				shouldSave = true;
				BlockState state = world.getBlockState(getPos());
				if (state.getBlock() instanceof MatterFurnace) {
					world.setBlockState(pos, state.with(MatterFurnace.LIT, isBurning()));
				}
			}
			if (shouldSave) {
				markDirty();
			}
			pushToInventories();
		}
	}

	@Override
	public void markDirty() {
		if (!isCompacting) {
			if (world != null && !world.isRemote) {
				isCompacting = true;
				ItemHelper.compactInventory(inputInventory);
				outputEmpty = ItemHelper.compactInventory(outputInventory);
				isCompacting = false;
			}
			//No need to mark it dirty if we are currently compacting as we will mark it when we are done compacting
			super.markDirty();
		}
	}

	public boolean isBurning() {
		return furnaceBurnTime > 0;
	}

	private void pullFromInventories() {
		TileEntity tile = WorldHelper.getTileEntity(world, pos.up());
		if (tile == null || tile instanceof HopperTileEntity || tile instanceof DropperTileEntity) {
			return;
		}
		IItemHandler handler = WorldHelper.getItemHandler(tile, Direction.DOWN);
		if (handler == null) {
			return;
		}
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack extractTest = handler.extractItem(i, Integer.MAX_VALUE, true);
			if (!extractTest.isEmpty()) {
				IItemHandler targetInv = extractTest.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent() || AbstractFurnaceTileEntity.isFuel(extractTest)
										 ? fuelInv : inputInventory;
				transferItem(targetInv, i, extractTest, handler);
			}
		}
	}

	private void pushToInventories() {
		if (outputEmpty) {
			return;
		}
		TileEntity tile = WorldHelper.getTileEntity(world, pos.down());
		if (tile == null || tile instanceof HopperTileEntity) {
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
		dummyFurnace.setInventorySlotContents(0, in);
		Optional<FurnaceRecipe> recipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, dummyFurnace, world);
		dummyFurnace.clear();
		return recipe.map(IRecipe::getRecipeOutput).orElse(ItemStack.EMPTY);
	}

	private void smeltItem() {
		ItemStack toSmelt = inputInventory.getStackInSlot(0);
		ItemStack smeltResult = getSmeltingResult(toSmelt).copy();
		if (world.rand.nextFloat() < getOreDoubleChance() && ItemHelper.isOre(toSmelt.getItem())) {
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
		if (!smeltResult.isItemEqual(currentSmelted)) {
			return false;
		}
		int result = currentSmelted.getCount() + smeltResult.getCount();
		return result <= currentSmelted.getMaxStackSize();
	}

	private int getItemBurnTime(ItemStack stack) {
		return ForgeHooks.getBurnTime(stack) * ticksBeforeSmelt / 200 * efficiencyBonus;
	}

	public int getBurnTimeRemainingScaled(int value) {
		//Only used on the client
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = ticksBeforeSmelt;
		}
		return furnaceBurnTime * value / currentItemBurnTime;
	}

	@Override
	public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
		super.read(state, nbt);
		furnaceBurnTime = nbt.getShort("BurnTime");
		furnaceCookTime = nbt.getShort("CookTime");
		inputInventory.deserializeNBT(nbt.getCompound("Input"));
		outputInventory.deserializeNBT(nbt.getCompound("Output"));
		fuelInv.deserializeNBT(nbt.getCompound("Fuel"));
		currentItemBurnTime = getItemBurnTime(getFuelItem());
	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.putShort("BurnTime", (short) furnaceBurnTime);
		nbt.putShort("CookTime", (short) furnaceCookTime);
		nbt.put("Input", inputInventory.serializeNBT());
		nbt.put("Output", outputInventory.serializeNBT());
		nbt.put("Fuel", fuelInv.serializeNBT());
		return nbt;
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