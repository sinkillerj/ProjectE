package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;

public class RMFurnaceTile extends TileEmc implements IEmcAcceptor
{
	private static final float EMC_CONSUMPTION = 1.6f;
	private final ItemStackHandler inputInventory = new StackHandler(getInvSize());
	private final ItemStackHandler outputInventory = new StackHandler(getInvSize());
	private final ItemStackHandler fuelInv = new StackHandler(1);
	private final LazyOptional<IItemHandler> automationInput = LazyOptional.of(() -> new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN)
	{
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		{
			return !getSmeltingResult(stack).isEmpty()
					? super.insertItem(slot, stack, simulate)
					: stack;
		}
	});
	private final LazyOptional<IItemHandler> automationFuel = LazyOptional.of(() -> new WrappedItemHandler(fuelInv, WrappedItemHandler.WriteMode.IN)
	{
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		{
			return SlotPredicates.FURNACE_FUEL.test(stack)
					? super.insertItem(slot, stack, simulate)
					: stack;
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
	private final TileEntityFurnace dummyFurnace = new TileEntityFurnace();
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int furnaceCookTime;
	
	public RMFurnaceTile()
	{
		this(ObjHandler.RM_FURNACE_TILE, 3, 4);
	}

	RMFurnaceTile(TileEntityType<?> type, int ticksBeforeSmelt, int efficiencyBonus)
	{
		super(type, 64);
		this.ticksBeforeSmelt = ticksBeforeSmelt;
		this.efficiencyBonus = efficiencyBonus;
	}

	@Override
	public void setPos(BlockPos pos)
	{
		super.setPos(pos);
		dummyFurnace.setPos(pos);
	}

	@Override
	public void setWorld(World world)
	{
		super.setWorld(world);
		dummyFurnace.setWorld(world);
	}

	protected int getInvSize()
	{
		return 13;
	}

	protected float getOreDoubleChance() {
		return 1F;
	}

	public IItemHandler getFuel()
	{
		return fuelInv;
	}
	
	private ItemStack getFuelItem()
	{
		return fuelInv.getStackInSlot(0);
	}

	public IItemHandler getInput()
	{
		return inputInventory;
	}

	public IItemHandler getOutput()
	{
		return outputInventory;
	}

	@Override
	public void remove()
	{
		super.remove();
		automationInput.invalidate();
		automationOutput.invalidate();
		automationFuel.invalidate();
		automationSides.invalidate();
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, EnumFacing side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (side == null)
			{
				return joined.cast();
			}
			else
			{
				switch (side)
				{
					case UP: return automationInput.cast();
					case DOWN: return automationOutput.cast();
					default: return automationSides.cast();
				}
			}
		}

		return super.getCapability(cap, side);
	}

	// todo 1.13 modernize vanillacopy
	@Override
	public void tick()
	{
		boolean flag = furnaceBurnTime > 0;
		boolean flag1 = false;
		
		if (furnaceBurnTime > 0)
		{
			--furnaceBurnTime;
		}
		
		if (!this.getWorld().isRemote)
		{
			pullFromInventories();
			ItemHelper.compactInventory(inputInventory);

			if (canSmelt() && !getFuelItem().isEmpty() && getFuelItem().getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) getFuelItem().getItem());
				if (itemEmc.getStoredEmc(getFuelItem()) >= EMC_CONSUMPTION)
				{
					itemEmc.extractEmc(getFuelItem(), EMC_CONSUMPTION);
					this.addEMC(EMC_CONSUMPTION);
				}
			}
			
			if (this.getStoredEmc() >= EMC_CONSUMPTION)
			{
				furnaceBurnTime = 1;
				this.removeEMC(EMC_CONSUMPTION);
			}
			
			if (furnaceBurnTime == 0 && canSmelt())
			{
				currentItemBurnTime = furnaceBurnTime = getItemBurnTime(getFuelItem());
			
				if (furnaceBurnTime > 0)
				{
					flag1 = true;
					
					if (!getFuelItem().isEmpty())
					{
						ItemStack copy = getFuelItem().copy();

						getFuelItem().shrink(1);

						if (getFuelItem().isEmpty())
						{
							fuelInv.setStackInSlot(0, copy.getItem().getContainerItem(copy));
						}
					}
				}
			}
		
			if (furnaceBurnTime > 0 && canSmelt())
			{
				++furnaceCookTime;
			
				if (furnaceCookTime == ticksBeforeSmelt)
				{
					furnaceCookTime = 0;
					smeltItem();
					flag1 = true;
				}
			}

			if (flag != furnaceBurnTime > 0)
			{
				flag1 = true;
				IBlockState state = world.getBlockState(pos);
				
				if (!this.getWorld().isRemote && state.getBlock() instanceof MatterFurnace)
				{
					getWorld().setBlockState(pos, state.with(MatterFurnace.LIT, furnaceBurnTime > 0));
				}
			}

			if (flag1)
			{
				markDirty();
			}

			ItemHelper.compactInventory(outputInventory);
			pushToInventories();
		}
	}
	
	public boolean isBurning()
	{
		return furnaceBurnTime > 0;
	}

	private void pullFromInventories()
	{
		TileEntity tile = this.getWorld().getTileEntity(pos.up());
		if (tile == null || tile instanceof TileEntityHopper || tile instanceof TileEntityDropper)
			return;
		LazyOptional<IItemHandler> handlerOpt = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		IItemHandler handler;

		if (!handlerOpt.isPresent())
		{
			if (tile instanceof ISidedInventory)
			{
				handler = new SidedInvWrapper((ISidedInventory) tile, EnumFacing.DOWN);
			} else if (tile instanceof IInventory)
			{
				handler = new InvWrapper((IInventory) tile);
			} else
			{
				return;
			}
		} else
		{
			handler = handlerOpt.orElseThrow(NullPointerException::new);
		}

		for (int i = 0; i < handler.getSlots(); i++)
		{
			ItemStack extractTest = handler.extractItem(i, Integer.MAX_VALUE, true);
			if (extractTest.isEmpty())
				continue;

			IItemHandler targetInv = extractTest.getItem() instanceof IItemEmc || TileEntityFurnace.isItemFuel(extractTest)
					? fuelInv : inputInventory;

			ItemStack remainderTest = ItemHandlerHelper.insertItemStacked(targetInv, extractTest, true);
			int successfullyTransferred = extractTest.getCount() - remainderTest.getCount();

			if (successfullyTransferred > 0)
			{
				ItemStack toInsert = handler.extractItem(i, successfullyTransferred, false);
				ItemStack result = ItemHandlerHelper.insertItemStacked(targetInv, toInsert, false);
				assert result.isEmpty();
			}
		}
	}
	
	private void pushToInventories()
	{
		// todo push to others
	}

	public ItemStack getSmeltingResult(ItemStack in)
	{
		dummyFurnace.setInventorySlotContents(0, in);
		IRecipe recipe = getWorld().getRecipeManager().getRecipe(dummyFurnace, getWorld());
		dummyFurnace.clear();

		if (recipe != null)
		{
			return recipe.getRecipeOutput();
		}

		return ItemStack.EMPTY;
	}
	
	private void smeltItem()
	{
		ItemStack toSmelt = inputInventory.getStackInSlot(0);
		ItemStack smeltResult = getSmeltingResult(toSmelt).copy();

		if (world.rand.nextFloat() < getOreDoubleChance()
			&& ItemHelper.isOre(toSmelt.getItem()))
		{
			smeltResult.grow(smeltResult.getCount());
		}

		ItemHandlerHelper.insertItemStacked(outputInventory, smeltResult, false);
		
		toSmelt.shrink(1);
	}
	
	private boolean canSmelt() 
	{
		ItemStack toSmelt = inputInventory.getStackInSlot(0);
		
		if (toSmelt.isEmpty())
		{
			return false;
		}
		
		ItemStack smeltResult = getSmeltingResult(toSmelt);
		if (smeltResult.isEmpty())
		{
			return false;
		}
		
		ItemStack currentSmelted = outputInventory.getStackInSlot(outputInventory.getSlots() - 1);
		
		if (currentSmelted.isEmpty())
		{
			return true;
		}
		if (!smeltResult.isItemEqual(currentSmelted))
		{
			return false;
		}
		
		int result = currentSmelted.getCount() + smeltResult.getCount();
		return result <= currentSmelted.getMaxStackSize();
	}
	
	private int getItemBurnTime(ItemStack stack)
	{
		int burnTime = 0;

		if (!stack.isEmpty()) {
			Item item = stack.getItem();
			int ret = stack.getBurnTime();
			burnTime = ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? TileEntityFurnace.getBurnTimes().getOrDefault(item, 0) : ret);
		}

		int val = burnTime;
		return (val * ticksBeforeSmelt) / 200 * efficiencyBonus;
	}
	
	public int getCookProgressScaled(int value)
	{
		return (furnaceCookTime + (isBurning() && canSmelt() ? 1 : 0)) * value / ticksBeforeSmelt;
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getBurnTimeRemainingScaled(int value)
	{
		if (this.currentItemBurnTime == 0)
			this.currentItemBurnTime = ticksBeforeSmelt;

		return furnaceBurnTime * value / currentItemBurnTime;
	}
	
	@Override
	public void read(NBTTagCompound nbt)
	{
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
	public NBTTagCompound write(NBTTagCompound nbt)
	{
		nbt = super.write(nbt);
		nbt.putShort("BurnTime", (short) furnaceBurnTime);
		nbt.putShort("CookTime", (short) furnaceCookTime);
		nbt.put("Input", inputInventory.serializeNBT());
		nbt.put("Output", outputInventory.serializeNBT());
		nbt.put("Fuel", fuelInv.serializeNBT());
		return nbt;
	}

	@Override
	public double acceptEMC(@Nonnull EnumFacing side, double toAccept)
	{
		if (this.getStoredEmc() < EMC_CONSUMPTION)
		{
			double needed = EMC_CONSUMPTION - this.getStoredEmc();
			double accept = Math.min(needed, toAccept);
			this.addEMC(accept);
			return accept;
		}
		return 0;
	}
}
