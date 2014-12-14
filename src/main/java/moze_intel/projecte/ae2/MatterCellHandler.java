package moze_intel.projecte.ae2;

import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.*;
import moze_intel.projecte.gameObjs.items.ItemMatterStorageCell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class MatterCellHandler implements ICellHandler {
    @Override
    public boolean isCell(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() instanceof ItemMatterStorageCell;
    }

    @Override
    public IMEInventoryHandler getCellInventory(ItemStack itemStack, ISaveProvider iSaveProvider, StorageChannel storageChannel) {
        if ( storageChannel == StorageChannel.ITEMS && itemStack != null && itemStack.getItem() instanceof ItemMatterStorageCell) {
            return new MatterCellInventoryHandler(itemStack);
        }
        return null;
    }

    @Override
    public IIcon getTopTexture_Light() {
        return null;
    }

    @Override
    public IIcon getTopTexture_Medium() {
        return null;
    }

    @Override
    public IIcon getTopTexture_Dark() {
        return null;
    }


    @Override
    public void openChestGui(EntityPlayer player, IChestOrDrive chest, ICellHandler cellHandler, IMEInventoryHandler inv, ItemStack is, StorageChannel chan) {
        //Platform.openGUI(player, (AEBaseTile) chest, chest.getUp(), GuiBridge.GUI_ME);
    }

    @Override
    public int getStatusForCell(ItemStack itemStack, IMEInventory imeInventory) {
        MatterCellInventoryHandler handler = (MatterCellInventoryHandler)imeInventory;
        if (handler.isBound()) {
            return 1; //Ok - green
        }
        return 3; //Full - red
    }

    @Override
    public double cellIdleDrain(ItemStack itemStack, IMEInventory imeInventory) {
        return 0;
    }
}
