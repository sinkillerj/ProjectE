package moze_intel.gameObjs.container;

import moze_intel.gameObjs.tiles.RelayMK2Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RelayMK2Container extends Container
{
	private RelayMK2Tile tile;
	private int storedEmc;
	
	public RelayMK2Container(InventoryPlayer invPlayer, RelayMK2Tile relay)
	{
		this.tile = relay;
		
		//Burn slot
        this.addSlotToContainer(new Slot(tile, 0, 84, 44));
        
        //Inventory buffer
        for (int i = 0; i <= 2; i++) 
          for (int j = 0; j <= 3; j++)
            this.addSlotToContainer(new Slot(tile, i * 4 + j + 1, 26 + i * 18, 18 + j * 18));
        
        //Klein star slot
        this.addSlotToContainer(new Slot(tile, 13, 144, 44));
        
        //Main player inventory
        for (int i = 0; i < 3; i++) 
          for (int j = 0; j < 9; j++)
            this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 16 + j * 18, 101 + i * 18));
        
        //Player hotbar
        for (int i = 0; i < 9; i++)
          this.addSlotToContainer(new Slot(invPlayer, i, 16 + i * 18, 159));
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, tile.displayEmc);
    }
	
	@Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (storedEmc != tile.displayEmc)
                icrafting.sendProgressBarUpdate(this, 0, tile.displayEmc);
        }
        
        storedEmc = tile.displayEmc;
    }
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
            tile.displayEmc = par2;
    }
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        return null;
    }

	@Override
	public boolean canInteractWith(EntityPlayer var1) 
	{
		return true;
	}
}
