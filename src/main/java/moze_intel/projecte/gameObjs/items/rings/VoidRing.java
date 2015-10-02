package moze_intel.projecte.gameObjs.items.rings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.item.IAlchBagItem;
import moze_intel.projecte.api.item.IAlchChestItem;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import java.util.List;

public class VoidRing extends GemEternalDensity implements IPedestalItem, IExtraFunction
{
	@SideOnly(Side.CLIENT)
	private IIcon void_off;
	@SideOnly(Side.CLIENT)
	private IIcon void_on;

	public VoidRing()
	{
		this.setUnlocalizedName("void_ring");
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld)
	{
		super.onUpdate(stack, world, entity, slot, isHeld);
		ObjHandler.blackHole.onUpdate(stack, world, entity, slot, isHeld);
		if (!stack.getTagCompound().hasKey("teleportCooldown"))
		{
			stack.getTagCompound().setByte("teleportCooldown", ((byte) 10));
		}
		stack.getTagCompound().setByte("teleportCooldown", ((byte) (stack.getTagCompound().getByte("teleportCooldown") - 1)));
	}


	@Override
	public void updateInPedestal(World world, int x, int y, int z)
	{
		((IPedestalItem) ObjHandler.blackHole).updateInPedestal(world, x, y, z);
	}

	@Override
	public List<String> getPedestalDescription()
	{
		return ((IPedestalItem) ObjHandler.blackHole).getPedestalDescription();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg)
	{
		return dmg == 0 ? void_off : void_on;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		void_off = register.registerIcon(this.getTexture("rings", "void_off"));
		void_on = register.registerIcon(this.getTexture("rings", "void_on"));
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player)
	{
		if (stack.getTagCompound().getByte("teleportCooldown") > 0 )
		{
			return;
		}

		Vec3 c = PlayerHelper.getBlockLookingAt(player, 64);
		if (c == null)
		{
			c = PlayerHelper.getLookVec(player, 32).getRight();
		}

		EnderTeleportEvent event = new EnderTeleportEvent(player, c.xCoord, c.yCoord, c.zCoord, 0);
		if (!MinecraftForge.EVENT_BUS.post(event))
		{
			if (player.isRiding())
			{
				player.mountEntity(null);
			}

			player.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
			player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);
			player.fallDistance = 0.0F;
			stack.getTagCompound().setByte("teleportCooldown", ((byte) 10));
		}
	}

	@Override
	public boolean updateInAlchBag(ItemStack[] inv, EntityPlayer player, ItemStack stack)
	{
		((IAlchBagItem) ObjHandler.blackHole).updateInAlchBag(inv, player, stack);
		return super.updateInAlchBag(inv, player, stack); // Gem of Eternal Density
	}

	@Override
	public void updateInAlchChest(World world, int x, int y, int z, ItemStack stack)
	{
		super.updateInAlchChest(world, x, y, z, stack); // Gem of Eternal Density
		((IAlchChestItem) ObjHandler.blackHole).updateInAlchChest(world, x, y, z, stack);
	}
}
