package moze_intel.gameObjs.items;

import moze_intel.MozeCore;
import moze_intel.utils.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TransmutationTablet extends ItemBase
{
    public TransmutationTablet()
    {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("trans_tablet");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            player.openGui(MozeCore.MODID, Constants.PORTABLE_STONE_GUI, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }

        return stack;
    }
}
