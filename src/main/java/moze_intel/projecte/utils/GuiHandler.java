package moze_intel.projecte.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
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

	public static ItemStack getHeldFromBuf(PacketBuffer buf) {
		return DistExecutor.unsafeRunForDist(() -> () -> {
			Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
			return Minecraft.getInstance().player == null ? ItemStack.EMPTY :Minecraft.getInstance().player.getHeldItem(hand);
		}, () -> () -> {
			throw new RuntimeException("Shouldn't be called on server!");
		});
	}
}