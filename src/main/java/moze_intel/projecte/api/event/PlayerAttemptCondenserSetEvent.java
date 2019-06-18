package moze_intel.projecte.api.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

/**
 * This event is fired on both the server and client when a player is attempting to place an item in the condenser.
 * This event is cancelable
 * This event is fired on MinecraftForge#EVENT_BUS
 */
@Cancelable
public class PlayerAttemptCondenserSetEvent extends Event
{
    private final PlayerEntity player;
    private final ItemStack stack;

    public PlayerAttemptCondenserSetEvent(@Nonnull PlayerEntity entityPlayer, @Nonnull ItemStack stack)
    {
        player = entityPlayer;
        this.stack = stack;
    }

    /**
     * @return The player who is attempting to put in the condenser slot.
     */
    @Nonnull
    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     * @return The stack that the player is trying to learn.
     */
    @Nonnull
    public ItemStack getStack()
    {
        return stack;
    }
}