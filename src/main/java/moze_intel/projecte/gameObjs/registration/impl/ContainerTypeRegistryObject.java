package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;

public class ContainerTypeRegistryObject<CONTAINER extends AbstractContainerMenu> extends WrappedRegistryObject<MenuType<CONTAINER>> {

	public ContainerTypeRegistryObject(RegistryObject<MenuType<CONTAINER>> registryObject) {
		super(registryObject);
	}
}