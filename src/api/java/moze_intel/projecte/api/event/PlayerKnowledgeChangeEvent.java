package moze_intel.projecte.api.event;

import java.util.UUID;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired serverside after a players transmutation knowledge is changed
 * <p>
 * This event is fired on {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}
 */
public class PlayerKnowledgeChangeEvent extends Event {

	private final UUID playerUUID;

	public PlayerKnowledgeChangeEvent(@NotNull Player player) {
		this(player.getUUID());
	}

	public PlayerKnowledgeChangeEvent(@NotNull UUID playerUUID) {
		this.playerUUID = playerUUID;
	}

	/**
	 * @return The player UUID whose knowledge changed. The associated player may or may not be logged in when this event fires.
	 */
	@NotNull
	public UUID getPlayerUUID() {
		return playerUUID;
	}
}