package moze_intel.gameObjs.container;

import moze_intel.gameObjs.tiles.CollectorMK2Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CollectorMK2Container extends Container
{
	private CollectorMK2Tile tile;
	private int storedEmc;
	private int sunLevel;
	private int kleinCharge;
	
	public CollectorMK2Container(InventoryPlayer invPlayer, CollectorMK2Tile collector)
	{
		this.tile = collector;
		
		//Klein Star Slot
		this.addSlotToContainer(new Slot(tile, 0, 140, 58));
				
		//Fuel Upgrade Slot
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				this.addSlotToContainer(new Slot(tile, i * 4 + j + 1, 18 + i * 18, 8 + j * 18));
				
		//Upgrade Result
		this.addSlotToContainer(new Slot(tile, 13, 140, 13));
				
		//Upgrade Target
		this.addSlotToContainer(new Slot(tile, 14, 169, 36));
			
		//Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 20 + j * 18, 84 + i * 18));
				
		//Player hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 20 + i * 18, 142));
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, tile.displayEmc);
        par1ICrafting.sendProgressBarUpdate(this, 1, tile.displaySunLevel);
    }
	
	@Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (storedEmc != tile.GetStoredEMC())
                icrafting.sendProgressBarUpdate(this, 0, (int) tile.GetStoredEMC());
            if(sunLevel != tile.GetSunLevel())
            	icrafting.sendProgressBarUpdate(this, 1, tile.GetSunLevel());
            if(kleinCharge != tile.GetKleinStarCharge())
            	icrafting.sendProgressBarUpdate(this, 2, tile.GetKleinStarCharge());
        }
        
        storedEmc = (int) tile.GetStoredEMC();
        sunLevel = tile.GetSunLevel();
        kleinCharge = tile.GetKleinStarCharge();
        
    }
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
            tile.displayEmc = par2;
        if (par1 == 1)
        	tile.displaySunLevel = par2;
        if (par1 == 2)
        	tile.displayKleinCharge = par2;
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
