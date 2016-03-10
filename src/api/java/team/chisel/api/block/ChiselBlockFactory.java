package team.chisel.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

/**
 * Factory to create builders to create blocks
 */
public class ChiselBlockFactory {

    private String domain;

    private ChiselBlockFactory(String domain) {
        this.domain = domain;
    }

    public static ChiselBlockFactory newFactory(String domain) {
        return new ChiselBlockFactory(domain);
    }

    public <T extends Block & ICarvable> ChiselBlockBuilder<T> newBlock(Material material, String blockName, BlockCreator<T> creator, Class<T> blockClass) {
        return newBlock(material, blockName, creator, blockClass, ItemBlock.class);
    }

    public <T extends Block & ICarvable> ChiselBlockBuilder<T> newBlock(Material material, String blockName, BlockCreator<T> creator, Class<T> blockClass, Class<? extends ItemBlock> itemBlockClass) {
        return newBlock(material, blockName, new BlockProvider<T>() {

            @Override
            public T createBlock(Material material, int index, int totalBlocks, VariationData... data) {
                return creator.createBlock(material, index, totalBlocks, data);
            }

            @Override
            public Class<T> getBlockClass() {
                return blockClass;
            }

            @Override
            public Class<? extends ItemBlock> getItemClass() {
                return itemBlockClass;
            }
        });
    }

    public <T extends Block & ICarvable> ChiselBlockBuilder<T> newBlock(Material material, String blockName, BlockProvider<T> provider) {
        return new ChiselBlockBuilder<T>(material, domain, blockName, provider);
    }
}
