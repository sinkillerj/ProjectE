package moze_intel.projecte.gameObjs.items;

import java.util.Optional;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.items.rings.BlackHoleBand;
import moze_intel.projecte.gameObjs.items.rings.VoidRing;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkHooks;

public class AlchemicalBag extends ItemPE {

	public final DyeColor color;

	public AlchemicalBag(Properties props, DyeColor color) {
		super(props);
		this.color = color;
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
		if (!level.isClientSide) {
			NetworkHooks.openGui((ServerPlayer) player, new ContainerProvider(player.getItemInHand(hand), hand), buf -> {
				buf.writeEnum(hand);
				buf.writeByte(player.getInventory().selected);
				buf.writeBoolean(false);
			});
		}

		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	public static ItemStack getFirstBagWithSuctionItem(Player player, NonNullList<ItemStack> inventory) {
		Optional<IAlchBagProvider> cap = Optional.empty();
		for (ItemStack stack : inventory) {
			if (!stack.isEmpty() && stack.getItem() instanceof AlchemicalBag bag) {
				if (cap.isEmpty()) {
					cap = player.getCapability(PECapabilities.ALCH_BAG_CAPABILITY).resolve();
					if (cap.isEmpty()) {
						//If the player really doesn't have the capability and it isn't just not not loaded yet, exit
						break;
					}
				}
				IItemHandler inv = cap.get().getBag(bag.color);
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack ring = inv.getStackInSlot(i);
					if (!ring.isEmpty() && (ring.getItem() instanceof BlackHoleBand || ring.getItem() instanceof VoidRing)) {
						if (ItemHelper.checkItemNBT(ring, Constants.NBT_KEY_ACTIVE)) {
							return stack;
						}
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	private class ContainerProvider implements MenuProvider {

		private final ItemStack stack;
		private final InteractionHand hand;

		private ContainerProvider(ItemStack stack, InteractionHand hand) {
			this.stack = stack;
			this.hand = hand;
		}

		@Nonnull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
			IItemHandlerModifiable inv = (IItemHandlerModifiable) player.getCapability(PECapabilities.ALCH_BAG_CAPABILITY)
					.orElseThrow(NullPointerException::new)
					.getBag(color);
			return new AlchBagContainer(windowId, playerInventory, hand, inv, playerInventory.selected, false);
		}

		@Nonnull
		@Override
		public Component getDisplayName() {
			return stack.getHoverName();
		}
	}
}