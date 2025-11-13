package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.items.KleinStar.KleinTier;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.InfestedBlock;
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
		addBags();
		addGear();
		addIgnoreMissing();
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
		tag(PETags.Items.DATA_COMPONENT_WHITELIST);
		tag(PETags.Items.CURIOS_BELT).add(
				PEItems.REPAIR_TALISMAN.get(),
				PEItems.WATCH_OF_FLOWING_TIME.get()
		);
		tag(PETags.Items.RELAYS).add(
				PEBlocks.RELAY.asItem(),
				PEBlocks.RELAY_MK2.asItem(),
				PEBlocks.RELAY_MK3.asItem()
		);
		tag(PETags.Items.COLLECTORS).add(
				PEBlocks.COLLECTOR.asItem(),
				PEBlocks.COLLECTOR_MK2.asItem(),
				PEBlocks.COLLECTOR_MK3.asItem()
		);
		tag(PETags.Items.MATTER_FURNACES).add(
				PEBlocks.DARK_MATTER_FURNACE.asItem(),
				PEBlocks.RED_MATTER_FURNACE.asItem()
		);
		IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> kleinStarBuilder = tag(PETags.Items.KLEIN_STARS);
		for (KleinTier tier : KleinTier.values()) {
			kleinStarBuilder.add(PEItems.getStar(tier).value());
		}
		tag(PETags.Items.CURIOS_KLEIN_STAR).addTag(PETags.Items.KLEIN_STARS);
		tag(PETags.Items.CURIOS_TRANSMUTATION_TABLET).add(PEItems.TRANSMUTATION_TABLET.get());
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
		tag(PETags.Items.PLANTABLE_SEEDS).addTags(
				ItemTags.VILLAGER_PLANTABLE_SEEDS,
				//Note: Try adding any seeds that aren't in the villager plantable ones, in case we are able to plant them
				Tags.Items.SEEDS
		);
		//Vanilla/Forge Tags
		tag(Tags.Items.TOOLS_SHEAR).add(
				PEItems.DARK_MATTER_SHEARS.get(),
				PEItems.RED_MATTER_SHEARS.get(),
				PEItems.RED_MATTER_KATAR.get()
		);
		tag(Tags.Items.CHESTS).add(
				PEBlocks.ALCHEMICAL_CHEST.asItem()
		);
		tag(Tags.Items.PLAYER_WORKSTATIONS_FURNACES).add(
				PEBlocks.DARK_MATTER_FURNACE.asItem(),
				PEBlocks.RED_MATTER_FURNACE.asItem()
		);
		tag(ItemTags.BEACON_PAYMENT_ITEMS).add(
				PEItems.DARK_MATTER.get(),
				PEItems.RED_MATTER.get()
		);
	}

	private void addIgnoreMissing() {
		IntrinsicTagAppender<Item> ignoreMissingEMC = tag(PETags.Items.IGNORE_MISSING_EMC).add(
				Items.DEBUG_STICK, Items.KNOWLEDGE_BOOK, Items.STRUCTURE_VOID, Items.FROGSPAWN, Items.PETRIFIED_OAK_SLAB, Items.REINFORCED_DEEPSLATE,
				Items.SPAWNER, Items.TRIAL_SPAWNER, Items.VAULT, Items.TRIAL_KEY, Items.OMINOUS_TRIAL_KEY,
				Items.ELYTRA, Items.TOTEM_OF_UNDYING,
				Items.EXPERIENCE_BOTTLE, Items.OMINOUS_BOTTLE,
				Items.DRAGON_HEAD, Items.PLAYER_HEAD, Items.WITHER_SKELETON_SKULL,
				Items.BEE_NEST, Items.FARMLAND,
				Items.COMMAND_BLOCK_MINECART,
				Items.BUDDING_AMETHYST, Items.SMALL_AMETHYST_BUD, Items.MEDIUM_AMETHYST_BUD, Items.LARGE_AMETHYST_BUD,
				//Blocks that have no emc because it is less than one:
				Items.STONE_SLAB, Items.COBBLESTONE_SLAB, Items.SMOOTH_STONE_SLAB, Items.STONE_BRICK_SLAB, Items.END_STONE_BRICK_SLAB,
				Items.GLASS_PANE, Items.CYAN_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.LIME_STAINED_GLASS_PANE, Items.MAGENTA_STAINED_GLASS_PANE,
				Items.PINK_STAINED_GLASS_PANE
		).addTags(Tags.Items.CLUSTERS, Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS);
		for (Item item : BuiltInRegistries.ITEM) {
			if (item instanceof SpawnEggItem || item instanceof MobBucketItem) {
				ignoreMissingEMC.add(item);
			} else if (item instanceof BlockItem blockItem) {
				Block block = blockItem.getBlock();
				if (block instanceof InfestedBlock || block instanceof HugeMushroomBlock) {
					ignoreMissingEMC.add(item);
				}
			}
		}
	}

	private void addBags() {
		IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> alchemicalBags = tag(PETags.Items.ALCHEMICAL_BAGS);
		for (DyeColor color : Constants.COLORS) {
			AlchemicalBag bag = PEItems.getBag(color);
			alchemicalBags.add(bag);
			tag(color.getDyedTag()).add(bag);
		}
	}

	@SuppressWarnings("unchecked")
	private void addGear() {
		addArmor();
		tag(Tags.Items.TOOLS).addTags(
				PETags.Items.TOOLS_HAMMERS,
				PETags.Items.TOOLS_KATARS,
				PETags.Items.TOOLS_MORNING_STARS
		);
		addTool(ItemTags.SWORDS, new ItemLike[]{
				PEItems.DARK_MATTER_SWORD,
				PEItems.RED_MATTER_SWORD
		}, ItemTags.SWORD_ENCHANTABLE, ItemTags.SHARP_WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE);
		addTool(ItemTags.AXES, new ItemLike[]{
				PEItems.DARK_MATTER_AXE,
				PEItems.RED_MATTER_AXE
		}, ItemTags.SHARP_WEAPON_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE);
		addTool(ItemTags.PICKAXES, new ItemLike[]{
				PEItems.DARK_MATTER_PICKAXE,
				PEItems.RED_MATTER_PICKAXE
		}, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE);
		addTool(ItemTags.SHOVELS, new ItemLike[]{
				PEItems.DARK_MATTER_SHOVEL,
				PEItems.RED_MATTER_SHOVEL
		}, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE);
		addTool(ItemTags.HOES, new ItemLike[]{
				PEItems.DARK_MATTER_HOE,
				PEItems.RED_MATTER_HOE
		}, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE);

		//Note: For our tool types these aren't added to any of the enchantable tags, but we remove them just in case someone else adds the base tags to an enchantable one
		addTool(PETags.Items.TOOLS_HAMMERS, new ItemLike[]{
				PEItems.DARK_MATTER_HAMMER,
				PEItems.RED_MATTER_HAMMER
		}, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE);
		addTool(PETags.Items.TOOLS_KATARS, new ItemLike[]{
				PEItems.RED_MATTER_KATAR
		}, ItemTags.SWORD_ENCHANTABLE, ItemTags.SHARP_WEAPON_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE);
		addTool(PETags.Items.TOOLS_MORNING_STARS, new ItemLike[]{
				PEItems.RED_MATTER_MORNING_STAR
		}, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE);

		tag(Tags.Items.MELEE_WEAPON_TOOLS).addTag(PETags.Items.TOOLS_KATARS);
		tag(Tags.Items.MINING_TOOL_TOOLS).addTags(
				PETags.Items.TOOLS_HAMMERS,
				PETags.Items.TOOLS_MORNING_STARS
		);
		tag(ItemTags.BREAKS_DECORATED_POTS).addTags(
				PETags.Items.TOOLS_HAMMERS,
				PETags.Items.TOOLS_KATARS,
				PETags.Items.TOOLS_MORNING_STARS
		);
	}

	private void addArmor() {
		addArmor(ItemTags.HEAD_ARMOR, ItemTags.HEAD_ARMOR_ENCHANTABLE,
				PEItems.DARK_MATTER_HELMET.get(),
				PEItems.RED_MATTER_HELMET.get(),
				PEItems.GEM_HELMET.get()
		);
		addArmor(ItemTags.CHEST_ARMOR, ItemTags.CHEST_ARMOR_ENCHANTABLE,
				PEItems.DARK_MATTER_CHESTPLATE.get(),
				PEItems.RED_MATTER_CHESTPLATE.get(),
				PEItems.GEM_CHESTPLATE.get()
		);
		addArmor(ItemTags.LEG_ARMOR, ItemTags.LEG_ARMOR_ENCHANTABLE,
				PEItems.DARK_MATTER_LEGGINGS.get(),
				PEItems.RED_MATTER_LEGGINGS.get(),
				PEItems.GEM_LEGGINGS.get()
		);
		addArmor(ItemTags.FOOT_ARMOR, ItemTags.FOOT_ARMOR_ENCHANTABLE,
				PEItems.DARK_MATTER_BOOTS.get(),
				PEItems.RED_MATTER_BOOTS.get(),
				PEItems.GEM_BOOTS.get()
		);
	}

	@SafeVarargs
	private void addTool(TagKey<Item> toolTag, ItemLike[] items, TagKey<Item>... enchantableTags) {
		IntrinsicTagAppender<Item> toolBuilder = tag(toolTag);
		for (ItemLike itemLike : items) {
			Item item = itemLike.asItem();
			toolBuilder.add(item);
			//Remove them from enchantment based tags
			for (TagKey<Item> enchantableTag : enchantableTags) {
				tag(enchantableTag).remove(item);
			}
		}
	}

	private void addArmor(TagKey<Item> armorTag, TagKey<Item> armorTagEnchantable, ItemLike... items) {
		IntrinsicTagAppender<Item> armorBuilder = tag(armorTag);
		IntrinsicTagAppender<Item> enchantableBuilder = tag(armorTagEnchantable);
		IntrinsicTagAppender<Item> durabilityEnchantable = tag(ItemTags.DURABILITY_ENCHANTABLE);
		IntrinsicTagAppender<Item> equippableEnchantable = tag(ItemTags.EQUIPPABLE_ENCHANTABLE);
		for (ItemLike itemLike : items) {
			Item item = itemLike.asItem();
			armorBuilder.add(item);
			//Remove them from enchantment based tags
			enchantableBuilder.remove(item);
			durabilityEnchantable.remove(item);
			equippableEnchantable.remove(item);
		}
	}

	private TagKey<Item> makeTag(TagKey<Item> tag, ItemLike item) {
		tag(tag).add(item.asItem());
		return tag;
	}
}
