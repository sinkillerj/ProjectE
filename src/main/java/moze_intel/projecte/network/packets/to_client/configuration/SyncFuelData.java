package moze_intel.projecte.network.packets.to_client.configuration;

import java.util.function.Consumer;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.FuelMapper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import org.jetbrains.annotations.NotNull;

public record SyncFuelData(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {

	private static final ResourceLocation ID = PECore.rl("sync_fuels");
	private static final Type TYPE = new Type(ID);

	@Override
	public void run(@NotNull Consumer<CustomPacketPayload> sender) {
		if (!listener.getConnection().isMemoryConnection()) {
			//Only bother syncing the fuel data if the player isn't local
			sender.accept(FuelMapper.getSyncPacket());
		}
		listener.finishCurrentTask(type());
	}

	@NotNull
	@Override
	public Type type() {
		return TYPE;
	}
}