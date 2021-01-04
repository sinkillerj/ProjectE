package moze_intel.projecte.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
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
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Vanilla copy of Advancement provider, slightly modified to be more friendly/usable by us
public class PEAdvancementsProvider implements IDataProvider {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private final DataGenerator generator;

	public PEAdvancementsProvider(DataGenerator generatorIn) {
		this.generator = generatorIn;
	}

	@Override
	public void act(@Nonnull DirectoryCache cache) {
		Path outputFolder = this.generator.getOutputFolder();
		Set<ResourceLocation> set = new HashSet<>();
		addAdvancements(advancement -> {
			if (set.add(advancement.getId())) {
				Path path = getPath(outputFolder, advancement);
				try {
					IDataProvider.save(GSON, cache, advancement.copy().serialize(), path);
				} catch (IOException ioexception) {
					LOGGER.error("Couldn't save advancement {}", path, ioexception);
				}
			} else {
				throw new IllegalStateException("Duplicate advancement " + advancement.getId());
			}
		});
	}

	private static Path getPath(Path path, Advancement advancement) {
		return path.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
	}

	@Nonnull
	@Override
	public String getName() {
		return "Advancements";
	}

	private void addAdvancements(Consumer<Advancement> advancementConsumer) {
		Advancement root = Advancement.Builder.builder()
				.withDisplay(PEItems.PHILOSOPHERS_STONE,
						PELang.PROJECTE.translate(),
						PELang.ADVANCEMENTS_PROJECTE_DESCRIPTION.translate(),
						new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
						FrameType.TASK,
						false,
						false,
						false)
				.withCriterion("philstone_recipe", InventoryChangeTrigger.Instance.forItems(Items.GLOWSTONE_DUST, Items.DIAMOND, Items.REDSTONE))
				.register(advancementConsumer, PECore.rl("root").toString());
		addTransmutation(advancementConsumer, root);
		addStorage(advancementConsumer, root);
		addMatters(advancementConsumer, root);
	}

	private static Advancement.Builder childDisplay(Advancement parent, IItemProvider icon, ILangEntry title, ILangEntry description) {
		return Advancement.Builder.builder()
				.withParent(parent)
				.withDisplay(icon, title.translate(), description.translate(), null, FrameType.TASK, true, true, false);
	}

	private void addTransmutation(Consumer<Advancement> advancementConsumer, Advancement parent) {
		Advancement root = childDisplay(parent, PEItems.PHILOSOPHERS_STONE, PELang.ADVANCEMENTS_PHILO_STONE, PELang.ADVANCEMENTS_PHILO_STONE_DESCRIPTION)
				.withCriterion("philosophers_stone", InventoryChangeTrigger.Instance.forItems(PEItems.PHILOSOPHERS_STONE))
				.register(advancementConsumer, PECore.rl("philosophers_stone").toString());
		//Branch 1
		Advancement transmutationTable = childDisplay(root, PEBlocks.TRANSMUTATION_TABLE, PELang.ADVANCEMENTS_TRANSMUTATION_TABLE,
				PELang.ADVANCEMENTS_TRANSMUTATION_TABLE_DESCRIPTION)
				.withCriterion("trans_table", InventoryChangeTrigger.Instance.forItems(PEBlocks.TRANSMUTATION_TABLE))
				.register(advancementConsumer, PECore.rl("transmutation_table").toString());
		childDisplay(transmutationTable, PEItems.TRANSMUTATION_TABLET, PELang.ADVANCEMENTS_TRANSMUTATION_TABLET, PELang.ADVANCEMENTS_TRANSMUTATION_TABLET_DESCRIPTION)
				.withCriterion("trans_tablet", InventoryChangeTrigger.Instance.forItems(PEItems.TRANSMUTATION_TABLET))
				.register(advancementConsumer, PECore.rl("transmutation_tablet").toString());
		//Branch 2
		Advancement kleinStarEin = childDisplay(root, PEItems.KLEIN_STAR_EIN, PELang.ADVANCEMENTS_KLEIN_STAR, PELang.ADVANCEMENTS_KLEIN_STAR_DESCRIPTION)
				.withCriterion("klein_star", InventoryChangeTrigger.Instance.forItems(PEItems.KLEIN_STAR_EIN))
				.register(advancementConsumer, PECore.rl("klein_star_ein").toString());
		childDisplay(kleinStarEin, PEItems.KLEIN_STAR_OMEGA, PELang.ADVANCEMENTS_KLEIN_STAR_BIG, PELang.ADVANCEMENTS_KLEIN_STAR_BIG_DESCRIPTION)
				.withCriterion("klein_star", InventoryChangeTrigger.Instance.forItems(PEItems.KLEIN_STAR_OMEGA))
				.register(advancementConsumer, PECore.rl("klein_star_omega").toString());
	}

	private void addStorage(Consumer<Advancement> advancementConsumer, Advancement parent) {
		Advancement root = childDisplay(parent, PEBlocks.ALCHEMICAL_CHEST, PELang.ADVANCEMENTS_ALCH_CHEST, PELang.ADVANCEMENTS_ALCH_CHEST_DESCRIPTION)
				.withCriterion("alch_chest", InventoryChangeTrigger.Instance.forItems(PEBlocks.ALCHEMICAL_CHEST))
				.register(advancementConsumer, PECore.rl("alchemical_chest").toString());
		//Branch 1
		childDisplay(root, PEItems.WHITE_ALCHEMICAL_BAG, PELang.ADVANCEMENTS_ALCH_BAG, PELang.ADVANCEMENTS_ALCH_BAG_DESCRIPTION)
				.withCriterion("bag", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(PETags.Items.ALCHEMICAL_BAGS).build()))
				.register(advancementConsumer, PECore.rl("alchemical_bag").toString());
		//Branch 2
		Advancement condenser = childDisplay(root, PEBlocks.CONDENSER, PELang.ADVANCEMENTS_CONDENSER, PELang.ADVANCEMENTS_CONDENSER_DESCRIPTION)
				.withCriterion("condenser", InventoryChangeTrigger.Instance.forItems(PEBlocks.CONDENSER))
				.register(advancementConsumer, PECore.rl("condenser").toString());
		Advancement collector = childDisplay(condenser, PEBlocks.COLLECTOR, PELang.ADVANCEMENTS_COLLECTOR, PELang.ADVANCEMENTS_COLLECTOR_DESCRIPTION)
				.withCriterion("collector", InventoryChangeTrigger.Instance.forItems(PEBlocks.CONDENSER))
				.register(advancementConsumer, PECore.rl("collector").toString());
		childDisplay(collector, PEBlocks.RELAY, PELang.ADVANCEMENTS_RELAY, PELang.ADVANCEMENTS_RELAY_DESCRIPTION)
				.withCriterion("relay", InventoryChangeTrigger.Instance.forItems(PEBlocks.RELAY))
				.register(advancementConsumer, PECore.rl("relay").toString());
	}

	private void addMatters(Consumer<Advancement> advancementConsumer, Advancement parent) {
		Advancement root = childDisplay(parent, PEItems.DARK_MATTER, PELang.ADVANCEMENTS_DARK_MATTER, PELang.ADVANCEMENTS_DARK_MATTER_DESCRIPTION)
				.withCriterion("dm", InventoryChangeTrigger.Instance.forItems(PEItems.DARK_MATTER))
				.register(advancementConsumer, PECore.rl("dark_matter").toString());
		//Branch 1
		Advancement dm_pickaxe = childDisplay(root, PEItems.DARK_MATTER_PICKAXE, PELang.ADVANCEMENTS_DARK_MATTER_PICKAXE,
				PELang.ADVANCEMENTS_DARK_MATTER_PICKAXE_DESCRIPTION)
				.withCriterion("dm_pick", InventoryChangeTrigger.Instance.forItems(PEItems.DARK_MATTER_PICKAXE))
				.register(advancementConsumer, PECore.rl("dark_matter_pickaxe").toString());
		childDisplay(dm_pickaxe, PEItems.RED_MATTER_PICKAXE, PELang.ADVANCEMENTS_RED_MATTER_PICKAXE, PELang.ADVANCEMENTS_RED_MATTER_PICKAXE_DESCRIPTION)
				.withCriterion("rm_pick", InventoryChangeTrigger.Instance.forItems(PEItems.RED_MATTER_PICKAXE))
				.register(advancementConsumer, PECore.rl("red_matter_pickaxe").toString());
		//Branch 2
		Advancement red_matter = childDisplay(root, PEItems.RED_MATTER, PELang.ADVANCEMENTS_RED_MATTER, PELang.ADVANCEMENTS_RED_MATTER_DESCRIPTION)
				.withCriterion("rm", InventoryChangeTrigger.Instance.forItems(PEItems.RED_MATTER))
				.register(advancementConsumer, PECore.rl("red_matter").toString());
		Advancement red_matter_block = childDisplay(red_matter, PEBlocks.RED_MATTER, PELang.ADVANCEMENTS_RED_MATTER_BLOCK, PELang.ADVANCEMENTS_RED_MATTER_BLOCK_DESCRIPTION)
				.withCriterion("rm_block", InventoryChangeTrigger.Instance.forItems(PEBlocks.RED_MATTER))
				.register(advancementConsumer, PECore.rl("red_matter_block").toString());
		childDisplay(red_matter_block, PEBlocks.RED_MATTER_FURNACE, PELang.ADVANCEMENTS_RED_MATTER_FURNACE, PELang.ADVANCEMENTS_RED_MATTER_FURNACE_DESCRIPTION)
				.withCriterion("rm_furnace", InventoryChangeTrigger.Instance.forItems(PEBlocks.RED_MATTER_FURNACE))
				.register(advancementConsumer, PECore.rl("red_matter_furnace").toString());
		//Branch 3
		Advancement dark_matter_block = childDisplay(root, PEBlocks.DARK_MATTER, PELang.ADVANCEMENTS_DARK_MATTER_BLOCK, PELang.ADVANCEMENTS_DARK_MATTER_BLOCK_DESCRIPTION)
				.withCriterion("dm_block", InventoryChangeTrigger.Instance.forItems(PEBlocks.DARK_MATTER))
				.register(advancementConsumer, PECore.rl("dark_matter_block").toString());
		childDisplay(dark_matter_block, PEBlocks.DARK_MATTER_FURNACE, PELang.ADVANCEMENTS_DARK_MATTER_FURNACE, PELang.ADVANCEMENTS_DARK_MATTER_FURNACE_DESCRIPTION)
				.withCriterion("dm_furnace", InventoryChangeTrigger.Instance.forItems(PEBlocks.DARK_MATTER_FURNACE))
				.register(advancementConsumer, PECore.rl("dark_matter_furnace").toString());
	}
}