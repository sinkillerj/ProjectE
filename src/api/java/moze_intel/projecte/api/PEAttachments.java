package moze_intel.projecte.api;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PEAttachments {

	public static DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> CHARGE = get("charge");

	private PEAttachments() {
	}

	private static <TYPE> DeferredHolder<AttachmentType<?>, AttachmentType<TYPE>> get(String name) {
		return DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, new ResourceLocation(ProjectEAPI.PROJECTE_MODID, name));
	}
}