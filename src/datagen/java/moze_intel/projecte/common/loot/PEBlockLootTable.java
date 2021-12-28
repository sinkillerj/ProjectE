package moze_intel.projecte.common.loot;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class PEBlockLootTable extends BlockLoot {

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
	public void dropOther(@Nonnull Block block, @Nonnull ItemLike drop) {
		//Override to use our own dropping method that names the loot table
		add(block, dropping(drop));
	}

	protected static LootTable.Builder dropping(ItemLike item) {
		return LootTable.lootTable().withPool(applyExplosionCondition(item, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.name("main")
				.add(LootItem.lootTableItem(item))
		));
	}

	private void registerCustomTNT(Block tnt) {
		add(tnt, LootTable.lootTable().withPool(applyExplosionCondition(tnt, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.name("main")
				.add(LootItem.lootTableItem(tnt).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(tnt)
						.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TntBlock.UNSTABLE, false)))))));
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