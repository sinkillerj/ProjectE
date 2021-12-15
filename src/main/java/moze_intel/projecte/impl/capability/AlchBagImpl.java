package moze_intel.projecte.impl.capability;

import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.capability.managing.SerializableCapabilityResolver;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_client.SyncBagDataPKT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public final class AlchBagImpl {

	public static void init() {
		CapabilityManager.INSTANCE.register(IAlchBagProvider.class, new Capability.IStorage<IAlchBagProvider>() {
			@Override
			public CompoundNBT writeNBT(Capability<IAlchBagProvider> capability, IAlchBagProvider instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<IAlchBagProvider> capability, IAlchBagProvider instance, Direction side, INBT nbt) {
				if (nbt instanceof CompoundNBT) {
					instance.deserializeNBT((CompoundNBT) nbt);
				}
			}
		}, DefaultImpl::new);
	}

	private static class DefaultImpl implements IAlchBagProvider {

		private final Map<DyeColor, IItemHandler> inventories = new EnumMap<>(DyeColor.class);

		@Nonnull
		@Override
		public IItemHandler getBag(@Nonnull DyeColor color) {
			if (!inventories.containsKey(color)) {
				inventories.put(color, new ItemStackHandler(104));
			}

			return inventories.get(color);
		}

		@Override
		public void sync(@Nullable DyeColor color, @Nonnull ServerPlayerEntity player) {
			PacketHandler.sendTo(new SyncBagDataPKT(writeNBT(color)), player);
		}

		private CompoundNBT writeNBT(DyeColor color) {
			CompoundNBT ret = new CompoundNBT();
			DyeColor[] colors = color == null ? DyeColor.values() : new DyeColor[]{color};
			for (DyeColor c : colors) {
				if (inventories.containsKey(c)) {
					INBT inv = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getStorage()
							.writeNBT(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventories.get(c), null);
					if (inv != null) {
						ret.put(c.getSerializedName(), inv);
					}
				}
			}
			return ret;
		}

		@Override
		public CompoundNBT serializeNBT() {
			return writeNBT(null);
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			for (DyeColor e : DyeColor.values()) {
				if (nbt.contains(e.getSerializedName())) {
					IItemHandler inv = new ItemStackHandler(104);
					CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getStorage()
							.readNBT(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inv, null, nbt.get(e.getSerializedName()));
					inventories.put(e, inv);
				}
			}
		}
	}

	public static class Provider extends SerializableCapabilityResolver<IAlchBagProvider> {

		public static final ResourceLocation NAME = PECore.rl("alch_bags");

		public Provider() {
			super(new DefaultImpl());
		}

		@Nonnull
		@Override
		public Capability<IAlchBagProvider> getMatchingCapability() {
			return ProjectEAPI.ALCH_BAG_CAPABILITY;
		}
	}

	private AlchBagImpl() {
	}
}