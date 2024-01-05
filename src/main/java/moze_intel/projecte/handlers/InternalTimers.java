package moze_intel.projecte.handlers;

import moze_intel.projecte.config.ProjectEConfig;

public class InternalTimers {

	private final Timer repair = new Timer();
	private final Timer heal = new Timer();
	private final Timer feed = new Timer();

	public void tick() {
		repair.tick();
		heal.tick();
		feed.tick();
	}

	public void activateRepair() {
		repair.shouldUpdate = ProjectEConfig.server.cooldown.player.repair.get() != -1;
	}

	public void activateHeal() {
		heal.shouldUpdate = ProjectEConfig.server.cooldown.player.heal.get() != -1;
	}

	public void activateFeed() {
		feed.shouldUpdate = ProjectEConfig.server.cooldown.player.feed.get() != -1;
	}

	public boolean canRepair() {
		if (repair.tickCount == 0) {
			repair.tickCount = ProjectEConfig.server.cooldown.player.repair.get();
			repair.shouldUpdate = false;
			return true;
		}
		return false;
	}

	public boolean canHeal() {
		if (heal.tickCount == 0) {
			heal.tickCount = ProjectEConfig.server.cooldown.player.heal.get();
			heal.shouldUpdate = false;
			return true;
		}
		return false;
	}

	public boolean canFeed() {
		if (feed.tickCount == 0) {
			feed.tickCount = ProjectEConfig.server.cooldown.player.feed.get();
			feed.shouldUpdate = false;
			return true;
		}
		return false;
	}

	private static class Timer {

		private int tickCount = 0;
		private boolean shouldUpdate = false;

		private void tick() {
			if (shouldUpdate) {
				if (tickCount > 0) {
					//Ensure we don't go negative if we are set to go off every tick
					tickCount--;
				}
				shouldUpdate = false;
			}
		}
	}
}