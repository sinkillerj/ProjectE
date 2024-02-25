package moze_intel.projecte.gameObjs.registries;

import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.DiviningRod.DiviningMode;
import moze_intel.projecte.gameObjs.items.GemEternalDensity.GemMode;
import moze_intel.projecte.gameObjs.items.MercurialEye.MercurialEyeMode;
import moze_intel.projecte.gameObjs.items.PhilosophersStone.PhilosophersStoneMode;
import moze_intel.projecte.gameObjs.items.rings.Arcana.ArcanaMode;
import moze_intel.projecte.gameObjs.items.rings.SWRG.SWRGMode;
import moze_intel.projecte.gameObjs.items.rings.TimeWatch.TimeWatchMode;
import moze_intel.projecte.gameObjs.items.tools.PEKatar.KatarMode;
import moze_intel.projecte.gameObjs.items.tools.PEPickaxe.PickaxeMode;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.AttachmentTypeDeferredRegister;
import moze_intel.projecte.handlers.CommonInternalAbilities;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.impl.capability.AlchBagImpl.AlchemicalBagAttachment;
import moze_intel.projecte.impl.capability.KnowledgeImpl.KnowledgeAttachment;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PEAttachmentTypes {

	private PEAttachmentTypes() {
	}

	public static final AttachmentTypeDeferredRegister ATTACHMENT_TYPES = new AttachmentTypeDeferredRegister(PECore.MODID);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<ItemStackHandler>> EYE_INVENTORY = ATTACHMENT_TYPES.register("eye_inventory",
			() -> AttachmentType.serializable(() -> new ItemStackHandler(2))
					.copyHandler((holder, handler) -> {
						ItemStackHandler copy = new ItemStackHandler(2);
						copy.setStackInSlot(0, handler.getStackInSlot(0).copy());
						copy.setStackInSlot(1, handler.getStackInSlot(1).copy());
						return copy;
					}).comparator(AttachmentTypeDeferredRegister.HANDLER_COMPARATOR)
					.build()
	);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<AlchemicalBagAttachment>> ALCHEMICAL_BAGS = ATTACHMENT_TYPES.register("alchemical_bags",
			() -> AttachmentType.serializable(AlchemicalBagAttachment::new)
					.copyHandler((holder, attachment) -> attachment.copy(holder))
					.comparator(AlchemicalBagAttachment::isCompatible)
					.copyOnDeath()
					.build()
	);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<KnowledgeAttachment>> KNOWLEDGE = ATTACHMENT_TYPES.register("knowledge",
			() -> AttachmentType.serializable(KnowledgeAttachment::new)
					.copyHandler((holder, attachment) -> attachment.copy(holder))
					.comparator(KnowledgeAttachment::isCompatible)
					.copyOnDeath()
					.build()
	);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Byte>> COOLDOWN = ATTACHMENT_TYPES.registerByte("cooldown", (byte) 0, (byte) 0, (byte) 20);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Integer>> CHARGE = ATTACHMENT_TYPES.registerNonNegativeInt("charge", 0);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Integer>> STORED_EXP = ATTACHMENT_TYPES.registerNonNegativeInt("stored_exp", 0);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Long>> STORED_EMC = ATTACHMENT_TYPES.registerNonNegativeLong("stored_emc", 0);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Double>> UNPROCESSED_EMC = ATTACHMENT_TYPES.registerNonNegativeDouble("unprocessed_emc", 0);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> ACTIVE = ATTACHMENT_TYPES.registerBoolean("active", false);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> STEP_ASSIST = ATTACHMENT_TYPES.registerBoolean("step_assist", false);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> NIGHT_VISION = ATTACHMENT_TYPES.registerBoolean("night_vision", false);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> GEM_WHITELIST = ATTACHMENT_TYPES.registerBoolean("gem_whitelist", false);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<List<ItemStack>>> GEM_CONSUMED = ATTACHMENT_TYPES.registerItemList("gem_consumed");
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<List<ItemStack>>> GEM_TARGETS = ATTACHMENT_TYPES.registerNonDuplicateItemList("gem_targets");


	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<TimeWatchMode>> TIME_WATCH_MODE = ATTACHMENT_TYPES.register("time_watch_mode", TimeWatchMode.class);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<ArcanaMode>> ARCANA_MODE = ATTACHMENT_TYPES.register("arcana_mode", ArcanaMode.class);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<SWRGMode>> SWRG_MODE = ATTACHMENT_TYPES.register("swrg_mode", SWRGMode.class);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<MercurialEyeMode>> MERCURIAL_EYE_MODE = ATTACHMENT_TYPES.register("eye_mode", MercurialEyeMode.class);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<PhilosophersStoneMode>> PHILOSOPHERS_STONE_MODE = ATTACHMENT_TYPES.register("philosophers_mode", PhilosophersStoneMode.class);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<KatarMode>> KATAR_MODE = ATTACHMENT_TYPES.register("katar_mode", KatarMode.class);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<PickaxeMode>> PICKAXE_MODE = ATTACHMENT_TYPES.register("pickaxe_mode", PickaxeMode.class);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<GemMode>> GEM_MODE = ATTACHMENT_TYPES.register("gem_mode", GemMode.class);
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<DiviningMode>> DIVINING_ROD_MODE = ATTACHMENT_TYPES.register("divining_mode", DiviningMode.class);


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