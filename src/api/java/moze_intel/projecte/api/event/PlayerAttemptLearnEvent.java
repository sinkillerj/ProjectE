package moze_intel.projecte.api.event;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on both the server and client when a player is attempting to learn a new item
 *
 * This event is cancelable
 *
 * This event is fired on MinecraftForge#EVENT_BUS
 */
@Cancelable
public class PlayerAttemptLearnEvent extends Event {

	//TODO: Update docs
	private final PlayerEntity player;
	private final ItemInfo sourceInfo;
	private final ItemInfo reducedInfo;

	public PlayerAttemptLearnEvent(@Nonnull PlayerEntity entityPlayer, @Nonnull ItemInfo sourceInfo, @Nonnull ItemInfo reducedInfo) {
		player = entityPlayer;
		this.sourceInfo = sourceInfo;
		this.reducedInfo = reducedInfo;
	}

	/**
	 * @return The player who is attempting to learn a new item.
	 */
	@Nonnull
	public PlayerEntity getPlayer() {
		return player;
	}

	/**
	 * @return The stack that the player is trying to learn.
	 *
	 * @apiNote The returned stack can be safely modified.
	 */
	@Nonnull
	public ItemInfo getSourceInfo() {
		return sourceInfo;
	}

	/**
	 * Gets the "cleaned" stack that the player is trying to learn. This stack has a size of one, no damage, and may have reduced NBT information.
	 *
	 * @return The "cleaned" stack that the player is trying to learn.
	 *
	 * @apiNote The returned stack can be safely modified.
	 */
	@Nonnull
	public ItemInfo getReducedInfo() {
		return reducedInfo;
	}
}