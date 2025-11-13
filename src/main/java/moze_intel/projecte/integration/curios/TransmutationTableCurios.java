package moze_intel.projecte.integration.curios;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

public class TransmutationTableCurios {
    public static final String MOD_ID = "curios";
    public static final String TABLET_SLOT_ID = "transmutation_tablet";

    public static boolean modLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }

    public static Optional<IItemHandlerModifiable> getCuriosInventory(Player player) {
        if (modLoaded()) return Integrator.getCuriosInventory(player);
        return Optional.empty();
    }

    private static final class Integrator {
        private static Optional<IItemHandlerModifiable> getCuriosInventory(Player player) {
            Optional<ICuriosItemHandler> curiosItemHandler = CuriosApi.getCuriosInventory(player);
            if (curiosItemHandler.isEmpty())  return Optional.empty();
            Optional<ICurioStacksHandler> itemHandler = curiosItemHandler.get().getStacksHandler(TABLET_SLOT_ID);
            return itemHandler.map(ICurioStacksHandler::getStacks);
        }
    }
}
