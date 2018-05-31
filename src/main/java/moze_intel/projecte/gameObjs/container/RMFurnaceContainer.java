package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class RMFurnaceContainer extends Container
{
	final RMFurnaceTile tile;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;
	
	public RMFurnaceContainer(InventoryPlayer invPlayer, RMFurnaceTile tile)
	{
		this.tile = tile;
		initSlots(invPlayer);
	}

	void initSlots(InventoryPlayer invPlayer)
	{
		IItemHandler fuel = tile.getFuel();
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Fuel
		this.addSlotToContainer(new ValidatedSlot(fuel, 0, 65, 53, SlotPredicates.FURNACE_FUEL));

		//Input(0)
		this.addSlotToContainer(new ValidatedSlot(input, 0, 65, 17, SlotPredicates.SMELTABLE));

		int counter = input.getSlots() - 1;

		//Input storage
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				this.addSlotToContainer(new ValidatedSlot(input, counter--, 11 + i * 18, 8 + j * 18, SlotPredicates.SMELTABLE));

		counter = output.getSlots() - 1;

		//Output(0)
		this.addSlotToContainer(new ValidatedSlot(output, counter--, 125, 35, s -> false));

		//Output Storage
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				this.addSlotToContainer(new ValidatedSlot(output, counter--, 147 + i * 18, 8 + j * 18, s -> false));

		//Player Inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 24 + j * 18, 84 + i * 18));

		//Player HotBar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 24 + i * 18, 142));
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer player)
	{
		return (player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.rmFurnaceOff
				|| player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.rmFurnaceOn)
			&& player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
	
	@Override
	public void addListener(IContainerListener par1IContainerListener)
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

	@SideOnly(Side.CLIENT)
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
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
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
			
			if (TileEntityFurnace.isItemFuel(newStack) || newStack.getItem() instanceof IItemEmc)
			{
				if (!this.mergeItemStack(stack, 0, 1, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!FurnaceRecipes.instance().getSmeltingResult(newStack).isEmpty())
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
