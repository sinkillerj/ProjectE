package team.chisel.api.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public interface BlockProvider<T extends Block & ICarvable> extends BlockCreator<T> {

    Class<T> getBlockClass();

    Class<? extends ItemBlock> getItemClass();

}
