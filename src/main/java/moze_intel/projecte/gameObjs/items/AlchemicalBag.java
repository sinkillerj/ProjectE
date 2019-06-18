package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.container.BaseContainerProvider;
import moze_intel.projecte.gameObjs.items.rings.BlackHoleBand;
import moze_intel.projecte.gameObjs.items.rings.VoidRing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchemicalBag extends ItemPE
{
	public final DyeColor color;
	
	public AlchemicalBag(Properties props, DyeColor color)
	{
		super(props);
		this.color = color;
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand)
	{
		if (!world.isRemote)
		{
			NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(hand), buf -> buf.writeBoolean(hand == Hand.MAIN_HAND));
		}
		
		return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	public static ItemStack getFirstBagWithSuctionItem(PlayerEntity player, NonNullList<ItemStack> inventory)
	{
		for (ItemStack stack : inventory)
		{
			if (stack.isEmpty())
			{
				continue;
			}

			if (stack.getItem() instanceof AlchemicalBag)
			{
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

	private class ContainerProvider extends BaseContainerProvider
	{
		private final Hand hand;

		private ContainerProvider(Hand hand) {
			this.hand = hand;
		}

		@Nonnull
		@Override
		public Container createContainer(@Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
			IItemHandlerModifiable inv = (IItemHandlerModifiable) playerIn.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY)
					.orElseThrow(NullPointerException::new)
					.getBag(color);
			return new AlchBagContainer(playerInventory, hand, inv);
		}

		@Nonnull
		@Override
		public String getGuiID() {
			return "projecte:alch_bag";
		}
	}
}
