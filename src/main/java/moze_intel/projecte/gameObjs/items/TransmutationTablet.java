package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TransmutationTablet extends ItemPE
{
	public TransmutationTablet(Properties props)
	{
		super(props);
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

	private static class ContainerProvider implements IInteractionObject
	{
		private final Hand hand;

		private ContainerProvider(Hand hand) {
			this.hand = hand;
		}

		@Override
		public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerIn) {
			return new TransmutationContainer(playerInventory, new TransmutationInventory(playerIn), hand);
		}

		@Nonnull
		@Override
		public String getGuiID() {
			return "projecte:transmutation_tablet";
		}

		@Nonnull
		@Override
		public ITextComponent getName() {
			return new StringTextComponent(getGuiID());
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Nullable
		@Override
		public ITextComponent getCustomName() {
			return null;
		}
	}
}
