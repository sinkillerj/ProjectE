package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class TransmutationTablet extends ItemPE {

	public TransmutationTablet(Properties props) {
		super(props);
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
		if (!world.isClientSide) {
			NetworkHooks.openGui((ServerPlayer) player, new ContainerProvider(hand), buf -> {
				buf.writeBoolean(true);
				buf.writeEnum(hand);
				buf.writeByte(player.getInventory().selected);
			});
		}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	private record ContainerProvider(InteractionHand hand) implements MenuProvider {

		@Override
		public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
			return new TransmutationContainer(windowId, playerInventory, hand, playerInventory.selected);
		}

		@Nonnull
		@Override
		public Component getDisplayName() {
			return PELang.TRANSMUTATION_TRANSMUTE.translate();
		}
	}
}