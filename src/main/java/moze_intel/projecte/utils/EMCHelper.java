package moze_intel.projecte.utils;

import com.google.common.collect.Maps;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.SimpleStack;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper class for EMC.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class EMCHelper
{
	/**
	 * Consumes EMC from fuel items or Klein Stars
	 * Any extra EMC is discarded !!! To retain remainder EMC use ItemPE.consumeFuel()
	 */
	public static double consumePlayerFuel(EntityPlayer player, double minFuel)
	{
		if (player.capabilities.isCreativeMode)
		{
			return minFuel;
		}

		IInventory inv = player.inventory;
		LinkedHashMap<Integer, Integer> map = Maps.newLinkedHashMap();
		boolean metRequirement = false;
		int emcConsumed = 0;

		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);

			if (stack == null)
			{
				continue;
			}
			else if (stack.getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) stack.getItem());
				if (itemEmc.getStoredEmc(stack) >= minFuel)
				{
					itemEmc.extractEmc(stack, minFuel);
					player.inventoryContainer.detectAndSendChanges();
					return minFuel;
				}
			}
			else if (!metRequirement)
			{
				if(FuelMapper.isStackFuel(stack))
				{
					int emc = getEmcValue(stack);
					int toRemove = ((int) Math.ceil((minFuel - emcConsumed) / (float) emc));

					if (stack.stackSize >= toRemove)
					{
						map.put(i, toRemove);
						emcConsumed += emc * toRemove;
						metRequirement = true;
					}
					else
					{
						map.put(i, stack.stackSize);
						emcConsumed += emc * stack.stackSize;

						if (emcConsumed >= minFuel)
						{
							metRequirement = true;
						}
					}

				}
			}
		}

		if (metRequirement)
		{
			for (Map.Entry<Integer, Integer> entry : map.entrySet())
			{
				inv.decrStackSize(entry.getKey(), entry.getValue());
			}

			player.inventoryContainer.detectAndSendChanges();
			return emcConsumed;
		}

		return -1;
	}

	public static boolean doesBlockHaveEmc(Block block)
	{
		if (block == null)
		{
			return false;
		}

		return doesItemHaveEmc(new ItemStack(block));
	}

	public static boolean doesItemHaveEmc(ItemStack stack)
	{
		if (stack == null)
		{
			return false;
		}

		SimpleStack iStack = new SimpleStack(stack);

		if (!iStack.isValid())
		{
			return false;
		}

		if (!stack.getHasSubtypes() && stack.getMaxDamage() != 0)
		{
			iStack.damage = 0;
		}

		return EMCMapper.mapContains(iStack);
	}

	public static boolean doesItemHaveEmc(Item item)
	{
		if (item == null)
		{
			return false;
		}

		return doesItemHaveEmc(new ItemStack(item));
	}

	public static int getEmcValue(Block Block)
	{
		SimpleStack stack = new SimpleStack(new ItemStack(Block));

		if (stack.isValid() && EMCMapper.mapContains(stack))
		{
			return EMCMapper.getEmcValue(stack);
		}

		return 0;
	}

	public static int getEmcValue(Item item)
	{
		SimpleStack stack = new SimpleStack(new ItemStack(item));

		if (stack.isValid() && EMCMapper.mapContains(stack))
		{
			return EMCMapper.getEmcValue(stack);
		}

		return 0;
	}

	/**
	 * Does not consider stack size
	 */
	public static int getEmcValue(ItemStack stack)
	{
		if (stack == null)
		{
			return 0;
		}

		SimpleStack iStack = new SimpleStack(stack);

		if (!iStack.isValid())
		{
			return 0;
		}

		if (!EMCMapper.mapContains(iStack) && !stack.getHasSubtypes() && stack.getMaxDamage() != 0)
		{
			//We don't have an emc value for id:metadata, so lets check if we have a value for id:0 and apply a damage multiplier based on that emc value.
			iStack.damage = 0;

			if (EMCMapper.mapContains(iStack))
			{
				int emc = EMCMapper.getEmcValue(iStack);

				int relDamage = (stack.getMaxDamage() - stack.getItemDamage());

				if (relDamage <= 0)
				{
					//Not Impossible. Don't use durability or enchants for emc calculation if this happens.
					return emc;
				}

				long result = emc * relDamage;

				if (result <= 0)
				{
					//Congratulations, big number is big.
					return emc;
				}

				result /= stack.getMaxDamage();
				result += getEnchantEmcBonus(stack);

				result += getStoredEMCBonus(stack);

				if (result > Integer.MAX_VALUE)
				{
					return emc;
				}

				if (result <= 0)
				{
					return 1;
				}

				return (int) result;
			}
		}
		else
		{
			if (EMCMapper.mapContains(iStack))
			{
				return EMCMapper.getEmcValue(iStack) + getEnchantEmcBonus(stack) + (int)getStoredEMCBonus(stack);
			}
		}

		return 0;
	}

	public static int getEnchantEmcBonus(ItemStack stack)
	{
		int result = 0;

		Map<Integer, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

		if (!enchants.isEmpty())
		{
			for (Map.Entry<Integer, Integer> entry : enchants.entrySet())
			{
				Enchantment ench = Enchantment.enchantmentsList[entry.getKey()];

				if (ench.getWeight() == 0)
				{
					continue;
				}

				result += Constants.ENCH_EMC_BONUS / ench.getWeight() * entry.getValue();
			}
		}

		return result;
	}

	public static int getKleinStarMaxEmc(ItemStack stack)
	{
		return Constants.MAX_KLEIN_EMC[stack.getItemDamage()];
	}

	public static double getStoredEMCBonus(ItemStack stack) {
		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("StoredEMC")) {
			return stack.stackTagCompound.getDouble("StoredEMC");
		}
		return 0;
	}
}
