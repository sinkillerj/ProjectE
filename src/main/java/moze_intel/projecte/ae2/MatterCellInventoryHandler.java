package moze_intel.projecte.ae2;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageHelper;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import moze_intel.projecte.gameObjs.items.ItemMatterStorageCell;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.Utils;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MatterCellInventoryHandler implements IMEInventoryHandler<IAEItemStack> {

    private ItemStack itemStack;
    public MatterCellInventoryHandler(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public boolean isBound() {
        return  itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey(ItemMatterStorageCell.nbtTagBound);
    }
    private String boundTo() {
        if (isBound())
            return itemStack.stackTagCompound.getString(ItemMatterStorageCell.nbtTagBound);
        return null;
    }

    private double getStoredEMC() {
        return ItemPE.getEmc(itemStack);
        //return Transmutation.getStoredEmc(boundTo());
    }
    private void setStoredEMC(double emc) {
        ItemPE.setEmc(itemStack, emc);
        //Transmutation.setStoredEmc(boundTo(), emc);
    }
    private void addEMC(double emc) {
        ItemPE.addEmc(itemStack, emc);
        //Transmutation.setStoredEmc(boundTo(),getStoredEMC() + emc);
    }
    private  void removeEMC(double emc) {
        ItemPE.removeEmc(itemStack,emc);
        //Transmutation.setStoredEmc(boundTo(),Math.max(getStoredEMC() - emc,0));
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(IAEItemStack input) {
        return canAccept(input);
    }

    @Override
    public boolean canAccept(IAEItemStack input) {
        //TODO Check if the EMC-Item is exactly the same as the input
        if (!Utils.doesItemHaveEmc(input.getItem()))
            return false;
        return isBound();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(int i) {
        return true;
    }

    @Override
    public IItemList getAvailableItems(IItemList iItemList) {
        String playerName = boundTo();
        if (playerName != null) {
            double storedEMC = getStoredEMC();
            IStorageHelper storageHelper = AEApi.instance().storage();
            List<ItemStack> knowledge = Transmutation.getKnowledge(playerName);
            for (ItemStack is: knowledge) {
                long count = (long) (storedEMC / Utils.getEmcValue(is));
                if (count > 0) {
                    IAEItemStack item = storageHelper.createItemStack(is);
                    item.setStackSize(count);
                    iItemList.add(item);
                }
            }
        }
        return iItemList;
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack input, Actionable type, BaseActionSource src) {
        if (canAccept(input)) { //=> isBound
            if (type == Actionable.SIMULATE) return null;

            String playerName = boundTo();
            if (playerName != null) { //Being extra sure
                ItemStack normalizedItemStack = Utils.getNormalizedStack(input.getItemStack());
                if (!Utils.ContainsItemStack(Transmutation.getKnowledge(playerName), normalizedItemStack)) {
                    //Player has not yet learned this.
                    Transmutation.addToKnowledge(playerName, normalizedItemStack);
                }
                if (type == Actionable.MODULATE) addEMC(input.getStackSize() * Utils.getEmcValue(input.getItemStack()));
            }
            //Consume all the Items
            return null;
        }
        return input;
    }

    @Override
    public IAEItemStack extractItems(IAEItemStack request, Actionable mode, BaseActionSource src) {
        String playerName = boundTo();
        if (playerName != null) {
            if (Transmutation.hasFullKnowledge(playerName) || (Utils.ContainsItemStack(Transmutation.getKnowledge(playerName),request.getItemStack()))) {
                double itemEMC = Utils.getEmcValue(request.getItem());
                double requestedEMC = itemEMC * request.getStackSize();
                double storedEMC = getStoredEMC();
                if (requestedEMC > storedEMC) {
                    double count = Math.floor(storedEMC/itemEMC);
                    requestedEMC = count * itemEMC ;
                    request.setStackSize((long) count);
                }
                if (mode == Actionable.MODULATE) removeEMC(requestedEMC);
                return request;
            }
        }
        return null;
    }

    @Override
    public StorageChannel getChannel() {
        return StorageChannel.ITEMS;
    }
}
