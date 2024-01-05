package moze_intel.projecte.gameObjs.registration.impl;

import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.gameObjs.registration.DoubleWrappedRegistryObject;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public class BlockRegistryObject<BLOCK extends Block, ITEM extends Item> extends DoubleWrappedRegistryObject<Block, BLOCK, Item, ITEM> implements ItemLike, IHasTranslationKey {

	public BlockRegistryObject(DeferredHolder<Block, BLOCK> blockRegistryObject, DeferredHolder<Item, ITEM> itemRegistryObject) {
		super(blockRegistryObject, itemRegistryObject);
	}

	@NotNull
	public BLOCK getBlock() {
		return getPrimary();
	}

	@NotNull
	@Override
	public ITEM asItem() {
		return getSecondary();
	}

	@Override
	public String getTranslationKey() {
		return getBlock().getDescriptionId();
	}

	public static class WallOrFloorBlockRegistryObject<BLOCK extends Block, WALL_BLOCK extends Block, ITEM extends StandingAndWallBlockItem> extends BlockRegistryObject<BLOCK, ITEM> {

		@NotNull
		private final DeferredHolder<Block, WALL_BLOCK> wallRO;

		public WallOrFloorBlockRegistryObject(DeferredHolder<Block, BLOCK> blockRegistryObject, DeferredHolder<Block, WALL_BLOCK> wallBlockRegistryObject,
				DeferredHolder<Item, ITEM> itemRegistryObject) {
			super(blockRegistryObject, itemRegistryObject);
			this.wallRO = wallBlockRegistryObject;
		}

		@NotNull
		public WALL_BLOCK getWallBlock() {
			return wallRO.get();
		}

		public String getWallName() {
			return wallRO.getId().getPath();
		}
	}
}