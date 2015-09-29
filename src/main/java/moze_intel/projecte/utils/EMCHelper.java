package moze_intel.projecte.utils;

import com.google.common.collect.Maps;

import moze_intel.projecte.api.exception.NoCreationEmcValueException;
import moze_intel.projecte.api.exception.NoDestructionEmcValueException;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.config.ProjectEConfig;
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
					int emc = getEmcValueForDestructionWithDamageAndBonuses(stack);
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

	public static boolean hasEmcValueForCreation(ItemStack itemStack) {
		SimpleStack iStack = new SimpleStack(itemStack);
		iStack.qnty = 1;
		if (iStack.isValid()) return EMCMapper.emcForCreation.containsKey(iStack);
		return false;
	}

	public static boolean hasBaseEmcValueForDestruction(ItemStack itemStack) {
		SimpleStack iStack = new SimpleStack(itemStack);
		iStack.qnty = 1;
		if (iStack.isValid())
		{
			if (EMCMapper.emcForDestruction.containsKey(iStack))
			{
				return true;
			} else if(!itemStack.getHasSubtypes() && itemStack.getMaxDamage() != 0){
				iStack.damage = 0;
				return EMCMapper.emcForDestruction.containsKey(iStack);
			}
		}
		return false;
	}

	public static boolean hasEmcValueForDestruction(ItemStack itemStack) {
		return hasBaseEmcValueForDestruction(itemStack);
	}

	public static int getEmcValueForCreationOrZero(ItemStack itemStack) {
		return hasEmcValueForCreation(itemStack) ? getEmcValueForCreation(itemStack) : 0;
	}

	public static int getEmcValueForCreation(ItemStack itemStack) {
		SimpleStack iStack = new SimpleStack(itemStack);
		iStack.qnty = 1;
		if (iStack.isValid())
		{
			if (EMCMapper.emcForCreation.containsKey(iStack))
			{
				return EMCMapper.emcForCreation.get(iStack);
			}
		}
		throw new NoCreationEmcValueException();
	}

	public static int getEmcValueForDestructionWithDamageAndBonuses(ItemStack itemStack) {
		if (hasBaseEmcValueForDestruction(itemStack))
		{
			return (int)(getBaseEmcValueForDestruction(itemStack) * getDamageFactor(itemStack)) + getEnchantEmcBonus(itemStack) + getStoredEMCBonus(itemStack);
		}
		throw new NoDestructionEmcValueException();
	}

	public static int getBaseEmcValueForDestruction(ItemStack itemStack) {
		SimpleStack iStack = new SimpleStack(itemStack);
		iStack.qnty = 1;
		if (iStack.isValid())
		{
			if (EMCMapper.emcForDestruction.containsKey(iStack))
			{
				return EMCMapper.emcForDestruction.get(iStack);
			} else if(!itemStack.getHasSubtypes() && itemStack.getMaxDamage() != 0){
				iStack.damage = 0;
				if (EMCMapper.emcForDestruction.containsKey(iStack)) {
					return EMCMapper.emcForDestruction.get(iStack);
				}
			}
		}
		throw new NoDestructionEmcValueException();
	}

	public static double getDamageFactor(ItemStack stack) {
		double relDamage = (stack.getMaxDamage() - stack.getItemDamage());

		if (relDamage <= 0)
		{
			//Don't use durability if this happens.
			return 1;
		}

		return relDamage / stack.getMaxDamage();
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

	public static int getStoredEMCBonus(ItemStack stack) {
		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("StoredEMC")) {
			return (int) stack.stackTagCompound.getDouble("StoredEMC");
		}
		return 0;
	}
}
