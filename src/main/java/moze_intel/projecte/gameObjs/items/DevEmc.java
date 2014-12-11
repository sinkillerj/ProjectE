package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DevEmc extends ItemPE {
    List<SimpleStack> outputItems;
    Random r = new Random();
    public DevEmc() {
        setUnlocalizedName("dev_emc");
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        //containedStack = new ItemStack(Blocks.cobblestone);
        containedStack = new ItemStack(Items.diamond);

        outputItems = new ArrayList<SimpleStack>();
        for (Item i: new Item[] {Items.diamond, Items.emerald, Items.coal, Items.gold_ingot, Items.iron_ingot, Items.potato}) {
            ItemStack is = new ItemStack(i);
            outputItems.add(new SimpleStack(is));
        }
    }
    private ItemStack containedStack;
    @SubscribeEvent
    public void onItemPickUp(EntityItemPickupEvent evt) {
        final EntityPlayer player = evt.entityPlayer;
        final ItemStack pickedStack = evt.item.getEntityItem();

        if (pickedStack == null || player == null) return;

        ItemStack foundDevEmc = null;

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);

            if (stack != null && stack.getItem() == this) {
                //TODO get containedStack
                if (containedStack != null) {
                    boolean isMatching = pickedStack.isItemEqual(containedStack);
                    if (isMatching) {
                        foundDevEmc = stack;
                    }
                }
            } else if (stack != null && stack.isItemEqual(pickedStack)) {
                if (stack.stackSize < stack.getMaxStackSize()) {
                    int remaining = stack.getMaxStackSize() - stack.stackSize;
                    if (remaining >= pickedStack.stackSize) {
                        stack.stackSize = stack.stackSize + pickedStack.stackSize;
                        pickedStack.stackSize = 0;
                    } else {
                        if (remaining >= pickedStack.stackSize) {
                            stack.stackSize = stack.stackSize + pickedStack.stackSize;
                            pickedStack.stackSize = 0;
                        } else {
                            stack.stackSize = stack.getMaxStackSize();
                            pickedStack.stackSize -= remaining;
                        }
                    }
                }
            }
        }

        if (foundDevEmc != null) {
            ItemPE.addEmc(foundDevEmc, pickedStack.stackSize * EMCMapper.getEmcValue(new SimpleStack(pickedStack)));
            pickedStack.stackSize = 0;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        this.itemIcon = register.registerIcon(this.getTexture("mercurial_eye"));
    }


    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        double emc = ItemPE.getEmc(stack);
        int selected = r.nextInt(outputItems.size());
        double emcSelected = EMCMapper.getEmcValue(outputItems.get(selected));
        //TODO some bug here... Probably because this is called multiple times?
        if (emcSelected <= emc && emcSelected > 0) {
            if (player.inventory.addItemStackToInventory(outputItems.get(selected).toItemStack()))
                ItemPE.removeEmc(stack,emcSelected);
        }
        return stack;
    }

}
