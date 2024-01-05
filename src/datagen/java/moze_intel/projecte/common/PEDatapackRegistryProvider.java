package moze_intel.projecte.common;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEDamageTypes;
import moze_intel.projecte.gameObjs.registries.PEDamageTypes.PEDamageType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

public class PEDatapackRegistryProvider extends DatapackBuiltinEntriesProvider {

	public PEDatapackRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, Set.of(PECore.MODID));
	}

	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.DAMAGE_TYPE, context -> {
				for (PEDamageType damageType : PEDamageTypes.DAMAGE_TYPES.values()) {
					context.register(damageType.key(), new DamageType(damageType.msgId(), damageType.exhaustion()));
				}
			});
}