package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.nbt.NBTTagCompound;

public class ClientKnowledgeSyncPKT implements IMessage, IMessageHandler<ClientKnowledgeSyncPKT, IMessage> {
	private NBTTagCompound nbt;

	public ClientKnowledgeSyncPKT() {
	}

	public ClientKnowledgeSyncPKT(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public IMessage onMessage(ClientKnowledgeSyncPKT message, MessageContext ctx) {
		Transmutation.clear();

		Transmutation.loadFromNBT(message.nbt);

		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}
}
