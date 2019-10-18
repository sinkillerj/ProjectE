package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class RMFurnaceContainer extends Container
{
	public final RMFurnaceTile tile;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;

	public RMFurnaceContainer(ContainerType<?> type, int windowId, PlayerInventory invPlayer, RMFurnaceTile tile)
	{
		super(type, windowId);
		this.tile = tile;
		initSlots(invPlayer);
	}

	public static RMFurnaceContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buffer)
	{
		return new RMFurnaceContainer(ObjHandler.RM_FURNACE_CONTAINER, windowId, invPlayer,
				(RMFurnaceTile) GuiHandler.getTeFromBuf(buffer));
	}

	void initSlots(PlayerInventory invPlayer)
	{
		IItemHandler fuel = tile.getFuel();
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Fuel
		this.addSlot(new ValidatedSlot(fuel, 0, 65, 53, SlotPredicates.FURNACE_FUEL));

		//Input(0)
		this.addSlot(new ValidatedSlot(input, 0, 65, 17, stack -> !tile.getSmeltingResult(stack).isEmpty()));

		int counter = input.getSlots() - 1;

		//Input storage
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				this.addSlot(new ValidatedSlot(input, counter--, 11 + i * 18, 8 + j * 18, stack -> !tile.getSmeltingResult(stack).isEmpty()));

		counter = output.getSlots() - 1;

		//Output(0)
		this.addSlot(new ValidatedSlot(output, counter--, 125, 35, s -> false));

		//Output Storage
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				this.addSlot(new ValidatedSlot(output, counter--, 147 + i * 18, 8 + j * 18, s -> false));

		//Player Inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 24 + j * 18, 84 + i * 18));

		//Player HotBar
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(invPlayer, i, 24 + i * 18, 142));
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player)
	{
		return player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.rmFurnaceOff
				&& player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
	
	@Override
	public void addListener(@Nonnull IContainerListener par1IContainerListener)
	{
		super.addListener(par1IContainerListener);
		par1IContainerListener.sendWindowProperty(this, 0, tile.furnaceCookTime);
		par1IContainerListener.sendWindowProperty(this, 1, tile.furnaceBurnTime);
		par1IContainerListener.sendWindowProperty(this, 2, tile.currentItemBurnTime);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (IContainerListener crafter : this.listeners)
		{
			if (lastCookTime != tile.furnaceCookTime)
				crafter.sendWindowProperty(this, 0, tile.furnaceCookTime);

			if (lastBurnTime != tile.furnaceBurnTime)
				crafter.sendWindowProperty(this, 1, tile.furnaceBurnTime);

			if (lastItemBurnTime != tile.currentItemBurnTime)
				crafter.sendWindowProperty(this, 2, tile.currentItemBurnTime);
		}

		lastCookTime = tile.furnaceCookTime;
		lastBurnTime = tile.furnaceBurnTime;
		lastItemBurnTime = tile.currentItemBurnTime;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0)
			tile.furnaceCookTime = par2;

		if (par1 == 1)
			tile.furnaceBurnTime = par2;

		if (par1 == 2)
			tile.currentItemBurnTime = par2;
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int slotIndex)
	{
		Slot slot = this.getSlot(slotIndex);
		
		if (slot == null || !slot.getHasStack()) 
		{
			return ItemStack.EMPTY;
		}
		
		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();
		
		if (slotIndex <= 26)
		{
			if (!this.mergeItemStack(stack, 27, 63, false))
			{
				return ItemStack.EMPTY;
			}
		}
		else
		{
			
			if (AbstractFurnaceTileEntity.isFuel(newStack) || newStack.getItem() instanceof IItemEmc)
			{
				if (!this.mergeItemStack(stack, 0, 1, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!tile.getSmeltingResult(newStack).isEmpty())
			{
				if (!this.mergeItemStack(stack, 1, 14, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else
			{
				return ItemStack.EMPTY;
			}
		}
		
		if (stack.isEmpty())
		{
			slot.putStack(ItemStack.EMPTY);
		}
		else
		{
			slot.onSlotChanged();
		}
		
		return newStack;
	}
}
