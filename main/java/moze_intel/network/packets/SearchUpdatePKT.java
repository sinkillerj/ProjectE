package moze_intel.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.MozeCore;
import moze_intel.gameObjs.container.TransmuteContainer;
import moze_intel.gameObjs.tiles.TransmuteTile;
import net.minecraft.inventory.Container;

public class SearchUpdatePKT implements IMessage, IMessageHandler<SearchUpdatePKT, IMessage> {
	private String search;

	public SearchUpdatePKT() {
	}

	public SearchUpdatePKT(String search) {
		this.search = search;
	}

	@Override
	public IMessage onMessage(SearchUpdatePKT pkt, MessageContext ctx) {
		Container cont = ctx.getServerHandler().playerEntity.openContainer;
		if (cont instanceof TransmuteContainer) {
			TransmuteTile tt = ((TransmuteContainer) cont).tile;
			if (pkt.search!=null)
				tt.filter = pkt.search.toLowerCase();
			else
				tt.filter = "";
			tt.updateOutputs();
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		search = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, search);
	}
}
