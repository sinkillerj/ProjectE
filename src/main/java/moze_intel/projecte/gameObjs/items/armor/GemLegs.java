package moze_intel.projecte.gameObjs.items.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class GemLegs extends GemArmorBase
{
    public GemLegs()
    {
        super(EntityEquipmentSlot.LEGS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean unused)
    {
        tooltips.add(I18n.translateToLocal("pe.gem.legs.lorename"));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        if (world.isRemote)
        {
            if (player.isSneaking() && !player.onGround && player.motionY <= 0)
            {
                player.motionY *= 2;
            }
        }

        if (player.isSneaking())
        {
            AxisAlignedBB box = new AxisAlignedBB(player.posX - 3.5, player.posY - 3.5, player.posZ - 3.5, player.posX + 3.5, player.posY + 3.5, player.posZ + 3.5);
            WorldHelper.repelEntitiesInAABBFromPoint(world, box, player.posX, player.posY, player.posZ, true);
        }
    }
}