package moze_intel.projecte.integration.curios;

import moze_intel.projecte.capability.BasicItemCapability;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CurioItemCapability extends BasicItemCapability<ICurio> implements ICurio {

	@Override
	public Capability<ICurio> getCapability() {
		return CuriosCapability.ITEM;
	}

	@Override
	public ItemStack getStack() {
		return super.getStack();
	}

	@Override
	public void curioTick(SlotContext context) {
		if (!context.cosmetic()) {
			getStack().inventoryTick(context.entity().getCommandSenderWorld(), context.entity(), context.index(), false);
		}
	}
}