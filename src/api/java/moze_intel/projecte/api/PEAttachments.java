package moze_intel.projecte.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

public class PEAttachments {

	public static DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> CHARGE = get("charge");

	private PEAttachments() {
	}

	private static <TYPE> DeferredHolder<AttachmentType<?>, AttachmentType<TYPE>> get(String name) {
		return DeferredHolder.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, new ResourceLocation(ProjectEAPI.PROJECTE_MODID, name));
	}

	/**
	 * Used to copy nbt and add attachments to it.
	 */
	@Internal
	@Nullable
	public static CompoundTag addAttachmentsToNbt(@Nullable CompoundTag nbt, @Nullable CompoundTag attachmentNbt) {
		if (nbt != null) {
			nbt = nbt.isEmpty() ? null : nbt.copy();
		}
		if (attachmentNbt != null && !attachmentNbt.isEmpty()) {
			if (nbt == null) {
				nbt = new CompoundTag();
			}
			nbt.put(AttachmentHolder.ATTACHMENTS_NBT_KEY, attachmentNbt);
		}
		return nbt;
	}
}