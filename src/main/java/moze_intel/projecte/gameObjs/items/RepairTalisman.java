package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.item.IAlchBagItem;
import moze_intel.projecte.api.item.IAlchChestItem;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.rings.PEToggleItem;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.integration.curios.CuriosIntegration;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RepairTalisman extends ItemPE implements IAlchBagItem, IAlchChestItem, IPedestalItem
{
	public RepairTalisman(Properties props)
	{
		super(props);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int par4, boolean par5)
	{
		if (world.isRemote || !(entity instanceof PlayerEntity))
		{
			return;
		}
		
		PlayerEntity player = (PlayerEntity) entity;
		player.getCapability(InternalTimers.CAPABILITY).ifPresent(timers -> {
			timers.activateRepair();
			if (timers.canRepair())
			{
				repairAllItems(player);
			}
		});
	}

	private void repairAllItems(PlayerEntity player)
	{
		LazyOptional<IItemHandler> inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		if (inv.isPresent())
		{
			repairInv(inv.orElseThrow(NullPointerException::new), player);
		}

		if(ModList.get().isLoaded("curios"))
		{
			IItemHandler curios = CuriosIntegration.getAll(player);
			if (curios != null)
			{
				repairInv(curios, player);
			}
		}
	}

	private void repairInv(IItemHandler inv, PlayerEntity player)
	{
		for (int i = 0; i < inv.getSlots(); i++)
		{
			ItemStack invStack = inv.getStackInSlot(i);

			if (invStack.isEmpty() || invStack.getItem() instanceof IModeChanger || !invStack.getItem().isRepairable(invStack))
			{
				continue;
			}

			if (invStack == player.getItemStackFromSlot(EquipmentSlotType.MAINHAND) && player.isSwingInProgress)
			{
				//Don't repair item that is currently used by the player.
				continue;
			}

			if (ItemHelper.isDamageable(invStack) && invStack.getDamage() > 0)
			{
				invStack.setDamage(invStack.getDamage() - 1);
			}
		}
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.repair.get() != -1)
		{
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
			if (tile.getActivityCooldown() == 0)
			{
				world.getEntitiesWithinAABB(ServerPlayerEntity.class, tile.getEffectBounds()).forEach(this::repairAllItems);
				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.repair.get());
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription()
	{
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.pedestalCooldown.repair.get() != -1)
		{
			list.add(new TranslationTextComponent("pe.repairtalisman.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.repairtalisman.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.repair.get())).applyTextStyle(TextFormatting.BLUE));
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

				if (invStack.isEmpty() || invStack.getItem() instanceof PEToggleItem || !invStack.getItem().isRepairable(invStack))
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
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull PlayerEntity player, @Nonnull ItemStack stack)
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

				if (invStack.isEmpty() || invStack.getItem() instanceof PEToggleItem || !invStack.getItem().isRepairable(invStack))
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
