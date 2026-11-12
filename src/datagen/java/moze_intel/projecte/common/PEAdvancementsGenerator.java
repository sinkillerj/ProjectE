package moze_intel.projecte.common;

import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class PEAdvancementsGenerator extends AdvancementSubProvider {

	public PEAdvancementsGenerator(@NotNull BootstrapContext<Advancement> output) {
		super(output);
	}

	@Override
	public void generate() {
		AdvancementHolder root = Advancement.Builder.advancement()
				.rootDisplay(PEItems.PHILOSOPHERS_STONE.get(),
						PELang.PROJECTE.translate(),
						PELang.ADVANCEMENTS_PROJECTE_DESCRIPTION.translate(),
						Identifier.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
						AdvancementType.TASK,
						false,
						false,
						false)
				.addCriterion("philstone_recipe", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GLOWSTONE_DUST, Items.DIAMOND, Items.REDSTONE))
				.save(output, "projecte:root");
		addTransmutation(root);
		addStorage(root);
		addMatters(root);
	}

	private static Advancement.Builder childDisplay(AdvancementHolder parent, ItemLike icon, ILangEntry title, ILangEntry description) {
		return Advancement.Builder.advancement()
				.parent(parent)
				.display(icon.asItem(), title.translate(), description.translate(), AdvancementType.TASK, true, true, false);
	}

	private void addTransmutation(AdvancementHolder parent) {
		AdvancementHolder root = childDisplay(parent, PEItems.PHILOSOPHERS_STONE, PELang.ADVANCEMENTS_PHILO_STONE, PELang.ADVANCEMENTS_PHILO_STONE_DESCRIPTION)
				.addCriterion("philosophers_stone", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.PHILOSOPHERS_STONE))
				.save(output, "projecte:philosophers_stone");
		AdvancementHolder transmutationTable = childDisplay(root, PEBlocks.TRANSMUTATION_TABLE, PELang.ADVANCEMENTS_TRANSMUTATION_TABLE,
				PELang.ADVANCEMENTS_TRANSMUTATION_TABLE_DESCRIPTION)
				.addCriterion("trans_table", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.TRANSMUTATION_TABLE))
				.save(output, "projecte:transmutation_table");
		childDisplay(transmutationTable, PEItems.TRANSMUTATION_TABLET, PELang.ADVANCEMENTS_TRANSMUTATION_TABLET, PELang.ADVANCEMENTS_TRANSMUTATION_TABLET_DESCRIPTION)
				.addCriterion("trans_tablet", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.TRANSMUTATION_TABLET))
				.save(output, "projecte:transmutation_tablet");
		AdvancementHolder kleinStarEin = childDisplay(root, PEItems.KLEIN_STAR_EIN, PELang.ADVANCEMENTS_KLEIN_STAR, PELang.ADVANCEMENTS_KLEIN_STAR_DESCRIPTION)
				.addCriterion("klein_star", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.KLEIN_STAR_EIN))
				.save(output, "projecte:klein_star_ein");
		childDisplay(kleinStarEin, PEItems.KLEIN_STAR_OMEGA, PELang.ADVANCEMENTS_KLEIN_STAR_BIG, PELang.ADVANCEMENTS_KLEIN_STAR_BIG_DESCRIPTION)
				.addCriterion("klein_star", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.KLEIN_STAR_OMEGA))
				.save(output, "projecte:klein_star_omega");
	}

	private void addStorage(AdvancementHolder parent) {
		var items = output.lookup(Registries.ITEM);
		AdvancementHolder root = childDisplay(parent, PEBlocks.ALCHEMICAL_CHEST, PELang.ADVANCEMENTS_ALCH_CHEST, PELang.ADVANCEMENTS_ALCH_CHEST_DESCRIPTION)
				.addCriterion("alch_chest", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.ALCHEMICAL_CHEST))
				.save(output, "projecte:alchemical_chest");
		childDisplay(root, PEItems.WHITE_ALCHEMICAL_BAG, PELang.ADVANCEMENTS_ALCH_BAG, PELang.ADVANCEMENTS_ALCH_BAG_DESCRIPTION)
				.addCriterion("bag", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, PETags.Items.ALCHEMICAL_BAGS).build()))
				.save(output, "projecte:alchemical_bag");
		AdvancementHolder condenser = childDisplay(root, PEBlocks.CONDENSER, PELang.ADVANCEMENTS_CONDENSER, PELang.ADVANCEMENTS_CONDENSER_DESCRIPTION)
				.addCriterion("condenser", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.CONDENSER))
				.save(output, "projecte:condenser");
		AdvancementHolder collector = childDisplay(condenser, PEBlocks.COLLECTOR, PELang.ADVANCEMENTS_COLLECTOR, PELang.ADVANCEMENTS_COLLECTOR_DESCRIPTION)
				.addCriterion("collector", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.CONDENSER))
				.save(output, "projecte:collector");
		childDisplay(collector, PEBlocks.RELAY, PELang.ADVANCEMENTS_RELAY, PELang.ADVANCEMENTS_RELAY_DESCRIPTION)
				.addCriterion("relay", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.RELAY))
				.save(output, "projecte:relay");
	}

	private void addMatters(AdvancementHolder parent) {
		AdvancementHolder root = childDisplay(parent, PEItems.DARK_MATTER, PELang.ADVANCEMENTS_DARK_MATTER, PELang.ADVANCEMENTS_DARK_MATTER_DESCRIPTION)
				.addCriterion("dm", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.DARK_MATTER))
				.save(output, "projecte:dark_matter");
		AdvancementHolder dm_pickaxe = childDisplay(root, PEItems.DARK_MATTER_PICKAXE, PELang.ADVANCEMENTS_DARK_MATTER_PICKAXE,
				PELang.ADVANCEMENTS_DARK_MATTER_PICKAXE_DESCRIPTION)
				.addCriterion("dm_pick", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.DARK_MATTER_PICKAXE))
				.save(output, "projecte:dark_matter_pickaxe");
		childDisplay(dm_pickaxe, PEItems.RED_MATTER_PICKAXE, PELang.ADVANCEMENTS_RED_MATTER_PICKAXE, PELang.ADVANCEMENTS_RED_MATTER_PICKAXE_DESCRIPTION)
				.addCriterion("rm_pick", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.RED_MATTER_PICKAXE))
				.save(output, "projecte:red_matter_pickaxe");
		AdvancementHolder red_matter = childDisplay(root, PEItems.RED_MATTER, PELang.ADVANCEMENTS_RED_MATTER, PELang.ADVANCEMENTS_RED_MATTER_DESCRIPTION)
				.addCriterion("rm", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.RED_MATTER))
				.save(output, "projecte:red_matter");
		AdvancementHolder red_matter_block = childDisplay(red_matter, PEBlocks.RED_MATTER, PELang.ADVANCEMENTS_RED_MATTER_BLOCK, PELang.ADVANCEMENTS_RED_MATTER_BLOCK_DESCRIPTION)
				.addCriterion("rm_block", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.RED_MATTER))
				.save(output, "projecte:red_matter_block");
		childDisplay(red_matter_block, PEBlocks.RED_MATTER_FURNACE, PELang.ADVANCEMENTS_RED_MATTER_FURNACE, PELang.ADVANCEMENTS_RED_MATTER_FURNACE_DESCRIPTION)
				.addCriterion("rm_furnace", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.RED_MATTER_FURNACE))
				.save(output, "projecte:red_matter_furnace");
		AdvancementHolder dark_matter_block = childDisplay(root, PEBlocks.DARK_MATTER, PELang.ADVANCEMENTS_DARK_MATTER_BLOCK, PELang.ADVANCEMENTS_DARK_MATTER_BLOCK_DESCRIPTION)
				.addCriterion("dm_block", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.DARK_MATTER))
				.save(output, "projecte:dark_matter_block");
		childDisplay(dark_matter_block, PEBlocks.DARK_MATTER_FURNACE, PELang.ADVANCEMENTS_DARK_MATTER_FURNACE, PELang.ADVANCEMENTS_DARK_MATTER_FURNACE_DESCRIPTION)
				.addCriterion("dm_furnace", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.DARK_MATTER_FURNACE))
				.save(output, "projecte:dark_matter_furnace");
	}
}
