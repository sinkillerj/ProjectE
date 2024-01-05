package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PEDamageTypeTagsProvider extends TagsProvider<DamageType> {

	public PEDamageTypeTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.DAMAGE_TYPE, lookupProvider, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		tag(DamageTypeTags.BYPASSES_ARMOR).add(
				PEDamageTypes.BYPASS_ARMOR_PLAYER_ATTACK.key()
		);
	}

	@NotNull
	@Override
	public String getName() {
		return "Damage Type Tags";
	}
}