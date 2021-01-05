package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.DoubleDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject.WallOrFloorBlockRegistryObject;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockDeferredRegister extends DoubleDeferredRegister<Block, Item> {

	public BlockDeferredRegister() {
		super(ForgeRegistries.BLOCKS, ForgeRegistries.ITEMS);
	}

	public BlockRegistryObject<Block, BlockItem> register(String name, AbstractBlock.Properties properties) {
		return registerDefaultProperties(name, () -> new Block(properties), BlockItem::new);
	}

	public <BLOCK extends Block> BlockRegistryObject<BLOCK, BlockItem> register(String name, Supplier<? extends BLOCK> blockSupplier) {
		return registerDefaultProperties(name, blockSupplier, BlockItem::new);
	}

	public <BLOCK extends Block, WALL_BLOCK extends Block> WallOrFloorBlockRegistryObject<BLOCK, WALL_BLOCK, WallOrFloorItem> registerWallOrFloorItem(String name,
			Function<AbstractBlock.Properties, BLOCK> blockSupplier, Function<AbstractBlock.Properties, WALL_BLOCK> wallBlockSupplier,
			AbstractBlock.Properties baseProperties) {
		RegistryObject<BLOCK> primaryObject = primaryRegister.register(name, () -> blockSupplier.apply(baseProperties));
		RegistryObject<WALL_BLOCK> wallObject = primaryRegister.register("wall_" + name, () -> wallBlockSupplier.apply(baseProperties.lootFrom(primaryObject.get())));
		return new WallOrFloorBlockRegistryObject<>(primaryObject, wallObject, secondaryRegister.register(name, () -> new WallOrFloorItem(primaryObject.get(), wallObject.get(),
				ItemDeferredRegister.getBaseProperties())));
	}

	public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerDefaultProperties(String name, Supplier<? extends BLOCK> blockSupplier,
			BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
		return register(name, blockSupplier, block -> itemCreator.apply(block, ItemDeferredRegister.getBaseProperties()));
	}

	public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> register(String name, Supplier<? extends BLOCK> blockSupplier,
			Function<BLOCK, ITEM> itemCreator) {
		return register(name, blockSupplier, itemCreator, BlockRegistryObject::new);
	}
}