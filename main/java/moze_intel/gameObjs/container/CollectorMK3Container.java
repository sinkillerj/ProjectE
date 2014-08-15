package moze_intel.gameObjs.container;

import moze_intel.gameObjs.tiles.CollectorMK3Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CollectorMK3Container extends Container
{
	private CollectorMK3Tile tile;
	private int sunLevel;
	
	public CollectorMK3Container(InventoryPlayer invPlayer, CollectorMK3Tile collector)
	{
		this.tile = collector;
		tile.openInventory();
		
		//Klein Star Slot
		this.addSlotToContainer(new Slot(tile, 0, 158, 58));
						
		//Fuel Upgrade Slot
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				this.addSlotToContainer(new Slot(tile, i * 4 + j + 1, 18 + i * 18, 8 + j * 18));
					
		//Upgrade Result
		this.addSlotToContainer(new Slot(tile, 17, 158, 13));
						
		//Upgrade Target
		this.addSlotToContainer(new Slot(tile, 18, 187, 36));
					
		//Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 30 + j * 18, 84 + i * 18));
						
		//Player hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 30 + i * 18, 142));
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, tile.displaySunLevel);
    }
	
	@Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if(sunLevel != tile.getSunLevel())
            	icrafting.sendProgressBarUpdate(this, 1, tile.getSunLevel());
        }
        
        sunLevel = tile.getSunLevel();
    }
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
		tile.displaySunLevel = par2;
    }
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		tile.closeInventory();
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
