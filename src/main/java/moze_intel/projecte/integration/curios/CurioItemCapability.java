package moze_intel.projecte.integration.curios;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public record CurioItemCapability(ItemStack stack) implements ICurio {

	@Override
	public ItemStack getStack() {
		return stack;
	}

	@Override
	public void curioTick(SlotContext context) {
		if (!context.cosmetic()) {
			getStack().inventoryTick(context.entity().level(), context.entity(), context.index(), false);
		}
	}

	public static void register(RegisterCapabilitiesEvent event, Item item) {
		event.registerItem(CuriosCapability.ITEM, (stack, ctx) -> new CurioItemCapability(stack), item);
	}
}