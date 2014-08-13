package moze_intel.gameObjs.container;

import moze_intel.gameObjs.tiles.RelayMK3Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RelayMK3Container extends Container
{
	private RelayMK3Tile tile;
	private int storedEmc;
	
	public RelayMK3Container(InventoryPlayer invPlayer, RelayMK3Tile relay)
	{
		this.tile = relay;
		
		//Burn slot
		this.addSlotToContainer(new Slot(tile, 0, 104, 58));
		 
		//Inventory Buffer
		for (int i = 0; i <= 3; i++) 
			for (int j = 0; j <= 4; j++)
				this.addSlotToContainer(new Slot(tile, i * 5 + j + 1, 28 + i * 18, 18 + j * 18));

		//Klein star charge
		this.addSlotToContainer(new Slot(tile, 21, 164, 58));
		    
		//Main player inventory
		for (int i = 0; i < 3; i++) 
			for (int j = 0; j < 9; j++) 
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 26 + j * 18, 113 + i * 18));

		//Player hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 26 + i * 18, 171));
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
