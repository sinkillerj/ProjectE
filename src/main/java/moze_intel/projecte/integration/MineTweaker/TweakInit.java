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
		MineTweakerAPI.registerClass(KleinStar.class);

		FileHelper.writeDefaultFile("projecte_default.zs", "scripts", generateLines());
	}

	private static List<String> generateLines()
	{
		List<String> defaultLines = new ArrayList<String>();

		defaultLines.add("import mods.projecte.PhiloStone;");
		defaultLines.add("");
		defaultLines.add("#Example script for projecte");
		defaultLines.add("");
		defaultLines.add("#addPhiloSmelting(output, input, (optional) fuel), fuel can be any item. Defaults to coal");
		defaultLines.add("#PhiloStone.addPhiloSmelting(<minecraft:stone_pickaxe>, <minecraft:iron_ingot>);");
		defaultLines.add("#PhiloStone.addPhiloSmelting(<minecraft:stone_pickaxe>, <minecraft:iron_ingot>, <minecraft:coal>);");
		defaultLines.add("#PhiloStone.addPhiloSmelting(<minecraft:stone_pickaxe>, <minecraft:iron_ingot>, <minecraft:stone>);");
		defaultLines.add("");
		defaultLines.add("#removePhiloSmelting(output)");
		defaultLines.add("#PhiloStone.removePhiloSmelting(<minecraft:stone_pickaxe>);");
		defaultLines.add("");
		defaultLines.add("#addWorldTransmutation(output,(optional)sneakOutput, input), two or three parameters");
		defaultLines.add("#PhiloStone.removeWorldTransmutation(<minecraft:sand>, <minecraft:cobblestone>, <minecraft:grass>);");
		defaultLines.add("#PhiloStone.addWorldTransmutation(<minecraft:obsidian>, <minecraft:cobblestone>, <minecraft:grass>);");
		defaultLines.add("# or");
		defaultLines.add("#PhiloStone.addWorldTransmutation(<minecraft:obsidian>, <minecraft:grass>);");


		return defaultLines;
	}
}
