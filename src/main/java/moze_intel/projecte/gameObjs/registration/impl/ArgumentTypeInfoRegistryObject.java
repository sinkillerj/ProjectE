package moze_intel.projecte.gameObjs.registration.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraftforge.registries.RegistryObject;

public class ArgumentTypeInfoRegistryObject<TYPE extends ArgumentType<?>> extends WrappedRegistryObject<ArgumentTypeInfo<TYPE, ? extends ArgumentTypeInfo.Template<TYPE>>> {

	public ArgumentTypeInfoRegistryObject(RegistryObject<ArgumentTypeInfo<TYPE, ? extends ArgumentTypeInfo.Template<TYPE>>> registryObject) {
		super(registryObject);
	}
}