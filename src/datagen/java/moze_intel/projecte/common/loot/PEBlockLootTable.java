package moze_intel.projecte.common.loot;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.TNTBlock;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.util.IItemProvider;

public class PEBlockLootTable extends BlockLootTables {

	private final Set<Block> knownBlocks = new HashSet<>();

	@Override
	protected void addTables() {
		registerDropSelfLootTable(PEBlocks.AETERNALIS_FUEL.getBlock());
		registerDropSelfLootTable(PEBlocks.ALCHEMICAL_CHEST.getBlock());
		registerDropSelfLootTable(PEBlocks.ALCHEMICAL_COAL.getBlock());
		registerDropSelfLootTable(PEBlocks.COLLECTOR.getBlock());
		registerDropSelfLootTable(PEBlocks.COLLECTOR_MK2.getBlock());
		registerDropSelfLootTable(PEBlocks.COLLECTOR_MK3.getBlock());
		registerDropSelfLootTable(PEBlocks.CONDENSER.getBlock());
		registerDropSelfLootTable(PEBlocks.CONDENSER_MK2.getBlock());
		registerDropSelfLootTable(PEBlocks.DARK_MATTER.getBlock());
		registerDropSelfLootTable(PEBlocks.DARK_MATTER_FURNACE.getBlock());
		registerDropSelfLootTable(PEBlocks.DARK_MATTER_PEDESTAL.getBlock());
		registerDropSelfLootTable(PEBlocks.INTERDICTION_TORCH.getBlock());
		registerDropSelfLootTable(PEBlocks.MOBIUS_FUEL.getBlock());
		registerDropSelfLootTable(PEBlocks.RED_MATTER.getBlock());
		registerDropSelfLootTable(PEBlocks.RED_MATTER_FURNACE.getBlock());
		registerDropSelfLootTable(PEBlocks.RELAY.getBlock());
		registerDropSelfLootTable(PEBlocks.RELAY_MK2.getBlock());
		registerDropSelfLootTable(PEBlocks.RELAY_MK3.getBlock());
		registerDropSelfLootTable(PEBlocks.TRANSMUTATION_TABLE.getBlock());

		registerCustomTNT(PEBlocks.NOVA_CATACLYSM.getBlock());
		registerCustomTNT(PEBlocks.NOVA_CATALYST.getBlock());
	}

	@Override
	public void registerDropping(@Nonnull Block block, @Nonnull IItemProvider drop) {
		//Override to use our own dropping method that names the loot table
		registerLootTable(block, dropping(drop));
	}

	protected static LootTable.Builder dropping(IItemProvider item) {
		return LootTable.builder().addLootPool(withSurvivesExplosion(item, LootPool.builder().rolls(ConstantRange.of(1)).name("main").addEntry(ItemLootEntry.builder(item))));
	}

	private void registerCustomTNT(Block tnt) {
		registerLootTable(tnt, LootTable.builder().addLootPool(withSurvivesExplosion(tnt, LootPool.builder().rolls(ConstantRange.of(1))
				.name("main")
				.addEntry(ItemLootEntry.builder(tnt).acceptCondition(BlockStateProperty.builder(tnt)
						.fromProperties(StatePropertiesPredicate.Builder.newBuilder().withBoolProp(TNTBlock.UNSTABLE, false)))))));
	}

	@Override
	protected void registerLootTable(@Nonnull Block block, @Nonnull LootTable.Builder table) {
		//Overwrite the core register method to add to our list of known blocks
		super.registerLootTable(block, table);
		knownBlocks.add(block);
	}

	@Nonnull
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return knownBlocks;
	}
}