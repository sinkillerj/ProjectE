package moze_intel.projecte.api.event;

import moze_intel.projecte.api.ItemInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired on the server when a player is attempting to learn a new item
 *
 * This event is {@link Cancelable}
 *
 * This event is fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 */
@Cancelable
public class PlayerAttemptLearnEvent extends Event {

	private final Player player;
	private final ItemInfo sourceInfo;
	private final ItemInfo reducedInfo;

	public PlayerAttemptLearnEvent(@NotNull Player player, @NotNull ItemInfo sourceInfo, @NotNull ItemInfo reducedInfo) {
		this.player = player;
		this.sourceInfo = sourceInfo;
		this.reducedInfo = reducedInfo;
	}

	/**
	 * @return The player who is attempting to learn a new item.
	 */
	@NotNull
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return The {@link ItemInfo} that the player is trying to learn.
	 */
	@NotNull
	public ItemInfo getSourceInfo() {
		return sourceInfo;
	}

	/**
	 * Gets the "cleaned" {@link ItemInfo} that the player is trying to learn. This {@link ItemInfo} may have reduced NBT information.
	 *
	 * @return The "cleaned" {@link ItemInfo} that the player is trying to learn.
	 */
	@NotNull
	public ItemInfo getReducedInfo() {
		return reducedInfo;
	}
}