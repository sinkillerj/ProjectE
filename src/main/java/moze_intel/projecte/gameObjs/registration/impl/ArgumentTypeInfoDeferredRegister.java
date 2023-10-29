package moze_intel.projecte.gameObjs.registration.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Function;
import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;

public class ArgumentTypeInfoDeferredRegister extends WrappedDeferredRegister<ArgumentTypeInfo<?, ?>> {

	public ArgumentTypeInfoDeferredRegister(String modid) {
		super(Registries.COMMAND_ARGUMENT_TYPE, modid);
	}

	public <TYPE extends ArgumentType<?>> ArgumentTypeInfoRegistryObject<TYPE> registerContextFree(String name, Class<TYPE> argumentClass, Supplier<TYPE> constructor) {
		return register(name, argumentClass, () -> SingletonArgumentInfo.contextFree(constructor));
	}

	public <TYPE extends ArgumentType<?>> ArgumentTypeInfoRegistryObject<TYPE> registerContextAware(String name, Class<TYPE> argumentClass,
			Function<CommandBuildContext, TYPE> constructor) {
		return register(name, argumentClass, () -> SingletonArgumentInfo.contextAware(constructor));
	}

	public <TYPE extends ArgumentType<?>> ArgumentTypeInfoRegistryObject<TYPE> register(String name, Class<TYPE> argumentClass,
			Supplier<ArgumentTypeInfo<TYPE, ? extends ArgumentTypeInfo.Template<TYPE>>> sup) {
		return register(name, () -> ArgumentTypeInfos.registerByClass(argumentClass, sup.get()), ArgumentTypeInfoRegistryObject::new);
	}
}