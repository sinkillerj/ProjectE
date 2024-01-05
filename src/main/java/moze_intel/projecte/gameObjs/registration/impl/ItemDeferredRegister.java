package moze_intel.projecte.gameObjs.registration.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.items.ICapabilityAware;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

public class ItemDeferredRegister extends PEDeferredRegister<Item> {

	private final List<ItemLike> allItems = new ArrayList<>();

	public ItemDeferredRegister(String modid) {
		super(Registries.ITEM, modid, ItemRegistryObject::new);
	}

	@Override
	public void register(@NotNull IEventBus bus) {
		super.register(bus);
		bus.addListener(this::registerCapabilities);
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (ItemLike itemLike : allItems) {
			Item item = itemLike.asItem();
			if (item instanceof IAlchBagItem) {
				event.registerItem(PECapabilities.ALCH_BAG_ITEM_CAPABILITY, (stack, context) -> (IAlchBagItem) stack.getItem(), item);
			}
			if (item instanceof IAlchChestItem) {
				event.registerItem(PECapabilities.ALCH_CHEST_ITEM_CAPABILITY, (stack, context) -> (IAlchChestItem) stack.getItem(), item);
			}
			if (item instanceof IExtraFunction) {
				event.registerItem(PECapabilities.EXTRA_FUNCTION_ITEM_CAPABILITY, (stack, context) -> (IExtraFunction) stack.getItem(), item);
			}
			if (item instanceof IItemCharge) {
				event.registerItem(PECapabilities.CHARGE_ITEM_CAPABILITY, (stack, context) -> (IItemCharge) stack.getItem(), item);
			}
			if (item instanceof IItemEmcHolder) {
				event.registerItem(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY, (stack, context) -> (IItemEmcHolder) stack.getItem(), item);
			}
			if (item instanceof IModeChanger) {
				event.registerItem(PECapabilities.MODE_CHANGER_ITEM_CAPABILITY, (stack, context) -> (IModeChanger) stack.getItem(), item);
			}
			if (item instanceof IPedestalItem) {
				event.registerItem(PECapabilities.PEDESTAL_ITEM_CAPABILITY, (stack, context) -> (IPedestalItem) stack.getItem(), item);
			}
			if (item instanceof IProjectileShooter) {
				event.registerItem(PECapabilities.PROJECTILE_SHOOTER_ITEM_CAPABILITY, (stack, context) -> (IProjectileShooter) stack.getItem(), item);
			}
			if (item instanceof ICapabilityAware capabilityAware) {
				capabilityAware.attachCapabilities(event);
			}
		}
	}

	public ItemRegistryObject<Item> register(String name) {
		return registerSimple(name, Item::new);
	}

	public ItemRegistryObject<Item> registerFireImmune(String name) {
		return registerFireImmune(name, Item::new);
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerSimple(String name, Function<Item.Properties, ITEM> sup) {
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
		return register(name, () -> sup.apply(propertyModifier.apply(new Item.Properties())));
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public <ITEM extends Item> ItemRegistryObject<ITEM> register(@NotNull String name, @NotNull Supplier<? extends ITEM> sup) {
		ItemRegistryObject<ITEM> registeredItem = (ItemRegistryObject<ITEM>) super.register(name, sup);
		allItems.add(registeredItem);
		return registeredItem;
	}
}