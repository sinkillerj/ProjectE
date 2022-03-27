package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class ItemDeferredRegister extends WrappedDeferredRegister<Item> {

	private static final CreativeModeTab creativeTab = new CreativeModeTab(PECore.MODID) {
		@NotNull
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(PEItems.PHILOSOPHERS_STONE);
		}

		@NotNull
		@Override
		public Component getDisplayName() {
			//Overwrite the lang key to match the one representing ProjectE
			return PELang.PROJECTE.translate();
		}
	};

	public ItemDeferredRegister(String modid) {
		super(ForgeRegistries.ITEMS, modid);
	}

	public static Item.Properties getBaseProperties() {
		return new Item.Properties().tab(creativeTab);
	}

	public ItemRegistryObject<Item> register(String name) {
		return register(name, Item::new);
	}

	public ItemRegistryObject<Item> registerFireImmune(String name) {
		return registerFireImmune(name, Item::new);
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, UnaryOperator.identity());
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerFireImmune(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, Item.Properties::fireResistant);
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerNoStack(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, properties -> properties.stacksTo(1));
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerNoStackFireImmune(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, properties -> properties.stacksTo(1).fireResistant());
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup, UnaryOperator<Item.Properties> propertyModifier) {
		return register(name, () -> sup.apply(propertyModifier.apply(getBaseProperties())));
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Supplier<? extends ITEM> sup) {
		return register(name, sup, ItemRegistryObject::new);
	}
}