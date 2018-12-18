package moze_intel.projecte.gameObjs.items.rings;

import moze_intel.projecte.api.item.IAlchBagItem;
import moze_intel.projecte.api.item.IAlchChestItem;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class VoidRing extends GemEternalDensity implements IPedestalItem, IExtraFunction
{
	public VoidRing()
	{
		this.setTranslationKey("void_ring");
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld)
	{
		super.onUpdate(stack, world, entity, slot, isHeld);
		ObjHandler.blackHole.onUpdate(stack, world, entity, slot, isHeld);
		if (!ItemHelper.getOrCreateCompound(stack).hasKey("teleportCooldown"))
		{
			stack.getTagCompound().setByte("teleportCooldown", ((byte) 10));
		}
		if(stack.getTagCompound().getByte("teleportCooldown") > 0) {
			stack.getTagCompound().setByte("teleportCooldown", ((byte) (stack.getTagCompound().getByte("teleportCooldown") - 1)));
		}
	}


	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		((IPedestalItem) ObjHandler.blackHole).updateInPedestal(world, pos);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		return ((IPedestalItem) ObjHandler.blackHole).getPedestalDescription();
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, EnumHand hand)
	{
		if (ItemHelper.getOrCreateCompound(stack).getByte("teleportCooldown") > 0 )
		{
			return false;
		}

		BlockPos c = PlayerHelper.getBlockLookingAt(player, 64);
		if (c == null)
		{
			c = new BlockPos(PlayerHelper.getLookVec(player, 32).getRight());
		}

		EnderTeleportEvent event = new EnderTeleportEvent(player, c.getX(), c.getY(), c.getZ(), 0);
		if (!MinecraftForge.EVENT_BUS.post(event))
		{
			if (player.isRiding())
			{
				player.dismountRidingEntity();
			}

			player.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
			player.fallDistance = 0.0F;
			stack.getTagCompound().setByte("teleportCooldown", ((byte) 10));
			return true;
		}

		return false;
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull EntityPlayer player, @Nonnull ItemStack stack)
	{
		((IAlchBagItem) ObjHandler.blackHole).updateInAlchBag(inv, player, stack);
		return super.updateInAlchBag(inv, player, stack); // Gem of Eternal Density
	}

	@Override
	public void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack)
	{
		super.updateInAlchChest(world, pos, stack); // Gem of Eternal Density
		((IAlchChestItem) ObjHandler.blackHole).updateInAlchChest(world, pos, stack);
	}
}
