package moze_intel.projecte.api.event;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on the server when a player is attempting to place an item in the condenser.
 *
 * This event is {@link Cancelable}
 *
 * This event is fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 */
@Cancelable
public class PlayerAttemptCondenserSetEvent extends Event {

	private final PlayerEntity player;
	private final ItemInfo sourceInfo;
	private final ItemInfo reducedInfo;

	public PlayerAttemptCondenserSetEvent(@Nonnull PlayerEntity entityPlayer, @Nonnull ItemInfo sourceInfo, @Nonnull ItemInfo reducedInfo) {
		player = entityPlayer;
		this.sourceInfo = sourceInfo;
		this.reducedInfo = reducedInfo;
	}

	/**
	 * @return The player who is attempting to put in the condenser slot.
	 */
	@Nonnull
	public PlayerEntity getPlayer() {
		return player;
	}

	/**
	 * @return The {@link ItemInfo} that the player is trying to condense.
	 */
	@Nonnull
	public ItemInfo getSourceInfo() {
		return sourceInfo;
	}

	/**
	 * Gets the "cleaned" {@link ItemInfo} that the player is trying to condense. This {@link ItemInfo} may have reduced NBT information.
	 *
	 * @return The "cleaned" {@link ItemInfo} that the player is trying to learn.
	 */
	@Nonnull
	public ItemInfo getReducedInfo() {
		return reducedInfo;
	}
}