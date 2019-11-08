package moze_intel.projecte.handlers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class InternalTimers {

	@CapabilityInject(InternalTimers.class)
	public static Capability<InternalTimers> CAPABILITY = null;
	public static final ResourceLocation NAME = new ResourceLocation(PECore.MODID, "internal_timers");

	private final Timer repair = new Timer();
	private final Timer heal = new Timer();
	private final Timer feed = new Timer();

	public void tick() {
		if (repair.shouldUpdate) {
			repair.tickCount++;
			repair.shouldUpdate = false;
		}

		if (heal.shouldUpdate) {
			heal.tickCount++;
			heal.shouldUpdate = false;
		}

		if (feed.shouldUpdate) {
			feed.tickCount++;
			feed.shouldUpdate = false;
		}
	}

	public void activateRepair() {
		repair.shouldUpdate = true;
	}

	public void activateHeal() {
		heal.shouldUpdate = true;
	}

	public void activateFeed() {
		feed.shouldUpdate = true;
	}

	public boolean canRepair() {
		if (repair.tickCount >= 19) {
			repair.tickCount = 0;
			repair.shouldUpdate = false;
			return true;
		}
		return false;
	}

	public boolean canHeal() {
		if (heal.tickCount >= 19) {
			heal.tickCount = 0;
			heal.shouldUpdate = false;
			return true;
		}
		return false;
	}

	public boolean canFeed() {
		if (feed.tickCount >= 19) {
			feed.tickCount = 0;
			feed.shouldUpdate = false;
			return true;
		}
		return false;
	}

	public static class Provider implements ICapabilityProvider {

		private final LazyOptional<InternalTimers> capInstance = LazyOptional.of(InternalTimers::new);

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
			if (capability == CAPABILITY) {
				return capInstance.cast();
			}
			return LazyOptional.empty();
		}
	}

	private static class Timer {

		public int tickCount = 0;
		public boolean shouldUpdate = false;
	}
}