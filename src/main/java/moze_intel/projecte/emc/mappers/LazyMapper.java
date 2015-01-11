package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LazyMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	IMappingCollector<NormalizedSimpleStack, Long> mapper;

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper) {
		this.mapper = mapper;
		addMapping(new ItemStack(Blocks.cobblestone), 1);
		addMapping(new ItemStack(Blocks.stone), 1);
		addMapping(new ItemStack(Blocks.netherrack), 1);
		addMapping(new ItemStack(Blocks.dirt), 1);
		addMapping(new ItemStack(Blocks.grass), 1);
		addMapping(new ItemStack(Blocks.mycelium), 1);
		addMapping(new ItemStack(Blocks.leaves), 1);
		addMapping(new ItemStack(Blocks.leaves2), 1);
		addMapping(new ItemStack(Blocks.sand, 1, 0), 1);
		addMapping(new ItemStack(Blocks.sand, 1, 1), 1);
		addMapping(new ItemStack(Blocks.snow), 1);
		addMapping(new ItemStack(Blocks.ice), 1);
		addMapping(new ItemStack(Blocks.deadbush), 1);
		addMapping(new ItemStack(Blocks.gravel), 4);
		addMapping(new ItemStack(Blocks.cactus), 8);
		addMapping(new ItemStack(Blocks.vine), 8);
		addMapping(new ItemStack(Blocks.torch), 9);
		addMapping(new ItemStack(Blocks.web), 12);
		addMapping(new ItemStack(Items.wheat_seeds), 16);
		addMapping(new ItemStack(Items.melon), 16);
		addMapping(new ItemStack(Items.clay_ball), 16);
		addMapping(new ItemStack(Blocks.waterlily), 16);

		for (int i = 0; i <= 8; i++) {
			addMapping(new ItemStack(Blocks.red_flower, 1, i), 16);
		}

		addMapping(new ItemStack(Blocks.yellow_flower), 16);
		addMapping(new ItemStack(Items.wheat), 24);
		addMapping(new ItemStack(Items.nether_wart), 24);
		addMapping(new ItemStack(Items.stick), 4);
		addMapping(new ItemStack(Blocks.red_mushroom), 32);
		addMapping(new ItemStack(Blocks.brown_mushroom), 32);
		addMapping(new ItemStack(Items.reeds), 32);
		addMapping(new ItemStack(Blocks.soul_sand), 49);
		addMapping(new ItemStack(Blocks.obsidian), 64);

		for (int i = 0; i < 16; i++) {
			addMapping(new ItemStack(Blocks.stained_hardened_clay, 1, i), 64);
		}

		addMapping(new ItemStack(Items.apple), 128);
		addMapping(new ItemStack(Items.dye, 1, 3), 128);
		addMapping(new ItemStack(Blocks.pumpkin), 144);
		addMapping(new ItemStack(Items.bone), 144);
		addMapping(new ItemStack(Blocks.mossy_cobblestone), 145);
		addMapping(new ItemStack(Items.wooden_sword), 20);
		addMapping(new ItemStack(Items.wooden_hoe), 24);
		addMapping(new ItemStack(Items.wooden_axe), 32);
		addMapping(new ItemStack(Items.wooden_shovel), 16);
		addMapping(new ItemStack(Items.stone_shovel), 7);
		addMapping(new ItemStack(Items.stone_sword), 6);
		addMapping(new ItemStack(Items.stone_axe), 11);
		addMapping(new ItemStack(Items.stone_hoe), 10);
		addMapping(new ItemStack(Items.saddle), 192);
		addMapping(new ItemStack(Items.record_11), 2048);
		addMapping(new ItemStack(Items.record_13), 2048);
		addMapping(new ItemStack(Items.record_blocks), 2048);
		addMapping(new ItemStack(Items.record_cat), 2048);
		addMapping(new ItemStack(Items.record_chirp), 2048);
		addMapping(new ItemStack(Items.record_far), 2048);
		addMapping(new ItemStack(Items.record_mall), 2048);
		addMapping(new ItemStack(Items.record_mellohi), 2048);
		addMapping(new ItemStack(Items.record_stal), 2048);
		addMapping(new ItemStack(Items.record_strad), 2048);
		addMapping(new ItemStack(Items.record_wait), 2048);
		addMapping(new ItemStack(Items.record_ward), 2048);
		addMapping(new ItemStack(Items.string), 12);
		addMapping(new ItemStack(Items.rotten_flesh), 32);
		addMapping(new ItemStack(Items.slime_ball), 32);
		addMapping(new ItemStack(Items.egg), 32);
		addMapping(new ItemStack(Items.feather), 48);
		addMapping(new ItemStack(Items.leather), 64);
		addMapping(new ItemStack(Items.spider_eye), 128);
		addMapping(new ItemStack(Items.gunpowder), 192);
		addMapping(new ItemStack(Items.ender_pearl), 1024);
		addMapping(new ItemStack(Items.blaze_rod), 1536);
		addMapping(new ItemStack(Items.ghast_tear), 4096);
		addMapping(new ItemStack(Blocks.dragon_egg), 262144);
		addMapping(new ItemStack(Items.porkchop), 64);
		addMapping(new ItemStack(Items.beef), 64);
		addMapping(new ItemStack(Items.chicken), 64);
		addMapping(new ItemStack(Items.fish), 64);
		addMapping(new ItemStack(Items.carrot), 64);
		addMapping(new ItemStack(Items.potato), 64);
		addMapping(new ItemStack(Items.iron_ingot), 256);
		addMapping(new ItemStack(Items.gold_ingot), 2048);
		addMapping(new ItemStack(Items.diamond), 8192);
		addMapping(new ItemStack(Items.flint), 4);
		addMapping(new ItemStack(Items.coal), 128);
		addMapping(new ItemStack(Items.redstone), 64);
		addMapping(new ItemStack(Items.glowstone_dust), 384);
		addMapping(new ItemStack(Items.quartz), 256);
		addMapping(new ItemStack(Items.dye, 1, 4), 864);

		for (int i = 0; i < 15; i++) {
			if (i == 3 || i == 4) {
				continue;
			}

			addMapping(new ItemStack(Items.dye, 1, i), 16);
		}

		addMapping(new ItemStack(Items.enchanted_book), 2048);
		addMapping(new ItemStack(Items.emerald), 16384);

		addMapping(new ItemStack(Items.nether_star), 139264);
		addMapping(new ItemStack(Items.iron_horse_armor), 1280);
		addMapping(new ItemStack(Items.golden_horse_armor), 1024);
		addMapping(new ItemStack(Items.diamond_horse_armor), 40960);
		addMapping(new ItemStack(Blocks.tallgrass), 1);
		addMapping(new ItemStack(Blocks.packed_ice), 4);
		addMapping(new ItemStack(Items.snowball), 1);
		addMapping(new ItemStack(Items.filled_map), 1472);
		addMapping(new ItemStack(Items.blaze_powder), 768);
		addMapping(new ItemStack(Items.dye, 1, 15), 48);

		addMapping("appliedenergistics2:item.ItemMultiMaterial", 1, 256);
	}

	protected void addMapping(ItemStack itemStack, int value) {
		this.mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(itemStack), (long)value, IMappingCollector.FixedValue.FixAndInherit);
	}

	protected void addMapping(String unlocalName, int meta, int value) {
		ItemStack stack = Utils.getStackFromString(unlocalName, meta);

		if (stack != null) {
			addMapping(stack, value);
		}
	}

}
