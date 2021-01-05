package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemDeferredRegister extends WrappedDeferredRegister<Item> {

	private static final ItemGroup creativeTab = new ItemGroup(PECore.MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(PEItems.PHILOSOPHERS_STONE);
		}

		@Nonnull
		@Override
		public ITextComponent getGroupName() {
			//Overwrite the lang key to match the one representing ProjectE
			return PELang.PROJECTE.translate();
		}
	};

	public ItemDeferredRegister() {
		super(ForgeRegistries.ITEMS);
	}

	public static Item.Properties getBaseProperties() {
		return new Item.Properties().group(creativeTab);
	}

	public static Item.Properties getBasePropertiesNoStack() {
		return getBaseProperties().maxStackSize(1);
	}

	public ItemRegistryObject<Item> register(String name) {
		return register(name, Item::new);
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, () -> sup.apply(getBaseProperties()));
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerNoStack(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, () -> sup.apply(getBasePropertiesNoStack()));
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Supplier<? extends ITEM> sup) {
		return register(name, sup, ItemRegistryObject::new);
	}
}