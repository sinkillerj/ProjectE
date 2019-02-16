package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.item.IAlchBagItem;
import moze_intel.projecte.api.item.IAlchChestItem;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.rings.RingToggle;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

// todo 1.13 @Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class RepairTalisman extends ItemPE implements IAlchBagItem, IAlchChestItem, IPedestalItem
{
	public RepairTalisman(Properties props)
	{
		super(props);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int par4, boolean par5)
	{
		if (world.isRemote || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		player.getCapability(InternalTimers.CAPABILITY).ifPresent(timers -> {
			timers.activateRepair();
			if (timers.canRepair())
			{
				repairAllItems(player);
			}
		});
	}

	private void repairAllItems(EntityPlayer player)
	{
		IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);

		for (int i = 0; i < inv.getSlots(); i++)
		{
			ItemStack invStack = inv.getStackInSlot(i);

			if (invStack.isEmpty() || invStack.getItem() instanceof IModeChanger || !invStack.getItem().isRepairable())
			{
				continue;
			}

			if (invStack == player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) && player.isSwingInProgress)
			{
				//Don't repair item that is currently used by the player.
				continue;
			}

			if (ItemHelper.isDamageable(invStack) && invStack.getDamage() > 0)
			{
				invStack.setDamage(invStack.getDamage() - 1);
			}
		}

		// if (ModList.get().isLoaded("baubles")) baubleRepair(player);
	}
/* todo 1.13
	@Optional.Method(modid = "baubles")
	public void baubleRepair(EntityPlayer player)
	{
		IItemHandler bInv = BaublesApi.getBaublesHandler(player);

		for (int i = 0; i < bInv.getSlots(); i++)
		{
			ItemStack bInvStack = bInv.getStackInSlot(i);
			if (bInvStack.isEmpty() || bInvStack.getItem() instanceof IModeChanger || !bInvStack.getItem().isRepairable())
			{
				continue;
			}

			if (ItemHelper.isDamageable(bInvStack) && bInvStack.getDamage() > 0)
			{
				bInvStack.setDamage(bInvStack.getDamage() - 1);
			}
		}
	}

	@Override
	@Optional.Method(modid = "baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.BELT;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) 
	{
		this.inventoryTick(stack, player.getEntityWorld(), player, 0, false);
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}*/

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.repair.get() != -1)
		{
			TileEntity te = world.getTileEntity(pos);
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
			if (tile.getActivityCooldown() == 0)
			{
				world.getEntitiesWithinAABB(EntityPlayerMP.class, tile.getEffectBounds()).forEach(this::repairAllItems);
				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.repair.get());
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = new ArrayList<>();
		if (ProjectEConfig.pedestalCooldown.repair.get() != -1)
		{
			list.add(TextFormatting.BLUE + I18n.format("pe.repairtalisman.pedestal1"));
			list.add(TextFormatting.BLUE + I18n.format("pe.repairtalisman.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.repair.get())));
		}
		return list;
	}

	@Override
	public void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack)
	{
		if (world.isRemote)
		{
			return;
		}

		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof AlchChestTile))
		{
			return;
		}
		AlchChestTile tile = ((AlchChestTile) te);

        byte coolDown = stack.getOrCreateTag().getByte("Cooldown");

		if (coolDown > 0)
		{
			stack.getTag().putByte("Cooldown", (byte) (coolDown - 1));
		}
		else
		{
			boolean hasAction = false;

			IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);
			for (int i = 0; i < inv.getSlots(); i++)
			{
				ItemStack invStack = inv.getStackInSlot(i);

				if (invStack.isEmpty() || invStack.getItem() instanceof RingToggle || !invStack.getItem().isRepairable())
				{
					continue;
				}

				if (ItemHelper.isDamageable(invStack) && invStack.getDamage() > 0)
				{
					invStack.setDamage(invStack.getDamage() - 1);

					if (!hasAction)
					{
						hasAction = true;
					}
				}
			}

			if (hasAction)
			{
				stack.getTag().putByte("Cooldown", (byte) 19);
				tile.markDirty();
			}
		}
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull EntityPlayer player, @Nonnull ItemStack stack)
	{
		if (player.getEntityWorld().isRemote)
		{
			return false;
		}

        byte coolDown = stack.getOrCreateTag().getByte("Cooldown");

		if (coolDown > 0)
		{
			stack.getTag().putByte("Cooldown", (byte) (coolDown - 1));
		}
		else
		{
			boolean hasAction = false;

			for (int i = 0; i < inv.getSlots(); i++)
			{
				ItemStack invStack = inv.getStackInSlot(i);

				if (invStack.isEmpty() || invStack.getItem() instanceof RingToggle || !invStack.getItem().isRepairable())
				{
					continue;
				}

				if (ItemHelper.isDamageable(invStack) && invStack.getDamage() > 0)
				{
					invStack.setDamage(invStack.getDamage() - 1);

					if (!hasAction)
					{
						hasAction = true;
					}
				}
			}

			if (hasAction)
			{
				stack.getTag().putByte("Cooldown", (byte) 19);
				return true;
			}
		}
		return false;
	}
}
