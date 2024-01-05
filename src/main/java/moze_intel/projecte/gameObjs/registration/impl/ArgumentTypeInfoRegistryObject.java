package moze_intel.projecte.gameObjs.registration.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfo.Template;
import net.minecraft.resources.ResourceKey;

public class ArgumentTypeInfoRegistryObject<TYPE extends ArgumentType<?>> extends PEDeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<TYPE, ? extends Template<TYPE>>> {

	public ArgumentTypeInfoRegistryObject(ResourceKey<ArgumentTypeInfo<?, ?>> key) {
		super(key);
	}
}