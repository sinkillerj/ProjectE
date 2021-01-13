package moze_intel.projecte.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.DistExecutor;

public class GuiHandler {

	public static TileEntity getTeFromBuf(PacketBuffer buf) {
		return DistExecutor.unsafeRunForDist(() -> () -> {
			BlockPos pos = buf.readBlockPos();
			return WorldHelper.getTileEntity(Minecraft.getInstance().world, pos);
		}, () -> () -> {
			throw new RuntimeException("Shouldn't be called on server!");
		});
	}
}