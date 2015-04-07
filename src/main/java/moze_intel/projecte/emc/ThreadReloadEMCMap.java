package moze_intel.projecte.emc;

import net.minecraft.world.World;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.handlers.TileEntityHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.utils.PELogger;

public class ThreadReloadEMCMap extends Thread {
	private boolean serverStarting;
	private World world;

	/**
	 * Runs the EMC Remap. If serverStarting is true, world can safely be null.
	 * @param serverStarting true if this is being run when the server is starting up.
	 * @param world The world; used for checking the condensers after an EMC remap. Can be null if serverStarting is true.
	 */
	public static void runEMCRemap(boolean serverStarting, World world) {
		new ThreadReloadEMCMap(serverStarting, world).start();
	}

	private ThreadReloadEMCMap(boolean serverStarting, World world) {
		super("ProjectE Reload EMC Thread");
		this.serverStarting = serverStarting;
		this.world = world;
	}

	@Override
	public void run() {
		if (serverStarting) {
			PELogger.logInfo("Starting server-side EMC mapping.");
		} else {
			EMCMapper.clearMaps();
		}
		CustomEMCParser.readUserData();
		EMCMapper.map();
		if (serverStarting) {
			PELogger.logInfo("Registered " + EMCMapper.emc.size() + " EMC values.");
		} else {
			TileEntityHandler.checkAllCondensers(world);
			PacketHandler.sendFragmentedEmcPacketToAll();
		}
	}
}
