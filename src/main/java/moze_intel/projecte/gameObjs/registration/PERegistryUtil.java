package moze_intel.projecte.gameObjs.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PERegistryUtil {

	private PERegistryUtil() {
	}

	public static BlockBehaviour.Properties blockProps(Identifier id) {
		return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id));
	}

	public static Item.Properties itemProps(Identifier id) {
		return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
	}

	public static Item.Properties blockItemProps(Identifier id) {
		return itemProps(id).useBlockDescriptionPrefix();
	}
}
