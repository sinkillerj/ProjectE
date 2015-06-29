package moze_intel.projecte.gameObjs.items.rings;

import moze_intel.projecte.api.IAlchBagItem;
import moze_intel.projecte.api.IAlchChestItem;
import moze_intel.projecte.api.IExtraFunction;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import java.util.List;

public class VoidRing extends GemEternalDensity implements IPedestalItem, IExtraFunction
{
	public VoidRing()
	{
		this.setUnlocalizedName("void_ring");
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld)
	{
		super.onUpdate(stack, world, entity, slot, isHeld);
		ObjHandler.blackHole.onUpdate(stack, world, entity, slot, isHeld);
	}


	@Override
	public void updateInPedestal(World world, BlockPos pos)
	{
		((IPedestalItem) ObjHandler.blackHole).updateInPedestal(world, pos);
	}

	@Override
	public List<String> getPedestalDescription()
	{
		return ((IPedestalItem) ObjHandler.blackHole).getPedestalDescription();
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player)
	{
		BlockPos pos = PlayerHelper.getBlockLookingAt(player, 64);
		if (pos == null)
		{
			Vec3 vec  = ((Vec3) PlayerHelper.getLookVec(player, 32).getSecond());
			pos = new BlockPos(vec);
		}

		EnderTeleportEvent event = new EnderTeleportEvent(player, pos.getX(), pos.getY() + 1, pos.getZ(), 5.0F);
		if (!MinecraftForge.EVENT_BUS.post(event))
		{
			if (player.isRiding())
			{
				player.mountEntity(null);
			}

			player.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
			player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);
			player.fallDistance = 0.0F;
		}
	}

	@Override
	public boolean updateInAlchBag(ItemStack[] inv, EntityPlayer player, ItemStack stack)
	{
		((IAlchBagItem) ObjHandler.blackHole).updateInAlchBag(inv, player, stack);
		return super.updateInAlchBag(inv, player, stack); // Gem of Eternal Density
	}

	@Override
	public void updateInAlchChest(AlchChestTile tile, ItemStack stack)
	{
		super.updateInAlchChest(tile, stack); // Gem of Eternal Density
		((IAlchChestItem) ObjHandler.blackHole).updateInAlchChest(tile, stack);
	}
}
