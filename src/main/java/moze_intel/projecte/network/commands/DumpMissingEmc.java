package moze_intel.projecte.network.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class DumpMissingEmc {

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("dumpmissingemc")
				.requires(cs -> cs.hasPermission(2))
				.executes(DumpMissingEmc::execute);
	}

	private static int execute(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		Set<ItemInfo> missing = new HashSet<>();
		for (Map.Entry<ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
			Item item = entry.getValue();
			CreativeModeTab group = item.getItemCategory();
			if (group != null || !(item instanceof EnchantedBookItem)) {
				//Vanilla has special handing for filling the enchanted book item's group, so don't try to fill it
				try {
					NonNullList<ItemStack> items = NonNullList.create();
					item.fillItemCategory(group, items);
					boolean hasValidItem = false;
					for (ItemStack stack : items) {
						if (!stack.isEmpty()) {
							hasValidItem = true;
							ItemInfo itemInfo = ItemInfo.fromStack(stack);
							if (EMCHelper.getEmcValue(itemInfo) == 0) {
								missing.add(itemInfo);
							}
						}
					}
					if (hasValidItem) {
						//Skip to next item if we found a non empty item
						continue;
					}
					//Otherwise fall down and try the item directly
				} catch (Exception ignored) {
					//Fall down and try to use the raw item
				}
			}
			if (item != Items.AIR) {
				ItemInfo itemInfo = ItemInfo.fromItem(item);
				if (EMCHelper.getEmcValue(itemInfo) == 0) {
					missing.add(itemInfo);
				}
			}
		}
		int missingCount = missing.size();
		if (missingCount == 0) {
			source.sendSuccess(PELang.DUMP_MISSING_EMC_NONE_MISSING.translate(), true);
		} else {
			if (missingCount == 1) {
				source.sendSuccess(PELang.DUMP_MISSING_EMC_ONE_MISSING.translate(), true);
			} else {
				source.sendSuccess(PELang.DUMP_MISSING_EMC_MULTIPLE_MISSING.translate(missingCount), true);
			}
			for (ItemInfo itemInfo : missing) {
				PECore.LOGGER.info(itemInfo);
			}
		}
		return missingCount;
	}
}