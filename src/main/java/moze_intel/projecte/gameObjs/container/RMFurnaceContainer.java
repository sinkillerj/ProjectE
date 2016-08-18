package moze_intel.projecte.gameObjs.container;

import com.google.common.base.Predicates;
import moze_intel.projecte.api.item.IItemEmc;
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
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class RMFurnaceContainer extends Container
{
	private final RMFurnaceTile tile;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;
	
	public RMFurnaceContainer(InventoryPlayer invPlayer, RMFurnaceTile tile)
	{
		this.tile = tile;

		IItemHandler fuel = tile.getFuel();
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);

		//Fuel
		this.addSlotToContainer(new SlotItemHandler(fuel, 0, 65, 53));

		int counter = 0;

		//Input(0)
		this.addSlotToContainer(new SlotItemHandler(input, counter++, 65, 17));

		//Input storage
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				this.addSlotToContainer(new SlotItemHandler(input, counter++, 11 + i * 18, 8 + j * 18));

		counter = output.getSlots() - 1;

		//Output(0)
		this.addSlotToContainer(new SlotItemHandler(output, counter--, 125, 35));

		//Output Storage
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				this.addSlotToContainer(new SlotItemHandler(output, counter--, 147 + i * 18, 8 + j * 18));
		
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
		return player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
	
	@Override
	public void addListener(IContainerListener par1IContainerListener)
	{
		super.addListener(par1IContainerListener);
		par1IContainerListener.sendProgressBarUpdate(this, 0, tile.furnaceCookTime);
		par1IContainerListener.sendProgressBarUpdate(this, 1, tile.furnaceBurnTime);
		par1IContainerListener.sendProgressBarUpdate(this, 2, tile.currentItemBurnTime);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (IContainerListener crafter : this.listeners)
		{
			if (lastCookTime != tile.furnaceCookTime)
				crafter.sendProgressBarUpdate(this, 0, tile.furnaceCookTime);

			if (lastBurnTime != tile.furnaceBurnTime)
				crafter.sendProgressBarUpdate(this, 1, tile.furnaceBurnTime);

			if (lastItemBurnTime != tile.currentItemBurnTime)
				crafter.sendProgressBarUpdate(this, 2, tile.currentItemBurnTime);
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
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		Slot slot = this.getSlot(slotIndex);
		
		if (slot == null || !slot.getHasStack()) 
		{
			return null;
		}
		
		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();
		
		if (slotIndex <= 26)
		{
			if (!this.mergeItemStack(stack, 27, 63, false))
			{
				return null;
			}
		}
		else
		{
			
			if (TileEntityFurnace.isItemFuel(newStack) || newStack.getItem() instanceof IItemEmc)
			{
				if (!this.mergeItemStack(stack, 0, 1, false))
				{
					return null;
				}
			}
			else if (FurnaceRecipes.instance().getSmeltingResult(newStack) != null)
			{
				if (!this.mergeItemStack(stack, 1, 14, false))
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
		
		if (stack.stackSize == 0)
		{
			slot.putStack(null);
		}
		else
		{
			slot.onSlotChanged();
		}
		
		return newStack;
	}
}
