package moze_intel.projecte.impl.capability;

import java.util.EnumMap;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.capability.managing.SerializableCapabilityResolver;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_client.SyncBagDataPKT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AlchBagImpl {

	public static IAlchBagProvider getDefault() {
		return new DefaultImpl();
	}

	private static class DefaultImpl implements IAlchBagProvider {

		private final Map<DyeColor, ItemStackHandler> inventories = new EnumMap<>(DyeColor.class);

		@NotNull
		@Override
		public IItemHandler getBag(@NotNull DyeColor color) {
			if (!inventories.containsKey(color)) {
				inventories.put(color, new ItemStackHandler(104));
			}
			return inventories.get(color);
		}

		@Override
		public void sync(@Nullable DyeColor color, @NotNull ServerPlayer player) {
			PacketHandler.sendTo(new SyncBagDataPKT(writeNBT(color)), player);
		}

		private CompoundTag writeNBT(DyeColor color) {
			CompoundTag ret = new CompoundTag();
			DyeColor[] colors = color == null ? DyeColor.values() : new DyeColor[]{color};
			for (DyeColor c : colors) {
				if (inventories.containsKey(c)) {
					ret.put(c.getSerializedName(), inventories.get(c).serializeNBT());
				}
			}
			return ret;
		}

		@Override
		public CompoundTag serializeNBT() {
			return writeNBT(null);
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			for (DyeColor e : DyeColor.values()) {
				if (nbt.contains(e.getSerializedName())) {
					ItemStackHandler inv = new ItemStackHandler(104);
					inv.deserializeNBT(nbt.getCompound(e.getSerializedName()));
					inventories.put(e, inv);
				}
			}
		}
	}

	public static class Provider extends SerializableCapabilityResolver<IAlchBagProvider> {

		public static final ResourceLocation NAME = PECore.rl("alch_bags");

		public Provider() {
			super(getDefault());
		}

		@NotNull
		@Override
		public Capability<IAlchBagProvider> getMatchingCapability() {
			return PECapabilities.ALCH_BAG_CAPABILITY;
		}
	}

	private AlchBagImpl() {
	}
}