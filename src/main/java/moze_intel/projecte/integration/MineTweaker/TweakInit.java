package moze_intel.projecte.integration.MineTweaker;

import minetweaker.MineTweakerAPI;
import moze_intel.projecte.utils.FileHelper;

import java.util.ArrayList;
import java.util.List;

public class TweakInit
{


	public static void init()
	{
		MineTweakerAPI.registerClass(PhiloStone.class);

		FileHelper.writeDefaultFile("projecte_default.zs", "scripts/projecte", generateLines());
	}

	private static List<String> generateLines()
	{
		List<String> defaultLines = new ArrayList<String>();

		defaultLines.add("import mods.projecte.PhiloStone");
		defaultLines.add("");
		defaultLines.add("#Example script for projecte");
		defaultLines.add("");
		defaultLines.add("#addPhiloSmelting(output, input, (optional) fuel), fuel can be any item. Defaults to coal");
		defaultLines.add("#PhiloStone.addPhiloSmelting(<minecraft:stone_pickaxe>, <minecraft:iron_ingot>);");
		defaultLines.add("#PhiloStone.addPhiloSmelting(<minecraft:stone_pickaxe>, <minecraft:iron_ingot>, <minecraft:coal>);");
		defaultLines.add("#PhiloStone.addPhiloSmelting(<minecraft:stone_pickaxe>, <minecraft:iron_ingot>, <minecraft:stone>);");

		return defaultLines;
	}
}
