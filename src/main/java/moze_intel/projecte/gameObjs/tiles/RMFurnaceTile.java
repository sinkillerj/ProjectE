package moze_intel.projecte.gameObjs.tiles;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class RMFurnaceTile extends TileEmc implements INamedContainerProvider {

	private static final long EMC_CONSUMPTION = 2;
	private final ItemStackHandler inputInventory = new StackHandler(getInvSize());
	private final ItemStackHandler outputInventory = new StackHandler(getInvSize());
	private final ItemStackHandler fuelInv = new StackHandler(1);
	private final LazyOptional<IItemHandler> automationInput = LazyOptional.of(() -> new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return !getSmeltingResult(stack).isEmpty() ? super.insertItem(slot, stack, simulate) : stack;
		}
	});
	private final LazyOptional<IItemHandler> automationFuel = LazyOptional.of(() -> new WrappedItemHandler(fuelInv, WrappedItemHandler.WriteMode.IN) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return SlotPredicates.FURNACE_FUEL.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	});
	private final LazyOptional<IItemHandler> automationOutput = LazyOptional.of(() -> new WrappedItemHandler(outputInventory, WrappedItemHandler.WriteMode.OUT));
	private final LazyOptional<IItemHandler> automationSides = LazyOptional.of(() -> {
		IItemHandlerModifiable fuel = (IItemHandlerModifiable) automationFuel.orElseThrow(NullPointerException::new);
		IItemHandlerModifiable out = (IItemHandlerModifiable) automationOutput.orElseThrow(NullPointerException::new);
		return new CombinedInvWrapper(fuel, out);
	});
	private final LazyOptional<IItemHandler> joined = LazyOptional.of(() -> {
		IItemHandlerModifiable in = (IItemHandlerModifiable) automationInput.orElseThrow(NullPointerException::new);
		IItemHandlerModifiable fuel = (IItemHandlerModifiable) automationFuel.orElseThrow(NullPointerException::new);
		IItemHandlerModifiable out = (IItemHandlerModifiable) automationOutput.orElseThrow(NullPointerException::new);
		return new CombinedInvWrapper(in, fuel, out);
	});
	protected final int ticksBeforeSmelt;
	private final int efficiencyBonus;
	private final FurnaceTileEntity dummyFurnace = new FurnaceTileEntity();
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int furnaceCookTime;

	public RMFurnaceTile() {
		this(ObjHandler.RM_FURNACE_TILE, 3, 4);
	}

	RMFurnaceTile(TileEntityType<?> type, int ticksBeforeSmelt, int efficiencyBonus) {
		super(type, 64);
		this.ticksBeforeSmelt = ticksBeforeSmelt;
		this.efficiencyBonus = efficiencyBonus;
	}

	@Override
	protected boolean canProvideEmc() {
		return false;
	}

	@Override
	protected long getEmcInsertLimit() {
		return EMC_CONSUMPTION;
	}

	@Override
	public void setPos(@Nonnull BlockPos pos) {
		super.setPos(pos);
		dummyFurnace.setPos(pos);
	}

	@Override
	public void setWorld(@Nonnull World world) {
		super.setWorld(world);
		dummyFurnace.setWorld(world);
	}

	protected int getInvSize() {
		return 13;
	}

	protected float getOreDoubleChance() {
		return 1F;
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
	public void remove() {
		super.remove();
		automationInput.invalidate();
		automationOutput.invalidate();
		automationFuel.invalidate();
		automationSides.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == null) {
				return joined.cast();
			} else {
				switch (side) {
					case UP:
						return automationInput.cast();
					case DOWN:
						return automationOutput.cast();
					default:
						return automationSides.cast();
				}
			}
		}

		return super.getCapability(cap, side);
	}

	// todo 1.13 modernize vanillacopy
	@Override
	public void tick() {
		boolean flag = furnaceBurnTime > 0;
		boolean flag1 = false;

		if (furnaceBurnTime > 0) {
			--furnaceBurnTime;
		}

		if (!this.getWorld().isRemote) {
			pullFromInventories();
			ItemHelper.compactInventory(inputInventory);
			ItemStack fuelItem = getFuelItem();
			if (canSmelt() && !fuelItem.isEmpty()) {
				LazyOptional<IItemEmcHolder> holderCapability = fuelItem.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY);
				if (holderCapability.isPresent()) {
					IItemEmcHolder emcHolder = holderCapability.orElse(null);
					if (emcHolder.getStoredEmc(fuelItem) >= EMC_CONSUMPTION) {
						emcHolder.extractEmc(fuelItem, EMC_CONSUMPTION, EmcAction.EXECUTE);
						forceInsertEmc(EMC_CONSUMPTION, EmcAction.EXECUTE);
					}
				}
			}

			if (this.getStoredEmc() >= EMC_CONSUMPTION) {
				furnaceBurnTime = 1;
				forceExtractEmc(EMC_CONSUMPTION, EmcAction.EXECUTE);
			}

			if (furnaceBurnTime == 0 && canSmelt()) {
				currentItemBurnTime = furnaceBurnTime = getItemBurnTime(fuelItem);

				if (furnaceBurnTime > 0) {
					flag1 = true;

					if (!fuelItem.isEmpty()) {
						ItemStack copy = fuelItem.copy();

						fuelItem.shrink(1);

						if (fuelItem.isEmpty()) {
							fuelInv.setStackInSlot(0, copy.getItem().getContainerItem(copy));
						}
					}
				}
			}

			if (furnaceBurnTime > 0 && canSmelt()) {
				++furnaceCookTime;

				if (furnaceCookTime == ticksBeforeSmelt) {
					furnaceCookTime = 0;
					smeltItem();
					flag1 = true;
				}
			}

			if (flag != furnaceBurnTime > 0) {
				flag1 = true;
				BlockState state = world.getBlockState(pos);

				if (!this.getWorld().isRemote && state.getBlock() instanceof MatterFurnace) {
					getWorld().setBlockState(pos, state.with(MatterFurnace.LIT, furnaceBurnTime > 0));
				}
			}

			if (flag1) {
				markDirty();
			}

			ItemHelper.compactInventory(outputInventory);
			pushToInventories();
		}
	}

	public boolean isBurning() {
		return furnaceBurnTime > 0;
	}

	private void pullFromInventories() {
		TileEntity tile = this.getWorld().getTileEntity(pos.up());
		if (tile == null || tile instanceof HopperTileEntity || tile instanceof DropperTileEntity) {
			return;
		}
		LazyOptional<IItemHandler> handlerOpt = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN);
		IItemHandler handler;

		if (!handlerOpt.isPresent()) {
			if (tile instanceof ISidedInventory) {
				handler = new SidedInvWrapper((ISidedInventory) tile, Direction.DOWN);
			} else if (tile instanceof IInventory) {
				handler = new InvWrapper((IInventory) tile);
			} else {
				return;
			}
		} else {
			handler = handlerOpt.orElseThrow(NullPointerException::new);
		}

		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack extractTest = handler.extractItem(i, Integer.MAX_VALUE, true);
			if (extractTest.isEmpty()) {
				continue;
			}

			IItemHandler targetInv = extractTest.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent() || AbstractFurnaceTileEntity.isFuel(extractTest)
									 ? fuelInv : inputInventory;

			ItemStack remainderTest = ItemHandlerHelper.insertItemStacked(targetInv, extractTest, true);
			int successfullyTransferred = extractTest.getCount() - remainderTest.getCount();

			if (successfullyTransferred > 0) {
				ItemStack toInsert = handler.extractItem(i, successfullyTransferred, false);
				ItemStack result = ItemHandlerHelper.insertItemStacked(targetInv, toInsert, false);
				assert result.isEmpty();
			}
		}
	}

	private void pushToInventories() {
		// todo push to others
	}

	public ItemStack getSmeltingResult(ItemStack in) {
		dummyFurnace.setInventorySlotContents(0, in);
		Optional<FurnaceRecipe> recipe = getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, dummyFurnace, world);
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

	private boolean canSmelt() {
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
		return (ForgeHooks.getBurnTime(stack) * ticksBeforeSmelt) / 200 * efficiencyBonus;
	}

	public int getCookProgressScaled(int value) {
		return (furnaceCookTime + (isBurning() && canSmelt() ? 1 : 0)) * value / ticksBeforeSmelt;
	}

	@OnlyIn(Dist.CLIENT)
	public int getBurnTimeRemainingScaled(int value) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = ticksBeforeSmelt;
		}

		return furnaceBurnTime * value / currentItemBurnTime;
	}

	@Override
	public void read(@Nonnull CompoundNBT nbt) {
		super.read(nbt);
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

	@Nullable
	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity player) {
		return new RMFurnaceContainer(ObjHandler.RM_FURNACE_CONTAINER, windowId, inv, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ObjHandler.rmFurnaceOff.getTranslationKey());
	}
}