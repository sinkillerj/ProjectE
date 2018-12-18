package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class RepairTalisman extends ItemPE implements IAlchBagItem, IAlchChestItem, IBauble, IPedestalItem
{
	public RepairTalisman()
	{
		this.setTranslationKey("repair_talisman");
		this.setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (world.isRemote || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;

		player.getCapability(InternalTimers.CAPABILITY, null).activateRepair();

		if (player.getCapability(InternalTimers.CAPABILITY, null).canRepair())
		{
			repairAllItems(player);
		}
	}

	private void repairAllItems(EntityPlayer player)
	{
		IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

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

			if (ItemHelper.isDamageable(invStack) && invStack.getItemDamage() > 0)
			{
				invStack.setItemDamage(invStack.getItemDamage() - 1);
			}
		}

		if (Loader.isModLoaded("baubles")) baubleRepair(player);
	}

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

			if (ItemHelper.isDamageable(bInvStack) && bInvStack.getItemDamage() > 0)
			{
				bInvStack.setItemDamage(bInvStack.getItemDamage() - 1);
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
		this.onUpdate(stack, player.getEntityWorld(), player, 0, false);
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
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.repairPedCooldown != -1)
		{
			TileEntity te = world.getTileEntity(pos);
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
			if (tile.getActivityCooldown() == 0)
			{
				world.getEntitiesWithinAABB(EntityPlayerMP.class, tile.getEffectBounds()).forEach(this::repairAllItems);
				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.repairPedCooldown);
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = new ArrayList<>();
		if (ProjectEConfig.pedestalCooldown.repairPedCooldown != -1)
		{
			list.add(TextFormatting.BLUE + I18n.format("pe.repairtalisman.pedestal1"));
			list.add(TextFormatting.BLUE + I18n.format("pe.repairtalisman.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.repairPedCooldown)));
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

		byte coolDown = ItemHelper.getOrCreateCompound(stack).getByte("Cooldown");

		if (coolDown > 0)
		{
			stack.getTagCompound().setByte("Cooldown", (byte) (coolDown - 1));
		}
		else
		{
			boolean hasAction = false;

			IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			for (int i = 0; i < inv.getSlots(); i++)
			{
				ItemStack invStack = inv.getStackInSlot(i);

				if (invStack.isEmpty() || invStack.getItem() instanceof RingToggle || !invStack.getItem().isRepairable())
				{
					continue;
				}

				if (ItemHelper.isDamageable(invStack) && invStack.getItemDamage() > 0)
				{
					invStack.setItemDamage(invStack.getItemDamage() - 1);

					if (!hasAction)
					{
						hasAction = true;
					}
				}
			}

			if (hasAction)
			{
				stack.getTagCompound().setByte("Cooldown", (byte) 19);
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

		byte coolDown = ItemHelper.getOrCreateCompound(stack).getByte("Cooldown");

		if (coolDown > 0)
		{
			stack.getTagCompound().setByte("Cooldown", (byte) (coolDown - 1));
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

				if (ItemHelper.isDamageable(invStack) && invStack.getItemDamage() > 0)
				{
					invStack.setItemDamage(invStack.getItemDamage() - 1);

					if (!hasAction)
					{
						hasAction = true;
					}
				}
			}

			if (hasAction)
			{
				stack.getTagCompound().setByte("Cooldown", (byte) 19);
				return true;
			}
		}
		return false;
	}
}
