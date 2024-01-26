package moze_intel.projecte.impl.capability;

import java.util.EnumMap;
import java.util.Map;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.network.PacketUtils;
import moze_intel.projecte.network.packets.to_client.SyncBagDataPKT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AlchBagImpl implements IAlchBagProvider {

	private final Player player;

	public AlchBagImpl(Player player) {
		this.player = player;
	}

	private AlchemicalBagAttachment attachment() {
		return this.player.getData(PEAttachmentTypes.ALCHEMICAL_BAGS);
	}

	@NotNull
	@Override
	public IItemHandler getBag(@NotNull DyeColor color) {
		return attachment().getBag(color);
	}

	@Override
	public void sync(@Nullable DyeColor color, @NotNull ServerPlayer player) {
		PacketUtils.sendTo(new SyncBagDataPKT(attachment().writeNBT(color)), player);
	}

	public static class AlchemicalBagAttachment implements INBTSerializable<CompoundTag> {

		private final Map<DyeColor, ItemStackHandler> inventories = new EnumMap<>(DyeColor.class);

		@NotNull
		public IItemHandlerModifiable getBag(@NotNull DyeColor color) {
			return inventories.computeIfAbsent(color, c -> new ItemStackHandler(104));
		}

		private CompoundTag writeNBT(DyeColor color) {
			CompoundTag ret = new CompoundTag();
			DyeColor[] colors = color == null ? DyeColor.values() : new DyeColor[]{color};
			for (DyeColor c : colors) {
				ItemStackHandler handler = inventories.get(c);
				if (handler != null) {
					ret.put(c.getSerializedName(), handler.serializeNBT());
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
				if (nbt.contains(e.getSerializedName(), Tag.TAG_COMPOUND)) {
					ItemStackHandler inv = new ItemStackHandler(104);
					inv.deserializeNBT(nbt.getCompound(e.getSerializedName()));
					inventories.put(e, inv);
				}
			}
		}
	}
}