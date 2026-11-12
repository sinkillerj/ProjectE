package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.BiFunction;
import java.util.function.Function;
import moze_intel.projecte.gameObjs.registration.DoubleDeferredRegister;
import moze_intel.projecte.gameObjs.registration.PERegistryUtil;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject.WallOrFloorBlockRegistryObject;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockDeferredRegister extends DoubleDeferredRegister<Block, Item> {

	public BlockDeferredRegister(String modid) {
		super(Registries.BLOCK, new ItemDeferredRegister(modid), modid);
	}

	public <BLOCK extends Block> BlockRegistryObject<BLOCK, BlockItem> register(String name, Function<Identifier, ? extends BLOCK> blockSupplier) {
		return register(name, blockSupplier, (block, props) -> new BlockItem(block, props));
	}

	public <BLOCK extends Block, WALL_BLOCK extends Block> WallOrFloorBlockRegistryObject<BLOCK, WALL_BLOCK, StandingAndWallBlockItem> registerWallOrFloorItem(String name,
			Function<BlockBehaviour.Properties, BLOCK> blockSupplier, Function<BlockBehaviour.Properties, WALL_BLOCK> wallBlockSupplier,
			Function<Identifier, BlockBehaviour.Properties> propertiesFactory) {
		DeferredHolder<Block, BLOCK> primaryObject = primaryRegister.register(name, id -> blockSupplier.apply(propertiesFactory.apply(id)));
		DeferredHolder<Block, WALL_BLOCK> wallObject = primaryRegister.register("wall_" + name, id -> wallBlockSupplier.apply(propertiesFactory.apply(id)));
		return new WallOrFloorBlockRegistryObject<>(primaryObject, wallObject, secondaryRegister.register(name, itemId -> new StandingAndWallBlockItem(primaryObject.get(), wallObject.get(),
				Direction.DOWN, PERegistryUtil.blockItemProps(itemId))));
	}

	public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> register(String name, Function<Identifier, ? extends BLOCK> blockSupplier,
			BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
		DeferredHolder<Block, BLOCK> primaryObject = primaryRegister.register(name, blockSupplier);
		return new BlockRegistryObject<>(primaryObject, secondaryRegister.register(name, itemId -> itemCreator.apply(primaryObject.get(), PERegistryUtil.blockItemProps(itemId))));
	}
}
