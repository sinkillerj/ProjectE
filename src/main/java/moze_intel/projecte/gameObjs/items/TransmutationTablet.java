package moze_intel.projecte.gameObjs.items;

import io.netty.buffer.Unpooled;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
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
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		if (!world.isRemote)
		{
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			buf.writeBoolean(hand == EnumHand.MAIN_HAND);
			NetworkHooks.openGui((EntityPlayerMP) player, new ContainerProvider(hand), buf);
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	private static class ContainerProvider implements IInteractionObject
	{
		private final EnumHand hand;

		private ContainerProvider(EnumHand hand) {
			this.hand = hand;
		}

		@Override
		public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
			return new TransmutationContainer(playerInventory, new TransmutationInventory(playerIn), hand);
		}

		@Override
		public String getGuiID() {
			return "projecte:transmutation_tablet";
		}

		@Override
		public ITextComponent getName() {
			return new TextComponentString(getGuiID());
		}

		// todo 1.13 stack name?
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
