package moze_intel.projecte.gameObjs.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import moze_intel.projecte.impl.capability.AlchBagImpl.AlchemicalBagAttachment;
import moze_intel.projecte.impl.capability.KnowledgeImpl.KnowledgeAttachment;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PEAttachmentTypes {

	private PEAttachmentTypes() {
	}

	public static final PEDeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = new PEDeferredRegister<>(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, PECore.MODID);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<AlchemicalBagAttachment>> ALCHEMICAL_BAGS = ATTACHMENT_TYPES.register("alchemical_bags",
			() -> AttachmentType.builder(AlchemicalBagAttachment::new)
					.serialize(AlchemicalBagAttachment.MAP_CODEC)
					.copyHandler(AlchemicalBagAttachment::copy)
					.copyOnDeath()
					.build()
	);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<KnowledgeAttachment>> KNOWLEDGE = ATTACHMENT_TYPES.register("knowledge",
			() -> AttachmentType.builder(KnowledgeAttachment::new)
					.serialize(KnowledgeAttachment.MAP_CODEC)
					.copyHandler(KnowledgeAttachment::copy)
					.copyOnDeath()
					.build()
	);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> GEM_ARMOR_STATE = ATTACHMENT_TYPES.register("gem_armor_state",
			() -> AttachmentType.builder(holder -> false)
					.serialize(Codec.BOOL.fieldOf("enabled"))
					.copyHandler((attachment, holder, provider) -> attachment ? true : null)
					.copyOnDeath()
					.build()
	);

	public static <HANDLER extends PEItemStacksHandler> HANDLER copyHandler(ResourceHandler<ItemResource> handler, Int2ObjectFunction<HANDLER> handlerCreator) {
		int slots = handler.size();
		HANDLER handlerCopy = handlerCreator.get(slots);
		for (int i = 0; i < slots; i++) {
			ItemStack stack = PEItemStacksHandler.getStack(handler, i);
			if (!stack.isEmpty()) {
				handlerCopy.setStackInSlot(i, stack.copy());
			}
		}
		return handlerCopy;
	}
}
