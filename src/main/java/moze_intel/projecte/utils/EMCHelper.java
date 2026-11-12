package moze_intel.projecte.utils;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Iterator;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Helper class for EMC. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class EMCHelper {

	//Only ever use a single decimal point for our formatter, because the majority of the time we are a whole number except for when we are abbreviating
	private static final NumberFormat EMC_FORMATTER = Util.make(NumberFormat.getInstance(), formatter -> formatter.setMaximumFractionDigits(1));

	public static <K> Object2IntMap<K> intMapOf(final K key, int value) {
		return Object2IntMaps.singleton(key, value);
	}

	public static <K> Object2IntMap<K> intMapOf(final K key, int value, final K key2, int value2) {
		Object2IntMap<K> intMap = new Object2IntArrayMap<>(2);
		intMap.put(key, value);
		intMap.put(key2, value2);
		return intMap;
	}

	public static <K> Object2IntMap<K> intMapOf(final K key, int value, final K key2, int value2, final K key3, int value3) {
		Object2IntMap<K> intMap = new Object2IntArrayMap<>(3);
		intMap.put(key, value);
		intMap.put(key2, value2);
		intMap.put(key3, value3);
		return intMap;
	}

	public static <K> Object2IntMap<K> intMapOf(final K key, int value, final K key2, int value2, final K key3, int value3, final K key4, int value4) {
		Object2IntMap<K> intMap = new Object2IntArrayMap<>(3);
		intMap.put(key, value);
		intMap.put(key2, value2);
		intMap.put(key3, value3);
		intMap.put(key4, value4);
		return intMap;
	}

	/**
	 * Consumes EMC from fuel items or Klein Stars Any extra EMC is discarded !!! To retain remainder EMC use ItemPE.consumeFuel()
	 *
	 * @implNote Order it tries to extract from is, Curios, Offhand, main inventory
	 */
	public static long consumePlayerFuel(Player player, @Range(from = 0, to = Long.MAX_VALUE) long minFuel) {
		if (player.isCreative() || minFuel == 0) {
			return minFuel;
		}
		ResourceHandler<ItemResource> curios = IntegrationHelper.getCurioItemHandler(player);
		if (curios != null) {
			for (int i = 0, slots = PEItemStacksHandler.getSlotCount(curios); i < slots; i++) {
				long actualExtracted = tryExtract(PEItemStacksHandler.getStack(curios, i), minFuel);
				if (actualExtracted > 0) {
					player.containerMenu.broadcastChanges();
					return actualExtracted;
				}
			}
		}

		//Note: The implementation of this will iterate in the order: Main inventory, Armor, Offhand
		ResourceHandler<ItemResource> inv = ItemCapabilityHelper.getPlayerInventory(player);
		if (inv != null) {
			//Ensure that we have an item handler capability, because if for example the player is dead we will not
			Int2IntMap map = new Int2IntOpenHashMap();
			boolean metRequirement = false;
			long emcConsumed = 0;
			for (int i = 0, slots = PEItemStacksHandler.getSlotCount(inv); i < slots; i++) {
				ItemStack stack = PEItemStacksHandler.getStack(inv, i);
				if (stack.isEmpty()) {
					continue;
				}
				long actualExtracted = tryExtract(stack, minFuel);
				if (actualExtracted > 0) { //Prioritize extracting from emc storage items
					player.containerMenu.broadcastChanges();
					return actualExtracted;
				} else if (!metRequirement && FuelMapper.isStackFuel(stack)) {
					//TODO: Should we be validating we simulate that we will be able to extract the stack and how much of it?
					long emc = IEMCProxy.INSTANCE.getValue(stack);
					int toRemove = Mth.ceil((double) (minFuel - emcConsumed) / emc);
					int actualRemoved = Math.min(stack.getCount(), toRemove);
					if (actualRemoved > 0) {
						map.put(i, actualRemoved);
						emcConsumed += emc * actualRemoved;
						metRequirement = emcConsumed >= minFuel;
					}
				}
			}
			if (metRequirement) {
				for (Iterator<Int2IntMap.Entry> iterator = Int2IntMaps.fastIterator(map); iterator.hasNext(); ) {
					Int2IntMap.Entry entry = iterator.next();
					//TODO: Should we be validating we were able to actually extract the items?
					PEItemStacksHandler.extractItem(inv, entry.getIntKey(), entry.getIntValue(), false);
				}
				player.containerMenu.broadcastChanges();
				return emcConsumed;
			}
		}
		return -1;
	}

	private static long tryExtract(@NotNull ItemStack stack, long minFuel) {
		IItemEmcHolder emcHolder = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (emcHolder != null) {
			long simulatedExtraction = emcHolder.extractEmc(stack, minFuel, EmcAction.SIMULATE);
			if (simulatedExtraction >= minFuel) {
				return emcHolder.extractEmc(stack, simulatedExtraction, EmcAction.EXECUTE);
			}
		}
		return 0;
	}

	public static String formatEmc(Number emc) {
		return EMC_FORMATTER.format(emc);
	}

	public static String formatEmc(double emc) {
		return EMC_FORMATTER.format(emc);
	}

	public static String formatEmc(long emc) {
		return EMC_FORMATTER.format(emc);
	}

	@Range(from = 0, to = Long.MAX_VALUE)
	public static long getEmcSellValue(@Range(from = 0, to = Long.MAX_VALUE) long originalValue) {
		if (originalValue == 0) {
			return 0;
		}
		long emc = Mth.lfloor(originalValue * ProjectEConfig.server.difficulty.covalenceLoss.get());
		if (emc < 1) {
			if (ProjectEConfig.server.difficulty.covalenceLossRounding.get()) {
				emc = 1;
			} else {
				emc = 0;
			}
		}
		return emc;
	}

	public static Component getEmcTextComponent(long emc, int stackSize) {
		if (ProjectEConfig.server.difficulty.covalenceLoss.get() == 1.0) {
			ILangEntry prefix;
			String value;
			if (stackSize > 1) {
				prefix = PELang.EMC_STACK_TOOLTIP;
				value = formatEmc(BigInteger.valueOf(emc).multiply(BigInteger.valueOf(stackSize)));
			} else {
				prefix = PELang.EMC_TOOLTIP;
				value = formatEmc(emc);
			}
			return prefix.translateColored(ChatFormatting.YELLOW, ChatFormatting.WHITE, value);
		}
		//Sell enabled
		long emcSellValue = getEmcSellValue(emc);
		ILangEntry prefix;
		String value;
		String sell;
		if (stackSize > 1) {
			prefix = PELang.EMC_STACK_TOOLTIP_WITH_SELL;
			BigInteger bigIntStack = BigInteger.valueOf(stackSize);
			value = formatEmc(BigInteger.valueOf(emc).multiply(bigIntStack));
			sell = formatEmc(BigInteger.valueOf(emcSellValue).multiply(bigIntStack));
		} else {
			prefix = PELang.EMC_TOOLTIP_WITH_SELL;
			value = formatEmc(emc);
			sell = formatEmc(emcSellValue);
		}
		return prefix.translateColored(ChatFormatting.YELLOW, ChatFormatting.WHITE, value, ChatFormatting.BLUE, sell);
	}

	@Range(from = 0, to = Long.MAX_VALUE)
	public static long getEMCPerDurability(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else if (stack.isDamageableItem()) {
			ItemStack stackCopy = stack.copy();
			stackCopy.setDamageValue(0);
			long emc = (long) Math.ceil(IEMCProxy.INSTANCE.getValue(stackCopy) / (double) stack.getMaxDamage());
			return Math.max(emc, 1);
		}
		return 1;
	}

	/**
	 * Adds the given amount to the amount of unprocessed EMC the stack has. The amount returned should be used for figuring out how much EMC actually gets removed. While
	 * the remaining fractional EMC will be stored in UnprocessedEMC.
	 *
	 * @param stack  The stack to set the UnprocessedEMC tag to.
	 * @param amount The partial amount of EMC to add with the current UnprocessedEMC
	 *
	 * @return The amount of non fractional EMC no longer being stored in UnprocessedEMC.
	 */
	public static long removeFractionalEMC(ItemStack stack, double amount) {
		double unprocessedEMC = stack.getOrDefault(PEDataComponentTypes.UNPROCESSED_EMC, 0.0);
		unprocessedEMC += amount;
		long toRemove = (long) unprocessedEMC;
		unprocessedEMC -= toRemove;
		stack.set(PEDataComponentTypes.UNPROCESSED_EMC, unprocessedEMC);
		return toRemove;
	}
}
