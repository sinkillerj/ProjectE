package moze_intel.projecte.emc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.integration.jei.PEJeiPlugin;
import moze_intel.projecte.network.packets.to_client.SyncFuelMapperPKT;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public final class FuelMapper {

	private static final List<Item> FUEL_MAP = new ArrayList<>();

	/**
	 * Used on server to load the map based on the tag
	 */
	public static void loadMap() {
		FUEL_MAP.clear();
		//TODO - 1.18: Validate that this works? But it seems to be late or something which causes it to not show in JEI
		for (Holder<Item> itemHolder : Registry.ITEM.getTagOrEmpty(PETags.Items.COLLECTOR_FUEL)) {
			Item item = itemHolder.value();
			if (EMCHelper.doesItemHaveEmc(item)) {
				FUEL_MAP.add(item);
			}
		}
		FUEL_MAP.sort(Comparator.comparing(EMCHelper::getEmcValue));
	}

	/**
	 * Used on client side to set values from server
	 */
	public static void setFuelMap(List<Item> map) {
		FUEL_MAP.clear();
		FUEL_MAP.addAll(map);
	}

	public static SyncFuelMapperPKT getSyncPacket() {
		return new SyncFuelMapperPKT(FUEL_MAP);
	}

	public static boolean isStackFuel(ItemStack stack) {
		return FUEL_MAP.contains(stack.getItem());
	}

	public static boolean isStackMaxFuel(ItemStack stack) {
		return FUEL_MAP.indexOf(stack.getItem()) == FUEL_MAP.size() - 1;
	}

	public static ItemStack getFuelUpgrade(ItemStack stack) {
		int index = FUEL_MAP.indexOf(stack.getItem());
		if (index == -1) {
			PECore.LOGGER.warn("Tried to upgrade invalid fuel: {}", stack);
			return ItemStack.EMPTY;
		}
		int nextIndex = index == FUEL_MAP.size() - 1 ? 0 : index + 1;
		return new ItemStack(FUEL_MAP.get(nextIndex));
	}

	/**
	 * @return An immutable version of the Fuel Map
	 */
	public static List<Item> getFuelMap() {
		return Collections.unmodifiableList(FUEL_MAP);
	}
}