package moze_intel.projecte.gameObjs.registration.impl;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.gameObjs.registration.DoubleWrappedRegistryObject;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

@ParametersAreNonnullByDefault
public class BlockRegistryObject<BLOCK extends Block, ITEM extends Item> extends DoubleWrappedRegistryObject<BLOCK, ITEM> implements ItemLike, IHasTranslationKey {

	public BlockRegistryObject(RegistryObject<BLOCK> blockRegistryObject, RegistryObject<ITEM> itemRegistryObject) {
		super(blockRegistryObject, itemRegistryObject);
	}

	@Nonnull
	public BLOCK getBlock() {
		return getPrimary();
	}

	@Nonnull
	@Override
	public ITEM asItem() {
		return getSecondary();
	}

	@Override
	public String getTranslationKey() {
		return getBlock().getDescriptionId();
	}

	public static class WallOrFloorBlockRegistryObject<BLOCK extends Block, WALL_BLOCK extends Block, ITEM extends StandingAndWallBlockItem> extends BlockRegistryObject<BLOCK, ITEM> {

		@Nonnull
		private final RegistryObject<WALL_BLOCK> wallRO;

		public WallOrFloorBlockRegistryObject(RegistryObject<BLOCK> blockRegistryObject, RegistryObject<WALL_BLOCK> wallBlockRegistryObject,
				RegistryObject<ITEM> itemRegistryObject) {
			super(blockRegistryObject, itemRegistryObject);
			this.wallRO = wallBlockRegistryObject;
		}

		@Nonnull
		public WALL_BLOCK getWallBlock() {
			return wallRO.get();
		}
	}
}