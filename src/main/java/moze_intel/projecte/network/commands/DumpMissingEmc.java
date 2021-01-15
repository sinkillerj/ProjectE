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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraftforge.registries.ForgeRegistries;

public class DumpMissingEmc {

	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("dumpmissingemc")
				.requires(cs -> cs.hasPermissionLevel(2))
				.executes(DumpMissingEmc::execute);
	}

	private static int execute(CommandContext<CommandSource> ctx) {
		CommandSource source = ctx.getSource();
		Set<ItemInfo> missing = new HashSet<>();
		for (Map.Entry<RegistryKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
			Item item = entry.getValue();
			ItemGroup group = item.getGroup();
			if (group != null || !(item instanceof EnchantedBookItem)) {
				//Vanilla has special handing for filling the enchanted book item's group, so don't try to fill it
				try {
					NonNullList<ItemStack> items = NonNullList.create();
					item.fillItemGroup(group, items);
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
			source.sendFeedback(PELang.DUMP_MISSING_EMC_NONE_MISSING.translate(), true);
		} else {
			if (missingCount == 1) {
				source.sendFeedback(PELang.DUMP_MISSING_EMC_ONE_MISSING.translate(), true);
			} else {
				source.sendFeedback(PELang.DUMP_MISSING_EMC_MULTIPLE_MISSING.translate(missingCount), true);
			}
			for (ItemInfo itemInfo : missing) {
				PECore.LOGGER.info(itemInfo);
			}
		}
		return missingCount;
	}
}