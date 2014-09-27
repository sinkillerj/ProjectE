package moze_intel.gameObjs.items;

import java.util.List;

import moze_intel.gameObjs.ObjHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Tome extends ItemBase
{
    public Tome()
    {
        this.setUnlocalizedName("tome");
        this.setCreativeTab(ObjHandler.cTab);
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemstack)
    {
    	return false; 
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        this.itemIcon = register.registerIcon(this.getTexture("tome"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4)
    {
        list.add("Unlocks all items with an EMC value in the table.");
    }
}





