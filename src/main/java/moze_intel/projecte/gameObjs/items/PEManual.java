package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.gameObjs.gui.GUIManual;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class PEManual extends ItemPE
{
    public PEManual()
    {
        this.setUnlocalizedName("manual");
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if (world.isRemote)
        {
            FMLCommonHandler.instance().showGuiScreen(new GUIManual());
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add(I18n.format("pe.manual.tooltip1"));
    }

}
