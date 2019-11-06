package moze_intel.projecte.utils;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage.EmcAction;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.gameObjs.items.KleinStar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Helper class for EMC. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class EMCHelper {

	/**
	 * Consumes EMC from fuel items or Klein Stars Any extra EMC is discarded !!! To retain remainder EMC use ItemPE.consumeFuel()
	 */
	public static long consumePlayerFuel(PlayerEntity player, long minFuel) {
		if (player.abilities.isCreativeMode) {
			return minFuel;
		}

		IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElseThrow(NullPointerException::new);
		Map<Integer, Integer> map = new LinkedHashMap<>();
		boolean metRequirement = false;
		long emcConsumed = 0;

		ItemStack offhand = player.getHeldItemOffhand();

		if (!offhand.isEmpty()) {
			Optional<IItemEmcHolder> holderCapability = LazyOptionalHelper.toOptional(offhand.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
			if (holderCapability.isPresent()) {
				IItemEmcHolder emcHolder = holderCapability.get();
				long simulatedExtraction = emcHolder.extractEmc(offhand, minFuel, EmcAction.SIMULATE);
				if (simulatedExtraction == minFuel) {
					long actualExtracted = emcHolder.extractEmc(offhand, simulatedExtraction, EmcAction.EXECUTE);
					player.openContainer.detectAndSendChanges();
					return actualExtracted;
				}
			}
		}

		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);

			if (stack.isEmpty()) {
				continue;
			}
			Optional<IItemEmcHolder> holderCapability = LazyOptionalHelper.toOptional(stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
			if (holderCapability.isPresent()) {
				IItemEmcHolder emcHolder = holderCapability.get();
				long simulatedExtraction = emcHolder.extractEmc(stack, minFuel, EmcAction.SIMULATE);
				if (simulatedExtraction == minFuel) {
					long actualExtracted = emcHolder.extractEmc(stack, simulatedExtraction, EmcAction.EXECUTE);
					player.openContainer.detectAndSendChanges();
					return actualExtracted;
				}
			} else if (!metRequirement) {
				if (FuelMapper.isStackFuel(stack)) {
					long emc = getEmcValue(stack);
					int toRemove = (int) Math.ceil((double) (minFuel - emcConsumed) / emc);

					if (stack.getCount() >= toRemove) {
						map.put(i, toRemove);
						emcConsumed += emc * toRemove;
						metRequirement = true;
					} else {
						map.put(i, stack.getCount());
						emcConsumed += emc * stack.getCount();

						if (emcConsumed >= minFuel) {
							metRequirement = true;
						}
					}

				}
			}
		}

		if (metRequirement) {
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				inv.extractItem(entry.getKey(), entry.getValue(), false);
			}

			player.openContainer.detectAndSendChanges();
			return emcConsumed;
		}

		return -1;
	}

	public static boolean doesItemHaveEmc(ItemInfo info) {
		return getEmcValue(info) > 0;
	}

	public static boolean doesItemHaveEmc(ItemStack stack) {
		return getEmcValue(stack) > 0;
	}

	public static boolean doesItemHaveEmc(IItemProvider item) {
		return getEmcValue(item) > 0;
	}

	public static long getEmcValue(IItemProvider item) {
		return item == null ? 0 : getEmcValue(ItemInfo.fromItem(item.asItem()));
	}

	/**
	 * Does not consider stack size
	 */
	public static long getEmcValue(ItemStack stack) {
		return stack.isEmpty() ? 0 : getEmcValue(ItemInfo.fromStack(stack));
	}

	/**
	 * Does not consider stack size
	 */
	public static long getEmcValue(ItemInfo info) {
		return NBTManager.getEmcValue(info);
	}

	public static long getEmcSellValue(ItemStack stack) {
		long originalValue = getEmcValue(stack);

		if (originalValue == 0) {
			return 0;
		}

		long emc = (long) Math.floor(originalValue * EMCMappingHandler.covalenceLoss);

		if (emc < 1) {
			if (EMCMappingHandler.covalenceLossRounding) {
				emc = 1;
			} else {
				emc = 0;
			}
		}

		return emc;
	}

	public static String getEmcSellString(ItemStack stack, int stackSize) {
		if (EMCMappingHandler.covalenceLoss == 1.0) {
			return " ";
		}

		BigInteger emc = BigInteger.valueOf(EMCHelper.getEmcSellValue(stack));

		return " (" + Constants.EMC_FORMATTER.format(emc.multiply(BigInteger.valueOf(stackSize))) + ")";
	}

	public static long getKleinStarMaxEmc(ItemStack stack) {
		if (stack.getItem() instanceof KleinStar) {
			return Constants.MAX_KLEIN_EMC[((KleinStar) stack.getItem()).tier.ordinal()];
		}
		return 0;
	}

	private static long getStoredEMCBonus(ItemStack stack) {
		if (stack.getOrCreateTag().contains("StoredEMC")) {
			return stack.getTag().getLong("StoredEMC");
		}
		return LazyOptionalHelper.toOptional(stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY)).map(emcHolder -> emcHolder.getStoredEmc(stack)).orElse(0L);
	}

	public static long getEMCPerDurability(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}

		if (stack.isDamageable()) {
			ItemStack stackCopy = stack.copy();
			stackCopy.setDamage(0);
			long emc = (long) Math.ceil(EMCHelper.getEmcValue(stackCopy) / (double) stack.getMaxDamage());
			return emc > 1 ? emc : 1;
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
		double unprocessedEMC = stack.getOrCreateTag().getDouble("UnprocessedEMC");
		unprocessedEMC += amount;
		long toRemove = (long) unprocessedEMC;
		unprocessedEMC -= toRemove;
		stack.getTag().putDouble("UnprocessedEMC", unprocessedEMC);
		return toRemove;
	}
}