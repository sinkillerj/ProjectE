package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TransmutationTablet extends ItemPE {

	public TransmutationTablet(Properties props) {
		super(props);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		if (!world.isRemote) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(player.getHeldItem(hand), hand),
					buf -> buf.writeBoolean(hand == Hand.MAIN_HAND));
		}
		return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	private static class ContainerProvider implements INamedContainerProvider {

		private final ItemStack stack;
		private final Hand hand;

		private ContainerProvider(ItemStack stack, Hand hand) {
			this.stack = stack;
			this.hand = hand;
		}

		@Override
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
			return new TransmutationContainer(windowId, playerInventory, hand);
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName() {
			return stack.getDisplayName();
		}
	}
}