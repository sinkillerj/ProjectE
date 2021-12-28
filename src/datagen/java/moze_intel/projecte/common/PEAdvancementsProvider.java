package moze_intel.projecte.common;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PEAdvancementsProvider extends AdvancementProvider {

	public PEAdvancementsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, existingFileHelper);
	}

	@Override
	protected void registerAdvancements(@Nonnull Consumer<Advancement> advancementConsumer, @Nonnull ExistingFileHelper fileHelper) {
		Advancement root = Advancement.Builder.advancement()
				.display(PEItems.PHILOSOPHERS_STONE,
						PELang.PROJECTE.translate(),
						PELang.ADVANCEMENTS_PROJECTE_DESCRIPTION.translate(),
						new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
						FrameType.TASK,
						false,
						false,
						false)
				.addCriterion("philstone_recipe", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GLOWSTONE_DUST, Items.DIAMOND, Items.REDSTONE))
				.save(advancementConsumer, PECore.rl("root"), fileHelper);
		addTransmutation(advancementConsumer, fileHelper, root);
		addStorage(advancementConsumer, fileHelper, root);
		addMatters(advancementConsumer, fileHelper, root);
	}

	private static Advancement.Builder childDisplay(Advancement parent, ItemLike icon, ILangEntry title, ILangEntry description) {
		return Advancement.Builder.advancement()
				.parent(parent)
				.display(icon, title.translate(), description.translate(), null, FrameType.TASK, true, true, false);
	}

	private void addTransmutation(Consumer<Advancement> advancementConsumer, ExistingFileHelper fileHelper, Advancement parent) {
		Advancement root = childDisplay(parent, PEItems.PHILOSOPHERS_STONE, PELang.ADVANCEMENTS_PHILO_STONE, PELang.ADVANCEMENTS_PHILO_STONE_DESCRIPTION)
				.addCriterion("philosophers_stone", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.PHILOSOPHERS_STONE))
				.save(advancementConsumer, PECore.rl("philosophers_stone"), fileHelper);
		//Branch 1
		Advancement transmutationTable = childDisplay(root, PEBlocks.TRANSMUTATION_TABLE, PELang.ADVANCEMENTS_TRANSMUTATION_TABLE,
				PELang.ADVANCEMENTS_TRANSMUTATION_TABLE_DESCRIPTION)
				.addCriterion("trans_table", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.TRANSMUTATION_TABLE))
				.save(advancementConsumer, PECore.rl("transmutation_table"), fileHelper);
		childDisplay(transmutationTable, PEItems.TRANSMUTATION_TABLET, PELang.ADVANCEMENTS_TRANSMUTATION_TABLET, PELang.ADVANCEMENTS_TRANSMUTATION_TABLET_DESCRIPTION)
				.addCriterion("trans_tablet", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.TRANSMUTATION_TABLET))
				.save(advancementConsumer, PECore.rl("transmutation_tablet"), fileHelper);
		//Branch 2
		Advancement kleinStarEin = childDisplay(root, PEItems.KLEIN_STAR_EIN, PELang.ADVANCEMENTS_KLEIN_STAR, PELang.ADVANCEMENTS_KLEIN_STAR_DESCRIPTION)
				.addCriterion("klein_star", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.KLEIN_STAR_EIN))
				.save(advancementConsumer, PECore.rl("klein_star_ein"), fileHelper);
		childDisplay(kleinStarEin, PEItems.KLEIN_STAR_OMEGA, PELang.ADVANCEMENTS_KLEIN_STAR_BIG, PELang.ADVANCEMENTS_KLEIN_STAR_BIG_DESCRIPTION)
				.addCriterion("klein_star", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.KLEIN_STAR_OMEGA))
				.save(advancementConsumer, PECore.rl("klein_star_omega"), fileHelper);
	}

	private void addStorage(Consumer<Advancement> advancementConsumer, ExistingFileHelper fileHelper, Advancement parent) {
		Advancement root = childDisplay(parent, PEBlocks.ALCHEMICAL_CHEST, PELang.ADVANCEMENTS_ALCH_CHEST, PELang.ADVANCEMENTS_ALCH_CHEST_DESCRIPTION)
				.addCriterion("alch_chest", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.ALCHEMICAL_CHEST))
				.save(advancementConsumer, PECore.rl("alchemical_chest"), fileHelper);
		//Branch 1
		childDisplay(root, PEItems.WHITE_ALCHEMICAL_BAG, PELang.ADVANCEMENTS_ALCH_BAG, PELang.ADVANCEMENTS_ALCH_BAG_DESCRIPTION)
				.addCriterion("bag", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(PETags.Items.ALCHEMICAL_BAGS).build()))
				.save(advancementConsumer, PECore.rl("alchemical_bag"), fileHelper);
		//Branch 2
		Advancement condenser = childDisplay(root, PEBlocks.CONDENSER, PELang.ADVANCEMENTS_CONDENSER, PELang.ADVANCEMENTS_CONDENSER_DESCRIPTION)
				.addCriterion("condenser", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.CONDENSER))
				.save(advancementConsumer, PECore.rl("condenser"), fileHelper);
		Advancement collector = childDisplay(condenser, PEBlocks.COLLECTOR, PELang.ADVANCEMENTS_COLLECTOR, PELang.ADVANCEMENTS_COLLECTOR_DESCRIPTION)
				.addCriterion("collector", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.CONDENSER))
				.save(advancementConsumer, PECore.rl("collector"), fileHelper);
		childDisplay(collector, PEBlocks.RELAY, PELang.ADVANCEMENTS_RELAY, PELang.ADVANCEMENTS_RELAY_DESCRIPTION)
				.addCriterion("relay", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.RELAY))
				.save(advancementConsumer, PECore.rl("relay"), fileHelper);
	}

	private void addMatters(Consumer<Advancement> advancementConsumer, ExistingFileHelper fileHelper, Advancement parent) {
		Advancement root = childDisplay(parent, PEItems.DARK_MATTER, PELang.ADVANCEMENTS_DARK_MATTER, PELang.ADVANCEMENTS_DARK_MATTER_DESCRIPTION)
				.addCriterion("dm", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.DARK_MATTER))
				.save(advancementConsumer, PECore.rl("dark_matter"), fileHelper);
		//Branch 1
		Advancement dm_pickaxe = childDisplay(root, PEItems.DARK_MATTER_PICKAXE, PELang.ADVANCEMENTS_DARK_MATTER_PICKAXE,
				PELang.ADVANCEMENTS_DARK_MATTER_PICKAXE_DESCRIPTION)
				.addCriterion("dm_pick", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.DARK_MATTER_PICKAXE))
				.save(advancementConsumer, PECore.rl("dark_matter_pickaxe"), fileHelper);
		childDisplay(dm_pickaxe, PEItems.RED_MATTER_PICKAXE, PELang.ADVANCEMENTS_RED_MATTER_PICKAXE, PELang.ADVANCEMENTS_RED_MATTER_PICKAXE_DESCRIPTION)
				.addCriterion("rm_pick", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.RED_MATTER_PICKAXE))
				.save(advancementConsumer, PECore.rl("red_matter_pickaxe"), fileHelper);
		//Branch 2
		Advancement red_matter = childDisplay(root, PEItems.RED_MATTER, PELang.ADVANCEMENTS_RED_MATTER, PELang.ADVANCEMENTS_RED_MATTER_DESCRIPTION)
				.addCriterion("rm", InventoryChangeTrigger.TriggerInstance.hasItems(PEItems.RED_MATTER))
				.save(advancementConsumer, PECore.rl("red_matter"), fileHelper);
		Advancement red_matter_block = childDisplay(red_matter, PEBlocks.RED_MATTER, PELang.ADVANCEMENTS_RED_MATTER_BLOCK, PELang.ADVANCEMENTS_RED_MATTER_BLOCK_DESCRIPTION)
				.addCriterion("rm_block", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.RED_MATTER))
				.save(advancementConsumer, PECore.rl("red_matter_block"), fileHelper);
		childDisplay(red_matter_block, PEBlocks.RED_MATTER_FURNACE, PELang.ADVANCEMENTS_RED_MATTER_FURNACE, PELang.ADVANCEMENTS_RED_MATTER_FURNACE_DESCRIPTION)
				.addCriterion("rm_furnace", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.RED_MATTER_FURNACE))
				.save(advancementConsumer, PECore.rl("red_matter_furnace"), fileHelper);
		//Branch 3
		Advancement dark_matter_block = childDisplay(root, PEBlocks.DARK_MATTER, PELang.ADVANCEMENTS_DARK_MATTER_BLOCK, PELang.ADVANCEMENTS_DARK_MATTER_BLOCK_DESCRIPTION)
				.addCriterion("dm_block", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.DARK_MATTER))
				.save(advancementConsumer, PECore.rl("dark_matter_block"), fileHelper);
		childDisplay(dark_matter_block, PEBlocks.DARK_MATTER_FURNACE, PELang.ADVANCEMENTS_DARK_MATTER_FURNACE, PELang.ADVANCEMENTS_DARK_MATTER_FURNACE_DESCRIPTION)
				.addCriterion("dm_furnace", InventoryChangeTrigger.TriggerInstance.hasItems(PEBlocks.DARK_MATTER_FURNACE))
				.save(advancementConsumer, PECore.rl("dark_matter_furnace"), fileHelper);
	}
}