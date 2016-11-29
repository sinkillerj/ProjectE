package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.material.Material;

public class RedAxe extends DarkAxe
{
	public RedAxe()
	{
		super("rm_axe", (byte)3, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.toolClasses.add("axe");
		this.harvestMaterials.add(Material.WOOD);
		this.harvestMaterials.add(Material.PLANTS);
		this.harvestMaterials.add(Material.VINE);
	}
}
