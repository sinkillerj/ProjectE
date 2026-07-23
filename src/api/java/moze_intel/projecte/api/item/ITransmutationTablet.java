package moze_intel.projecte.api.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public interface ITransmutationTablet {
    void openContainer(Player player, InteractionHand hand, int selected);
    void openContainer(Player player);
}
