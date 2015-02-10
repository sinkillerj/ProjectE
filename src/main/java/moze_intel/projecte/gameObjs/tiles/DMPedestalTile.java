package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.IPedestalItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Vincent on 2/9/2015.
 */
public class DMPedestalTile extends TileEntity {
    public ItemStack currentItem;
    public boolean isActive = false;

    @Override
    public void updateEntity()
    {
        if (currentItem == null || (hasWorldObj() && worldObj.isRemote))
        {
            return;
        }
        else if (isActive)
        {
            Item item = currentItem.getItem();
            if (item instanceof IPedestalItem)
            {
                ((IPedestalItem) item).updateInPedestal(worldObj, xCoord, yCoord, zCoord);
            }
        }
    }

    public void toggleState() {
        if (currentItem != null)
        {
            isActive = !isActive;
        }
    }
}
