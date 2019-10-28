package moze_intel.projecte.api.event;

import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired serverside after a players transmutation knowledge is changed
 *
 * This event is not cancelable, and has no result
 *
 * This event is fired on MinecraftForge#EVENT_BUS
 */
public class PlayerKnowledgeChangeEvent extends Event {

	private final UUID playerUUID;

	public PlayerKnowledgeChangeEvent(@Nonnull PlayerEntity entityPlayer) {
		playerUUID = entityPlayer.getUniqueID();
	}

	/**
	 * @return The player UUID whose knowledge changed. The associated player may or may not be logged in when this event fires.
	 */
	@Nonnull
	public UUID getPlayerUUID() {
		return playerUUID;
	}
}