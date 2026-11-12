package moze_intel.projecte.common.loot;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
import org.jetbrains.annotations.NotNull;

public class PEBlockLootTable extends BlockLootSubProvider {

	private final Set<Block> knownBlocks = new ReferenceOpenHashSet<>();

	public PEBlockLootTable(LootTableSubProvider.Context output) {
		super(Set.of(
				PEBlocks.COLLECTOR.getSecondary(),
				PEBlocks.COLLECTOR_MK2.getSecondary(),
				PEBlocks.COLLECTOR_MK3.getSecondary(),
				PEBlocks.CONDENSER.getSecondary(),
				PEBlocks.CONDENSER_MK2.getSecondary(),
				PEBlocks.DARK_MATTER.getSecondary(),
				PEBlocks.DARK_MATTER_FURNACE.getSecondary(),
				PEBlocks.DARK_MATTER_PEDESTAL.getSecondary(),
				PEBlocks.RED_MATTER.getSecondary(),
				PEBlocks.RED_MATTER_FURNACE.getSecondary(),
				PEBlocks.RELAY.getSecondary(),
				PEBlocks.RELAY_MK2.getSecondary(),
				PEBlocks.RELAY_MK3.getSecondary()
		), FeatureFlags.VANILLA_SET, output);
	}

	@Override
	protected void generate() {
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
	public void dropOther(@NotNull Block block, @NotNull ItemLike drop) {
		add(block, dropping(drop));
	}

	protected LootTable.Builder dropping(ItemLike item) {
		return LootTable.lootTable().withPool(applyExplosionCondition(item, LootPool.lootPool().setRolls(Holder.direct(new ConstantValue(1)))
				.add(LootItem.lootTableItem(item))
		));
	}

	private void registerCustomTNT(Block tnt) {
		add(tnt, LootTable.lootTable().withPool(applyExplosionCondition(tnt, LootPool.lootPool().setRolls(Holder.direct(new ConstantValue(1)))
				.add(LootItem.lootTableItem(tnt).when(MatchBlock.blockMatches(blocks, tnt,
						StatePropertiesPredicate.Builder.properties().hasProperty(TntBlock.UNSTABLE, false)))))));
	}

	@Override
	protected void add(@NotNull Block block, @NotNull LootTable.Builder table) {
		super.add(block, table);
		knownBlocks.add(block);
	}

	@NotNull
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return knownBlocks;
	}
}
