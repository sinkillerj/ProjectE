package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.item.ITransmutationTablet;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransmutationTablet extends ItemPE implements ITransmutationTablet {

	public TransmutationTablet(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (!level.isClientSide()) {
            openContainer(player, hand, player.getInventory().getSelectedSlot());
		}
		return InteractionResult.SUCCESS;
	}

    @Override
    public void openContainer(Player player, InteractionHand hand, int selected) {
        player.openMenu(new ContainerProvider(hand), buf -> {
            buf.writeBoolean(true);
            buf.writeEnum(hand);
            buf.writeByte(player.getInventory().getSelectedSlot());
        });
    }

    @Override
    public void openContainer(Player player) {
        player.openMenu(new ContainerProvider(null), buf -> buf.writeBoolean(false));
    }

    private record ContainerProvider(@Nullable InteractionHand hand) implements MenuProvider {

		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
            if (hand == null) {
                return new TransmutationContainer(windowId, playerInventory);
            } else {
                return new TransmutationContainer(windowId, playerInventory, hand, playerInventory.getSelectedSlot());
            }
		}

		@NotNull
		@Override
		public Component getDisplayName() {
			return PELang.TRANSMUTATION_TRANSMUTE.translate();
		}
	}
}
