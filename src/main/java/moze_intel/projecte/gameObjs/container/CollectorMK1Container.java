package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.container.slots.collector.SlotCollectorLock;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CollectorMK1Container extends Container
{
	private final CollectorMK1Tile tile;
	private int sunLevel;


	public CollectorMK1Container(InventoryPlayer invPlayer, CollectorMK1Tile collector)
	{
		this.tile = collector;

		IItemHandler aux = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		IItemHandler main = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		//Klein Star Slot
		this.addSlotToContainer(new ValidatedSlot(aux, CollectorMK1Tile.KLEIN_SLOT, 124, 58, SlotPredicates.COLLECTOR_INV));

		int counter = main.getSlots() - 1;
		//Fuel Upgrade storage
		for (int i = 0; i <= 1; i++)
			for (int j = 0; j <= 3; j++)
				this.addSlotToContainer(new ValidatedSlot(main, counter--, 20 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));
		
		//Upgrade Result
		this.addSlotToContainer(new ValidatedSlot(aux, CollectorMK1Tile.UPGRADE_SLOT, 124, 13, SlotPredicates.COLLECTOR_INV));
		
		//Upgrade Target
		this.addSlotToContainer(new SlotCollectorLock(aux, CollectorMK1Tile.LOCK_SLOT, 153, 36));
		
		//Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		
		//Player hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
	}
	
	@Override
	public void onCraftGuiOpened(ICrafting par1ICrafting)
	{
		super.onCraftGuiOpened(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, tile.displaySunLevel);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		if (sunLevel != tile.getSunLevel())
		{
			for (ICrafting icrafting : this.crafters)
			{
				icrafting.sendProgressBarUpdate(this, 0, tile.getSunLevel());
			}
		}
		
		sunLevel = tile.getSunLevel();
	}
	
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		tile.displaySunLevel = par2;
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
		
		if (slotIndex <= 10)
		{
			if (!this.mergeItemStack(stack, 11, 46, false))
			{
				return null;
			}
		}
		else if (slotIndex >= 11 && slotIndex <= 46)
		{
			if (!FuelMapper.isStackFuel(stack) || FuelMapper.isStackMaxFuel(stack) || !this.mergeItemStack(stack, 1, 8, false))
			{
				return null;
			}
		}
		else
		{
			return null;
		}
		
		if (stack.stackSize == 0)
		{
			slot.putStack(null);
		}
		else
		{
			slot.onSlotChanged();
		}
		
		slot.onPickupFromSlot(player, stack);
		return newStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
}