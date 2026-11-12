package moze_intel.projecte.integration.curios;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
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
			//Note: We act as if curios are being held by the offhand when it comes to ticking
			getStack().inventoryTick(context.entity().level(), context.entity(), EquipmentSlot.OFFHAND);
		}
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, Identifier id) {
		if (!slotContext.cosmetic() && stack.getItem() instanceof IExposesCurioAttributes exposesCurioAttributes) {
			Multimap<Holder<Attribute>, AttributeModifier> attributes = LinkedHashMultimap.create();
			exposesCurioAttributes.addAttributes(attributes);
			return attributes;
		}
		return ICurio.super.getAttributeModifiers(slotContext, id);
	}

	public static void register(RegisterCapabilitiesEvent event, Item item) {
		event.registerItem(CuriosCapability.ITEM, (stack, ctx) -> new CurioItemCapability(stack), item);
	}
}
