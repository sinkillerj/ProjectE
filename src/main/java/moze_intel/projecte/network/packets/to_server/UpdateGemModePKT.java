package moze_intel.projecte.network.packets.to_server;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.network.packets.IPEPacket;
import moze_intel.projecte.utils.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UpdateGemModePKT(boolean mode) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("update_gem_mode");

	public UpdateGemModePKT(FriendlyByteBuf buffer) {
		this(buffer.readBoolean());
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		context.player()
				.map(player -> {
					ItemStack stack = player.getMainHandItem();
					return stack.isEmpty() ? player.getOffhandItem() : stack;
				})
				//Note: Void Ring extends gem of eternal density, so we only need to check if it is an instance of the base class
				.filter(stack -> !stack.isEmpty() && stack.getItem() instanceof GemEternalDensity)
				.ifPresent(stack -> stack.getOrCreateTag().putBoolean(Constants.NBT_KEY_GEM_WHITELIST, mode));
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeBoolean(mode);
	}
}