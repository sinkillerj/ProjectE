package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.container.BaseContainerProvider;
import moze_intel.projecte.gameObjs.items.rings.BlackHoleBand;
import moze_intel.projecte.gameObjs.items.rings.VoidRing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchemicalBag extends ItemPE
{
	public final EnumDyeColor color;
	
	public AlchemicalBag(Properties props, EnumDyeColor color)
	{
		super(props);
		this.color = color;
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		if (!world.isRemote)
		{
			NetworkHooks.openGui((EntityPlayerMP) player, new ContainerProvider(hand), buf -> buf.writeBoolean(hand == EnumHand.MAIN_HAND));
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	public static ItemStack getFirstBagWithSuctionItem(EntityPlayer player, NonNullList<ItemStack> inventory)
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
		private final EnumHand hand;

		private ContainerProvider(EnumHand hand) {
			this.hand = hand;
		}

		@Nonnull
		@Override
		public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
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
