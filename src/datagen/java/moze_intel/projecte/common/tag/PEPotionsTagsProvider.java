package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.NotNull;

public class PEPotionsTagsProvider extends TagsProvider<Potion> {

	public PEPotionsTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, Registries.POTION, lookupProvider, PECore.MODID);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		getOrCreateRawBuilder(PETags.Potions.IGNORE_MISSING_EMC)
				.addElement(Potions.LUCK.getKey().identifier());
	}

	@NotNull
	@Override
	public String getName() {
		return "Potion Tags";
	}
}
