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
		dropSelf(PEBlocks.AETERNALIS_FUEL.getBlock());
		dropSelf(PEBlocks.ALCHEMICAL_CHEST.getBlock());
		dropSelf(PEBlocks.ALCHEMICAL_COAL.getBlock());
		dropSelf(PEBlocks.COLLECTOR.getBlock());
		dropSelf(PEBlocks.COLLECTOR_MK2.getBlock());
		dropSelf(PEBlocks.COLLECTOR_MK3.getBlock());
		dropSelf(PEBlocks.CONDENSER.getBlock());
		dropSelf(PEBlocks.CONDENSER_MK2.getBlock());
		dropSelf(PEBlocks.DARK_MATTER.getBlock());
		dropSelf(PEBlocks.DARK_MATTER_FURNACE.getBlock());
		dropSelf(PEBlocks.DARK_MATTER_PEDESTAL.getBlock());
		dropSelf(PEBlocks.INTERDICTION_TORCH.getBlock());
		dropSelf(PEBlocks.MOBIUS_FUEL.getBlock());
		dropSelf(PEBlocks.RED_MATTER.getBlock());
		dropSelf(PEBlocks.RED_MATTER_FURNACE.getBlock());
		dropSelf(PEBlocks.RELAY.getBlock());
		dropSelf(PEBlocks.RELAY_MK2.getBlock());
		dropSelf(PEBlocks.RELAY_MK3.getBlock());
		dropSelf(PEBlocks.TRANSMUTATION_TABLE.getBlock());

		registerCustomTNT(PEBlocks.NOVA_CATACLYSM.getBlock());
		registerCustomTNT(PEBlocks.NOVA_CATALYST.getBlock());
	}

	@Override
	public void dropOther(@Nonnull Block block, @Nonnull IItemProvider drop) {
		//Override to use our own dropping method that names the loot table
		add(block, dropping(drop));
	}

	protected static LootTable.Builder dropping(IItemProvider item) {
		return LootTable.lootTable().withPool(applyExplosionCondition(item, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
				.name("main")
				.add(ItemLootEntry.lootTableItem(item))
		));
	}

	private void registerCustomTNT(Block tnt) {
		add(tnt, LootTable.lootTable().withPool(applyExplosionCondition(tnt, LootPool.lootPool().setRolls(ConstantRange.exactly(1))
				.name("main")
				.add(ItemLootEntry.lootTableItem(tnt).when(BlockStateProperty.hasBlockStateProperties(tnt)
						.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TNTBlock.UNSTABLE, false)))))));
	}

	@Override
	protected void add(@Nonnull Block block, @Nonnull LootTable.Builder table) {
		//Overwrite the core register method to add to our list of known blocks
		super.add(block, table);
		knownBlocks.add(block);
	}

	@Nonnull
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return knownBlocks;
	}
}