package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.DoubleDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject.WallOrFloorBlockRegistryObject;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockDeferredRegister extends DoubleDeferredRegister<Block, Item> {

	public BlockDeferredRegister(String modid) {
		super(Registries.BLOCK, Registries.ITEM, modid);
	}

	public BlockRegistryObject<Block, BlockItem> register(String name, BlockBehaviour.Properties properties) {
		return registerDefaultProperties(name, () -> new Block(properties), BlockItem::new);
	}

	public <BLOCK extends Block> BlockRegistryObject<BLOCK, BlockItem> register(String name, Supplier<? extends BLOCK> blockSupplier) {
		return registerDefaultProperties(name, blockSupplier, BlockItem::new);
	}

	public <BLOCK extends Block, WALL_BLOCK extends Block> WallOrFloorBlockRegistryObject<BLOCK, WALL_BLOCK, StandingAndWallBlockItem> registerWallOrFloorItem(String name,
			Function<BlockBehaviour.Properties, BLOCK> blockSupplier, Function<BlockBehaviour.Properties, WALL_BLOCK> wallBlockSupplier,
			BlockBehaviour.Properties baseProperties) {
		DeferredHolder<Block, BLOCK> primaryObject = primaryRegister.register(name, () -> blockSupplier.apply(baseProperties));
		DeferredHolder<Block, WALL_BLOCK> wallObject = primaryRegister.register("wall_" + name, () -> wallBlockSupplier.apply(baseProperties.lootFrom(primaryObject)));
		return new WallOrFloorBlockRegistryObject<>(primaryObject, wallObject, secondaryRegister.register(name, () -> new StandingAndWallBlockItem(primaryObject.get(), wallObject.get(),
				new Item.Properties(), Direction.DOWN)));
	}

	public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerDefaultProperties(String name, Supplier<? extends BLOCK> blockSupplier,
			BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
		return register(name, blockSupplier, block -> itemCreator.apply(block, new Item.Properties()));
	}

	public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> register(String name, Supplier<? extends BLOCK> blockSupplier,
			Function<BLOCK, ITEM> itemCreator) {
		return register(name, blockSupplier, itemCreator, BlockRegistryObject::new);
	}
}