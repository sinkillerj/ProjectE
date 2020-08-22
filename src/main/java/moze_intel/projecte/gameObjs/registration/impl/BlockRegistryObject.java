package moze_intel.projecte.gameObjs.registration.impl;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.gameObjs.registration.DoubleWrappedRegistryObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

@ParametersAreNonnullByDefault
public class BlockRegistryObject<BLOCK extends Block, ITEM extends Item> extends DoubleWrappedRegistryObject<BLOCK, ITEM> implements IItemProvider {

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

    public static class WallOrFloorBlockRegistryObject<BLOCK extends Block, WALL_BLOCK extends Block, ITEM extends WallOrFloorItem> extends BlockRegistryObject<BLOCK, ITEM> {

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