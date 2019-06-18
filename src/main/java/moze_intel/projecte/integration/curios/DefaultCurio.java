package moze_intel.projecte.integration.curios;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.capability.ICurio;

public class DefaultCurio implements ICurio {
    protected final ItemStack stack;

    public DefaultCurio(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void onCurioTick(String identifier, LivingEntity living) {
        stack.inventoryTick(living.getEntityWorld(), living, 0, false);
    }
}
