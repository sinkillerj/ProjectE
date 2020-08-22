package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;

public class ContainerTypeRegistryObject<CONTAINER extends Container> extends WrappedRegistryObject<ContainerType<CONTAINER>> {

    public ContainerTypeRegistryObject(RegistryObject<ContainerType<CONTAINER>> registryObject) {
        super(registryObject);
    }
}