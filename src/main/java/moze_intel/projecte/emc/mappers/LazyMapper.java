package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.ItemHelper;

import com.google.common.collect.ImmutableMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class LazyMapper implements IEMCMapper<NormalizedSimpleStack, Integer> {

	IMappingCollector<NormalizedSimpleStack, Integer> mapper;

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		this.mapper = mapper;
		addMapping(new ItemStack(Blocks.COBBLESTONE), 1);
		addMapping(new ItemStack(Blocks.STONE), 1);
		addMapping(new ItemStack(Blocks.END_STONE), 1);
		addMapping(new ItemStack(Blocks.NETHERRACK), 1);
		addMapping(new ItemStack(Blocks.DIRT), 1);
		addMapping(new ItemStack(Blocks.DIRT, 1, 2), 2);
		mapper.addConversion(1, NormalizedSimpleStack.getFor(Blocks.GRASS), ImmutableMap.of(NormalizedSimpleStack.getFor(Blocks.DIRT), 2));
		addMapping(new ItemStack(Blocks.MYCELIUM), 2);
		addMapping(new ItemStack(Blocks.LEAVES), 1);
		addMapping(new ItemStack(Blocks.LEAVES2), 1);
		addMapping(new ItemStack(Blocks.SAND, 1, 0), 1);
		addMapping(new ItemStack(Blocks.SAND, 1, 1), 1);
		addMapping(new ItemStack(Blocks.SNOW), 1);
		addMapping(new ItemStack(Blocks.ICE), 1);
		addMapping(new ItemStack(Blocks.DEADBUSH), 1);
		addMapping(new ItemStack(Blocks.GRAVEL), 4);
		addMapping(new ItemStack(Blocks.CACTUS), 8);
		addMapping(new ItemStack(Blocks.VINE), 8);
		addMapping(new ItemStack(Blocks.TORCH), 9);
		addMapping(new ItemStack(Blocks.WEB), 12);
		addMapping(new ItemStack(Items.WHEAT_SEEDS), 16);
		addMapping(new ItemStack(Items.MELON), 16);
		addMapping(new ItemStack(Items.CLAY_BALL), 16);
		addMapping(new ItemStack(Blocks.WATERLILY), 16);
		addMapping(new ItemStack(Blocks.STONE, 1, 1), 16);
		addMapping(new ItemStack(Blocks.STONE, 1, 3), 16);
		addMapping(new ItemStack(Blocks.STONE, 1, 5), 16);

		for (int i = 0; i <= 8; i++) {
			addMapping(new ItemStack(Blocks.RED_FLOWER, 1, i), 16);
		}

		for (int i = 0; i <= 5; i++) {
			if (i == 2 || i == 3) {
				continue;
			}

			addMapping(new ItemStack(Blocks.DOUBLE_PLANT, 1, i), 32);
		}

		addMapping(new ItemStack(Blocks.YELLOW_FLOWER), 16);
		addMapping(new ItemStack(Items.WHEAT), 24);
		addMapping(new ItemStack(Items.NETHER_WART), 24);
		addMapping(new ItemStack(Items.STICK), 4);
		addMapping(new ItemStack(Blocks.RED_MUSHROOM), 32);
		addMapping(new ItemStack(Blocks.BROWN_MUSHROOM), 32);
		addMapping(new ItemStack(Items.REEDS), 32);
		addMapping(new ItemStack(Blocks.SOUL_SAND), 49);
		addMapping(new ItemStack(Blocks.OBSIDIAN), 64);

		for (int i = 0; i < 16; i++) {
			addMapping(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, i), 64);
		}

		addMapping(new ItemStack(Items.APPLE), 128);
		//Cocoa beans
		addMapping(new ItemStack(Items.DYE, 1, 3), 128);
		addMapping(new ItemStack(Blocks.SPONGE), 128);
		addMapping(new ItemStack(Blocks.SPONGE, 1, 1), 128);
		addMapping(new ItemStack(Blocks.PUMPKIN), 144);
		addMapping(new ItemStack(Items.BONE), 144);

		mapper.addConversion(1, NormalizedSimpleStack.getFor(Blocks.MOSSY_COBBLESTONE), ImmutableMap.of(NormalizedSimpleStack.getFor(Blocks.COBBLESTONE), 2));
		//Mossy Stone Bricks
		mapper.addConversion(1, NormalizedSimpleStack.getFor(new ItemStack(Blocks.STONEBRICK, 1, 1)), ImmutableMap.of(NormalizedSimpleStack.getFor(Blocks.STONEBRICK), 2));
		addMapping(new ItemStack(Blocks.STONEBRICK, 1, 2), 1);
		addMapping(new ItemStack(Blocks.STONEBRICK, 1, 3), 1);
		addMapping(new ItemStack(Items.SADDLE), 192);
		addMapping(new ItemStack(Items.RECORD_11), 2048);
		addMapping(new ItemStack(Items.RECORD_13), 2048);
		addMapping(new ItemStack(Items.RECORD_BLOCKS), 2048);
		addMapping(new ItemStack(Items.RECORD_CAT), 2048);
		addMapping(new ItemStack(Items.RECORD_CHIRP), 2048);
		addMapping(new ItemStack(Items.RECORD_FAR), 2048);
		addMapping(new ItemStack(Items.RECORD_MALL), 2048);
		addMapping(new ItemStack(Items.RECORD_MELLOHI), 2048);
		addMapping(new ItemStack(Items.RECORD_STAL), 2048);
		addMapping(new ItemStack(Items.RECORD_STRAD), 2048);
		addMapping(new ItemStack(Items.RECORD_WAIT), 2048);
		addMapping(new ItemStack(Items.RECORD_WARD), 2048);
		addMapping(new ItemStack(Items.STRING), 12);
		addMapping(new ItemStack(Items.ROTTEN_FLESH), 32);
		addMapping(new ItemStack(Items.SLIME_BALL), 32);
		addMapping(new ItemStack(Items.EGG), 32);
		addMapping(new ItemStack(Items.FEATHER), 48);
		addMapping(new ItemStack(Items.RABBIT_HIDE), 16);
		addMapping(new ItemStack(Items.SPIDER_EYE), 128);
		addMapping(new ItemStack(Items.GUNPOWDER), 192);
		addMapping(new ItemStack(Items.ENDER_PEARL), 1024);
		addMapping(new ItemStack(Items.BLAZE_ROD), 1536);
		addMapping(new ItemStack(Items.GHAST_TEAR), 4096);
		addMapping(new ItemStack(Blocks.DRAGON_EGG), 262144);
		addMapping(new ItemStack(Items.PORKCHOP), 64);
		addMapping(new ItemStack(Items.BEEF), 64);
		addMapping(new ItemStack(Items.CHICKEN), 64);
		addMapping(new ItemStack(Items.RABBIT), 64);

		for (int i = 0; i < 4; i++) {
			addMapping(new ItemStack(Items.FISH, 1, i), 64);
		}

		addMapping(new ItemStack(Items.CARROT), 64);
		addMapping(new ItemStack(Items.POTATO), 64);
		addMapping(new ItemStack(Items.POISONOUS_POTATO), 64);
		addMapping(new ItemStack(Items.IRON_INGOT), 256);
		addMapping(new ItemStack(Items.GOLD_INGOT), 2048);
		addMapping(new ItemStack(Items.DIAMOND), 8192);
		addMapping(new ItemStack(Items.FLINT), 4);
		addMapping(new ItemStack(Items.COAL), 128);
		addMapping(new ItemStack(Items.REDSTONE), 64);
		addMapping(new ItemStack(Items.GLOWSTONE_DUST), 384);
		addMapping(new ItemStack(Items.QUARTZ), 256);
		addMapping(new ItemStack(Items.PRISMARINE_SHARD), 256);
		addMapping(new ItemStack(Items.PRISMARINE_CRYSTALS), 512);
		//Lapis Lazuli
		addMapping(new ItemStack(Items.DYE, 1, 4), 864);

		//ink sac
		addMapping(new ItemStack(Items.DYE, 1, 0), 16);

		addMapping(new ItemStack(Items.ENCHANTED_BOOK), 2048);
		addMapping(new ItemStack(Items.EMERALD), 16384);

		addMapping(new ItemStack(Items.NETHER_STAR), 139264);
		mapper.addConversion(1, NormalizedSimpleStack.getFor(Items.IRON_HORSE_ARMOR), ImmutableMap.of(NormalizedSimpleStack.getFor(Items.IRON_INGOT), 8));
		mapper.addConversion(1, NormalizedSimpleStack.getFor(Items.GOLDEN_HORSE_ARMOR), ImmutableMap.of(NormalizedSimpleStack.getFor(Items.GOLD_INGOT), 8));
		addMapping(new ItemStack(Items.DIAMOND_HORSE_ARMOR), 40960);
		addMapping(new ItemStack(Blocks.TALLGRASS), 1);
		addMapping(new ItemStack(Blocks.TALLGRASS, 1, 1), 1);
		addMapping(new ItemStack(Blocks.TALLGRASS, 1, 2), 1);
		addMapping(new ItemStack(Blocks.DOUBLE_PLANT, 1, 2), 1);
		addMapping(new ItemStack(Blocks.DOUBLE_PLANT, 1, 3), 1);
		addMapping(new ItemStack(Blocks.PACKED_ICE), 4);
		addMapping(new ItemStack(Items.SNOWBALL), 1);
		addMapping(new ItemStack(Items.FILLED_MAP), 1472);

		addMapping("appliedenergistics2:item.ItemMultiMaterial", 1, 256);
	}

	protected void addMapping(ItemStack itemStack, int value) {
		this.mapper.setValueBefore(NormalizedSimpleStack.getFor(itemStack), value);
	}

	protected void addMapping(String unlocalName, int meta, int value) {
		ItemStack stack = ItemHelper.getStackFromString(unlocalName, meta);

		if (stack != null) {
			addMapping(stack, value);
		}
	}

	@Override
	public String getName() {
		return "LazyMapper";
	}

	@Override
	public String getDescription() {
		return "Default values for Items";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}
