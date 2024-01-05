package moze_intel.projecte.network.packets;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IPEPacket<CONTEXT extends IPayloadContext> extends CustomPacketPayload {

	void handle(CONTEXT context);

	default void handleMainThread(CONTEXT context) {
		context.workHandler().execute(() -> handle(context));
	}
}