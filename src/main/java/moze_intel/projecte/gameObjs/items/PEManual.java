package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.gameObjs.gui.GUIManual;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PEManual extends ItemPE
{
    public PEManual()
    {
        this.setUnlocalizedName("manual");
        this.setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            FMLCommonHandler.instance().showGuiScreen(new GUIManual());
        }
        return stack;
    }
}
