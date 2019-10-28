package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.items.rings.BlackHoleBand;
import moze_intel.projecte.gameObjs.items.rings.VoidRing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class AlchemicalBag extends ItemPE {

	public final DyeColor color;

	public AlchemicalBag(Properties props, DyeColor color) {
		super(props);
		this.color = color;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		if (!world.isRemote) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(player.getHeldItem(hand), hand),
					buf -> {
						buf.writeBoolean(hand == Hand.MAIN_HAND);
						buf.writeBoolean(false);
					});
		}

		return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	public static ItemStack getFirstBagWithSuctionItem(PlayerEntity player, NonNullList<ItemStack> inventory) {
		for (ItemStack stack : inventory) {
			if (stack.isEmpty()) {
				continue;
			}

			if (stack.getItem() instanceof AlchemicalBag) {
				IItemHandler inv = player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY)
						.orElseThrow(NullPointerException::new)
						.getBag(((AlchemicalBag) stack.getItem()).color);
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack ring = inv.getStackInSlot(i);

					if (!ring.isEmpty() && (ring.getItem() instanceof BlackHoleBand || ring.getItem() instanceof VoidRing)) {
						if (ring.getOrCreateTag().getBoolean(TAG_ACTIVE)) {
							return stack;
						}
					}
				}
			}
		}

		return ItemStack.EMPTY;
	}

	private class ContainerProvider implements INamedContainerProvider {

		private final ItemStack stack;
		private final Hand hand;

		private ContainerProvider(ItemStack stack, Hand hand) {
			this.stack = stack;
			this.hand = hand;
		}

		@Nonnull
		@Override
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
			IItemHandlerModifiable inv = (IItemHandlerModifiable) playerIn.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY)
					.orElseThrow(NullPointerException::new)
					.getBag(color);
			return new AlchBagContainer(windowId, playerInventory, hand, inv, false);
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName() {
			return stack.getDisplayName();
		}
	}
}