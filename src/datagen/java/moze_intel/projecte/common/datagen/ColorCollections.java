package moze_intel.projecte.common.datagen;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColorCollection;

public final class ColorCollections {

	private ColorCollections() {}

	public static Block[] blocks(ColorCollection<Block> collection) {
		return collection.asList().toArray(Block[]::new);
	}

	public static Item[] items(ColorCollection<Item> collection) {
		return collection.asList().toArray(Item[]::new);
	}

	public static Item woolItem(DyeColor color) {
		return net.minecraft.world.item.Items.WOOL.pick(color);
	}
}
