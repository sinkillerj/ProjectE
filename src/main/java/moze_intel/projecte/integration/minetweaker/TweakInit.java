package moze_intel.projecte.integration.minetweaker;

import minetweaker.MineTweakerAPI;
import moze_intel.projecte.utils.PELogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TweakInit
{
	public static void init()
	{
		MineTweakerAPI.registerClass(PhiloStone.class);
		MineTweakerAPI.registerClass(KleinStar.class);

        File parent = new File("scripts");

        if (!parent.isDirectory()) {
            parent.mkdir();
        }

        File script = new File(parent, "projecte_default.zs");

        try {
            Files.write(script.toPath(), generateLines());
        } catch (IOException ex) {
            PELogger.logWarn("Failed to write sample script");
        }
	}

	private static List<String> generateLines()
	{
		List<String> defaultLines = new ArrayList<>();

		defaultLines.add("import mods.projecte.PhiloStone;");
		defaultLines.add("import mods.projecte.KleinStar;");
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
		defaultLines.add("");
		defaultLines.add("");
		defaultLines.add("#KleinStar.addShaped(<ProjectE:item.pe_klein_star:2>, [[<ProjectE:item.pe_klein_star:1>, <minecraft:dirt>, <ProjectE:item.pe_klein_star:1>], [<minecraft:dirt>, <ProjectE:item.pe_klein_star:1>, <minecraft:dirt>], [<ProjectE:item.pe_klein_star:1>, <minecraft:dirt>, <ProjectE:item.pe_klein_star:1>]]);");
		defaultLines.add("#KleinStar.addShapeless(<ProjectE:item.pe_klein_star:2>, [<ProjectE:item.pe_klein_star:1>, <ProjectE:item.pe_klein_star:1>]);");
		defaultLines.add("");
		defaultLines.add("#KleinStar.removeRecipe(<ProjectE:item.pe_klein_star:2>);");
		defaultLines.add("");


		return defaultLines;
	}
}