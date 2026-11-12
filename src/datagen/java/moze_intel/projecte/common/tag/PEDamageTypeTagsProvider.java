package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class PEDamageTypeTagsProvider extends TagsProvider<DamageType> {

	public PEDamageTypeTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider) {
		super(output, Registries.DAMAGE_TYPE, lookupProvider, PECore.MODID);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		ResourceKey<DamageType> playerAttack = PEDamageTypes.BYPASS_ARMOR_PLAYER_ATTACK.key();
		getOrCreateRawBuilder(DamageTypeTags.BYPASSES_ARMOR).addElement(playerAttack.identifier());
		getOrCreateRawBuilder(DamageTypeTags.CAN_BREAK_ARMOR_STAND).addElement(playerAttack.identifier());
		getOrCreateRawBuilder(DamageTypeTags.IS_PLAYER_ATTACK).addElement(playerAttack.identifier());
		getOrCreateRawBuilder(DamageTypeTags.PANIC_CAUSES).addElement(playerAttack.identifier());
		getOrCreateRawBuilder(Tags.DamageTypes.IS_PHYSICAL).addElement(playerAttack.identifier());
	}

	@NotNull
	@Override
	public String getName() {
		return "Damage Type Tags";
	}
}
