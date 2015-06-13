package moze_intel.projecte.gameObjs.items.rings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IAlchBagItem;
import moze_intel.projecte.api.IAlchChestItem;
import moze_intel.projecte.api.IExtraFunction;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.utils.Coordinates;
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
		Coordinates c = PlayerHelper.getBlockLookingAt(player, 64);
		if (c == null)
		{
			Vec3 vec  = ((Vec3) PlayerHelper.getLookVec(player, 32).getSecond());
			c = new Coordinates(((int) vec.xCoord), ((int) vec.yCoord), ((int) vec.zCoord));
		}

		EnderTeleportEvent event = new EnderTeleportEvent(player, c.x, c.y + 1, c.z, 5.0F);
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
	public void updateInAlchBag(ItemStack[] inv, EntityPlayer player, ItemStack stack)
	{
		super.updateInAlchBag(inv, player, stack); // Gem of Eternal Density
		((IAlchBagItem) ObjHandler.blackHole).updateInAlchBag(inv, player, stack);
	}

	@Override
	public void updateInAlchChest(AlchChestTile tile, ItemStack stack)
	{
		super.updateInAlchChest(tile, stack); // Gem of Eternal Density
		((IAlchChestItem) ObjHandler.blackHole).updateInAlchChest(tile, stack);
	}
}
