package moze_intel.projecte.utils;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.items.KleinStar;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.math.BigInteger;
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
		if (player.abilities.isCreativeMode)
		{
			return minFuel;
		}

		IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP).orElseThrow(NullPointerException::new);
		Map<Integer, Integer> map = new LinkedHashMap<>();
		boolean metRequirement = false;
		int emcConsumed = 0;

		ItemStack offhand = player.getHeldItemOffhand();

		if (!offhand.isEmpty() && offhand.getItem() instanceof IItemEmc)
		{
			IItemEmc itemEmc = ((IItemEmc) offhand.getItem());
			if (itemEmc.getStoredEmc(offhand) >= minFuel)
			{
				itemEmc.extractEmc(offhand, minFuel);
				player.inventoryContainer.detectAndSendChanges();
				return minFuel;
			}
		}

		for (int i = 0; i < inv.getSlots(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);

			if (stack.isEmpty())
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
					long emc = getEmcValue(stack);
					int toRemove = ((int) Math.ceil((minFuel - emcConsumed) / (double) emc));

					if (stack.getCount() >= toRemove)
					{
						map.put(i, toRemove);
						emcConsumed += emc * toRemove;
						metRequirement = true;
					}
					else
					{
						map.put(i, stack.getCount());
						emcConsumed += emc * stack.getCount();

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
				inv.extractItem(entry.getKey(), entry.getValue(), false);
			}

			player.inventoryContainer.detectAndSendChanges();
			return emcConsumed;
		}

		return -1;
	}

	public static boolean doesItemHaveEmc(ItemStack stack)
	{
		return !stack.isEmpty() && doesItemHaveEmc(stack.getItem());
	}

	public static boolean doesItemHaveEmc(IItemProvider item)
	{
		return item != null && EMCMapper.emc.containsKey(item.asItem());
	}

	public static long getEmcValue(IItemProvider item)
	{
		if (EMCMapper.emc.containsKey(item.asItem()))
		{
			return EMCMapper.getEmcValue(item);
		}

		return 0;
	}

	/**
	 * Does not consider stack size
	 */
	public static long getEmcValue(ItemStack stack)
	{
		if (stack.isEmpty() || !EMCMapper.emc.containsKey(stack.getItem()))
		{
			return 0;
		}

		if (ItemHelper.isDamageable(stack))
		{
			long emc = EMCMapper.getEmcValue(stack.getItem());

			// maxDmg + 1 because vanilla lets you use the tool one more time
			// when item damage == max damage (shows as Durability: 0 / max)
			int relDamage = (stack.getMaxDamage() + 1 - stack.getDamage());

			if (relDamage <= 0)
			{
				// This may happen when mods overflow their max damage or item damage.
				// Don't use durability or enchants for emc calculation if this happens.
				return emc;
			}

			long result = emc * relDamage;

			if (result <= 0)
			{
				//Congratulations, big number is big.
				return emc;
			}

			result /= stack.getMaxDamage();
			boolean positive = result > 0;
			result += getEnchantEmcBonus(stack);

			//If it was positive and then became negative that means it overflowed
			if (positive && result < 0) {
				return emc;
			}

			positive = result > 0;
			result += getStoredEMCBonus(stack);

			//If it was positive and then became negative that means it overflowed
			if (positive && result < 0) {
				return emc;
			}

			if (result <= 0)
			{
				return 1;
			}

			return result;
		}
		else
		{
			return EMCMapper.getEmcValue(stack.getItem()) + getEnchantEmcBonus(stack) + (long)getStoredEMCBonus(stack);
		}
	}

	private static long getEnchantEmcBonus(ItemStack stack)
	{
		long result = 0;

		Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

		if (!enchants.isEmpty())
		{
			for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet())
			{
				Enchantment ench = entry.getKey();
				if (ench == null || ench.getRarity().getWeight() == 0)
				{
					continue;
				}

				result += Constants.ENCH_EMC_BONUS / ench.getRarity().getWeight() * entry.getValue();
			}
		}

		return result;
	}

	public static long getEmcSellValue(ItemStack stack)
	{
		long originalValue = EMCHelper.getEmcValue(stack);

		if (originalValue == 0)
		{
			return 0;
		}

		long emc = (long) Math.floor(originalValue * EMCMapper.covalenceLoss);

		if (emc < 1)
		{
			emc = 1;
		}

		return emc;
	}

	public static String getEmcSellString(ItemStack stack, int stackSize)
	{
		if (EMCMapper.covalenceLoss == 1.0)
		{
			return " ";
		}

		BigInteger emc = BigInteger.valueOf(EMCHelper.getEmcSellValue(stack));

		return " (" + Constants.EMC_FORMATTER.format(emc.multiply(BigInteger.valueOf(stackSize))) + ")";
	}

	public static long getKleinStarMaxEmc(ItemStack stack)
	{
		if (stack.getItem() instanceof KleinStar)
		{
			return Constants.MAX_KLEIN_EMC[((KleinStar) stack.getItem()).tier.ordinal()];
		}
		return 0;
	}

	private static double getStoredEMCBonus(ItemStack stack) {
		if (stack.getTag() != null && stack.getTag().contains("StoredEMC")) {
			return stack.getTag().getDouble("StoredEMC");
		} else if (stack.getItem() instanceof IItemEmc) {
			return ((IItemEmc) stack.getItem()).getStoredEmc(stack);
		}
		return 0;
	}

	public static long getEMCPerDurability(ItemStack stack) {
		if(stack.isEmpty())
			return 0;

		if(stack.isDamageable()){
			ItemStack stackCopy = stack.copy();
			stackCopy.setDamage(0);
			long emc = (long)Math.ceil(EMCHelper.getEmcValue(stackCopy) / (double) stack.getMaxDamage());
			return emc > 1 ? emc : 1;
		}
		return 1;
	}
}
