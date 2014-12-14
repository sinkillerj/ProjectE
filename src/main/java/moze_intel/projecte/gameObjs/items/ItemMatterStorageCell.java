package moze_intel.projecte.gameObjs.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.EnumSet;


public class ItemMatterStorageCell extends ItemPE {
    public static final String nbtTagBound = "EMCBoundTo";
    public ItemMatterStorageCell()  {
        setUnlocalizedName("ae2_matter_storage_cell");
        setMaxStackSize( 1 );
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            if (itemStack.stackTagCompound == null)
            {
                itemStack.stackTagCompound = new NBTTagCompound();
            }
            itemStack.stackTagCompound.setString(nbtTagBound,player.getCommandSenderName());
        }
        return itemStack;
    }
}
