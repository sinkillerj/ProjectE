package moze_intel.projecte.common;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public class PEDataMapsProvider extends DataMapProvider {

	public PEDataMapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	protected void gather() {
		//Four times the burn time of coal
		int alchemicalCoal = 1_600 * 4;
		int mobiusFuel = alchemicalCoal * 4;
		int aeternalisFuel = mobiusFuel * 4;
		builder(NeoForgeDataMaps.FURNACE_FUELS)
				.add(PEItems.ALCHEMICAL_COAL, new FurnaceFuel(alchemicalCoal), false)
				.add(PEBlocks.ALCHEMICAL_COAL.getId(), new FurnaceFuel(alchemicalCoal * 9), false)
				.add(PEItems.MOBIUS_FUEL, new FurnaceFuel(mobiusFuel), false)
				.add(PEBlocks.MOBIUS_FUEL.getId(), new FurnaceFuel(mobiusFuel * 9), false)
				.add(PEItems.AETERNALIS_FUEL, new FurnaceFuel(aeternalisFuel), false)
				.add(PEBlocks.AETERNALIS_FUEL.getId(), new FurnaceFuel(aeternalisFuel * 9), false)
		;
	}
}