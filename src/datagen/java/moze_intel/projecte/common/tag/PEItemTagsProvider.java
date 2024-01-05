package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.items.KleinStar.EnumKleinTier;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PEItemTagsProvider extends ItemTagsProvider {

	public PEItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		addGear();
		IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> alchemicalBags = tag(PETags.Items.ALCHEMICAL_BAGS);
		for (DyeColor color : DyeColor.values()) {
			alchemicalBags.add(PEItems.getBag(color));
		}
		tag(ItemTags.BOOKSHELF_BOOKS).add(PEItems.TOME_OF_KNOWLEDGE.get());
		tag(ItemTags.FREEZE_IMMUNE_WEARABLES).add(PEItems.GEM_CHESTPLATE.get());
		tag(PETags.Items.COLLECTOR_FUEL).add(
				Items.CHARCOAL,
				Items.REDSTONE,
				Items.REDSTONE_BLOCK,
				Items.COAL,
				Items.COAL_BLOCK,
				Items.GUNPOWDER,
				Items.GLOWSTONE_DUST,
				Items.BLAZE_POWDER,
				Items.GLOWSTONE,
				PEItems.ALCHEMICAL_COAL.get(),
				PEBlocks.ALCHEMICAL_COAL.asItem(),
				PEItems.MOBIUS_FUEL.get(),
				PEBlocks.MOBIUS_FUEL.asItem(),
				PEItems.AETERNALIS_FUEL.get(),
				PEBlocks.AETERNALIS_FUEL.asItem()
		);
		tag(PETags.Items.COVALENCE_DUST).add(
				PEItems.LOW_COVALENCE_DUST.get(),
				PEItems.MEDIUM_COVALENCE_DUST.get(),
				PEItems.HIGH_COVALENCE_DUST.get()
		);
		tag(PETags.Items.NBT_WHITELIST);
		tag(PETags.Items.CURIOS_BELT).add(
				PEItems.REPAIR_TALISMAN.get(),
				PEItems.WATCH_OF_FLOWING_TIME.get()
		);
		IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> kleinStarBuilder = tag(PETags.Items.CURIOS_KLEIN_STAR);
		for (EnumKleinTier tier : EnumKleinTier.values()) {
			kleinStarBuilder.add(PEItems.getStar(tier).value());
		}
		tag(PETags.Items.CURIOS_NECKLACE).add(
				PEItems.BODY_STONE.get(),
				PEItems.EVERTIDE_AMULET.get(),
				PEItems.LIFE_STONE.get(),
				PEItems.SOUL_STONE.get(),
				PEItems.VOLCANITE_AMULET.get()
		);
		tag(PETags.Items.CURIOS_RING).add(
				PEItems.ARCANA_RING.get(),
				PEItems.BLACK_HOLE_BAND.get(),
				PEItems.GEM_OF_ETERNAL_DENSITY.get(),
				PEItems.IGNITION_RING.get(),
				PEItems.SWIFTWOLF_RENDING_GALE.get(),
				PEItems.VOID_RING.get(),
				PEItems.ZERO_RING.get()
		);
		//Vanilla/Forge Tags
		tag(Tags.Items.SHEARS).add(
				PEItems.DARK_MATTER_SHEARS.get(),
				PEItems.RED_MATTER_SHEARS.get(),
				PEItems.RED_MATTER_KATAR.get()
		);
		tag(Tags.Items.CHESTS).add(
				PEBlocks.ALCHEMICAL_CHEST.asItem()
		);
		tag(ItemTags.BEACON_PAYMENT_ITEMS).add(
				PEItems.DARK_MATTER.get(),
				PEItems.RED_MATTER.get()
		);
	}

	@SuppressWarnings("unchecked")
	private void addGear() {
		addArmor();
		tag(Tags.Items.TOOLS).addTags(
				PETags.Items.TOOLS_HAMMERS,
				PETags.Items.TOOLS_KATARS,
				PETags.Items.TOOLS_MORNING_STARS
		);
		tag(ItemTags.SWORDS).addTags(
				makeTag(PETags.Items.TOOLS_SWORDS_DARK_MATTER, PEItems.DARK_MATTER_SWORD),
				makeTag(PETags.Items.TOOLS_SWORDS_RED_MATTER, PEItems.RED_MATTER_SWORD)
		);
		tag(ItemTags.AXES).addTags(
				makeTag(PETags.Items.TOOLS_AXES_DARK_MATTER, PEItems.DARK_MATTER_AXE),
				makeTag(PETags.Items.TOOLS_AXES_RED_MATTER, PEItems.RED_MATTER_AXE)
		);
		tag(ItemTags.PICKAXES).addTags(
				makeTag(PETags.Items.TOOLS_PICKAXES_DARK_MATTER, PEItems.DARK_MATTER_PICKAXE),
				makeTag(PETags.Items.TOOLS_PICKAXES_RED_MATTER, PEItems.RED_MATTER_PICKAXE)
		);
		tag(ItemTags.SHOVELS).addTags(
				makeTag(PETags.Items.TOOLS_SHOVELS_DARK_MATTER, PEItems.DARK_MATTER_SHOVEL),
				makeTag(PETags.Items.TOOLS_SHOVELS_RED_MATTER, PEItems.RED_MATTER_SHOVEL)
		);
		tag(ItemTags.HOES).addTags(
				makeTag(PETags.Items.TOOLS_HOES_DARK_MATTER, PEItems.DARK_MATTER_HOE),
				makeTag(PETags.Items.TOOLS_HOES_RED_MATTER, PEItems.RED_MATTER_HOE)
		);
		tag(PETags.Items.TOOLS_HAMMERS).addTags(
				makeTag(PETags.Items.TOOLS_HAMMERS_DARK_MATTER, PEItems.DARK_MATTER_HAMMER),
				makeTag(PETags.Items.TOOLS_HAMMERS_RED_MATTER, PEItems.RED_MATTER_HAMMER)
		);
		tag(PETags.Items.TOOLS_KATARS).addTag(
				makeTag(PETags.Items.TOOLS_KATARS_RED_MATTER, PEItems.RED_MATTER_KATAR)
		);
		tag(PETags.Items.TOOLS_MORNING_STARS).addTag(
				makeTag(PETags.Items.TOOLS_MORNING_STARS_RED_MATTER, PEItems.RED_MATTER_MORNING_STAR)
		);
	}

	@SuppressWarnings("unchecked")
	private void addArmor() {
		tag(ItemTags.TRIMMABLE_ARMOR).add(
				PEItems.GEM_HELMET.get(),
				PEItems.GEM_CHESTPLATE.get(),
				PEItems.GEM_LEGGINGS.get(),
				PEItems.GEM_BOOTS.get(),

				PEItems.DARK_MATTER_HELMET.get(),
				PEItems.DARK_MATTER_CHESTPLATE.get(),
				PEItems.DARK_MATTER_LEGGINGS.get(),
				PEItems.DARK_MATTER_BOOTS.get(),

				PEItems.RED_MATTER_HELMET.get(),
				PEItems.RED_MATTER_CHESTPLATE.get(),
				PEItems.RED_MATTER_LEGGINGS.get(),
				PEItems.RED_MATTER_BOOTS.get()
		);
		tag(Tags.Items.ARMORS_HELMETS).add(
				PEItems.GEM_HELMET.get()
		).addTags(
				makeTag(PETags.Items.ARMORS_HELMETS_DARK_MATTER, PEItems.DARK_MATTER_HELMET),
				makeTag(PETags.Items.ARMORS_HELMETS_RED_MATTER, PEItems.RED_MATTER_HELMET)
		);
		tag(Tags.Items.ARMORS_CHESTPLATES).add(
				PEItems.GEM_CHESTPLATE.get()
		).addTags(
				makeTag(PETags.Items.ARMORS_CHESTPLATES_DARK_MATTER, PEItems.DARK_MATTER_CHESTPLATE),
						makeTag(PETags.Items.ARMORS_CHESTPLATES_RED_MATTER, PEItems.RED_MATTER_CHESTPLATE)
		);
		tag(Tags.Items.ARMORS_LEGGINGS).add(
				PEItems.GEM_LEGGINGS.get()
		).addTags(
				makeTag(PETags.Items.ARMORS_LEGGINGS_DARK_MATTER, PEItems.DARK_MATTER_LEGGINGS),
				makeTag(PETags.Items.ARMORS_LEGGINGS_RED_MATTER, PEItems.RED_MATTER_LEGGINGS)
		);
		tag(Tags.Items.ARMORS_BOOTS).add(
				PEItems.GEM_BOOTS.get()
		).addTags(
				makeTag(PETags.Items.ARMORS_BOOTS_DARK_MATTER, PEItems.DARK_MATTER_BOOTS),
				makeTag(PETags.Items.ARMORS_BOOTS_RED_MATTER, PEItems.RED_MATTER_BOOTS)
		);
	}
	
	private TagKey<Item> makeTag(TagKey<Item> tag, ItemLike item) {
		tag(tag).add(item.asItem());
		return tag;
	}
}
