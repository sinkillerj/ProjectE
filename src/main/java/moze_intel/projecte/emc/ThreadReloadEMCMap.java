package moze_intel.projecte.emc;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.handlers.TileEntityHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.world.World;

public class ThreadReloadEMCMap extends Thread {
	private World world;

	/**
	 * Runs the EMC Remap.
	 * @param world The world; used for checking the condensers after an EMC remap.
	 */
	public static void runEMCRemap(World world) {
		new ThreadReloadEMCMap(world).start();
	}

	private ThreadReloadEMCMap(World world) {
		super("ProjectE Reload EMC Thread");
		this.world = world;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		EMCMapper.clearMaps();
		CustomEMCParser.readUserData();
		EMCMapper.map();
		TileEntityHandler.checkAllCondensers(world);
		PacketHandler.sendFragmentedEmcPacketToAll();
		PELogger.logInfo("Thread ran for " + (System.currentTimeMillis() - start) + " ms.");
	}
}
