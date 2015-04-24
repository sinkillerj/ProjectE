package moze_intel.projecte.emc;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.handlers.TileEntityHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class ThreadReloadEMCMap extends Thread {
	private ICommandSender sender;
	private IChatComponent finishedMessage;

	/**
	 * Runs the EMC Remap.
	 * @param sender The ICommandSender object that represents the one who sent the command.
	 * @param finishedMessage The chat message to be sent upon finishing remapping.
	 */
	public static void runEMCRemap(ICommandSender sender, IChatComponent finishedMessage) {
		new ThreadReloadEMCMap(sender, finishedMessage).start();
	}

	private ThreadReloadEMCMap(ICommandSender sender, IChatComponent finishedMessage) {
		super("ProjectE Reload EMC Thread");
		this.sender = sender;
		this.finishedMessage = finishedMessage;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		EMCMapper.clearMaps();
		CustomEMCParser.readUserData();
		EMCMapper.map();
		TileEntityHandler.checkAllCondensers(sender.getEntityWorld());
		PacketHandler.sendFragmentedEmcPacketToAll();
		sender.addChatMessage(finishedMessage);
		PELogger.logInfo("Thread ran for " + (System.currentTimeMillis() - start) + " ms.");
	}
}
