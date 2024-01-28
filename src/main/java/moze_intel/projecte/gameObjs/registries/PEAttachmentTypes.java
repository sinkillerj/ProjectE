package moze_intel.projecte.gameObjs.registries;

import java.util.stream.IntStream;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import moze_intel.projecte.handlers.CommonInternalAbilities;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.impl.capability.AlchBagImpl.AlchemicalBagAttachment;
import moze_intel.projecte.impl.capability.KnowledgeImpl.KnowledgeAttachment;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PEAttachmentTypes {

	private PEAttachmentTypes() {
	}

	public static final PEDeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = new PEDeferredRegister<>(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, PECore.MODID);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<ItemStackHandler>> EYE_INVENTORY = ATTACHMENT_TYPES.register("eye_inventory",
			() -> AttachmentType.serializable(() -> new ItemStackHandler(2))
					.comparator((a, b) -> {
						int slots = a.getSlots();
						if (slots != b.getSlots()) {
							return false;
						}
						return IntStream.range(0, slots).allMatch(slot -> ItemStack.matches(a.getStackInSlot(slot), b.getStackInSlot(slot)));
					})
					.build()
	);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<AlchemicalBagAttachment>> ALCHEMICAL_BAGS = ATTACHMENT_TYPES.register("alchemical_bags",
			() -> AttachmentType.serializable(AlchemicalBagAttachment::new)
					.copyOnDeath()
					.build()
	);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<KnowledgeAttachment>> KNOWLEDGE = ATTACHMENT_TYPES.register("knowledge",
			() -> AttachmentType.serializable(KnowledgeAttachment::new)
					.copyOnDeath()
					.build()
	);

	//TODO - 1.20.4: Somehow mention not copied and also not serialized
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<CommonInternalAbilities>> COMMON_INTERNAL_ABILITIES = ATTACHMENT_TYPES.register("common_internal_abilities",
			() -> AttachmentType.builder(CommonInternalAbilities::new).build()
	);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<InternalTimers>> INTERNAL_TIMERS = ATTACHMENT_TYPES.register("internal_timers",
			() -> AttachmentType.builder(InternalTimers::new).build()
	);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<InternalAbilities>> INTERNAL_ABILITIES = ATTACHMENT_TYPES.register("internal_abilities",
			() -> AttachmentType.builder(InternalAbilities::new).build()
	);
}