package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.material.Material;

public class RedShovel extends DarkShovel
{
	public RedShovel() 
	{
		super("rm_shovel", (byte)3, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.toolClasses.add("shovel");
		this.harvestMaterials.add(Material.GRASS);
		this.harvestMaterials.add(Material.GROUND);
		this.harvestMaterials.add(Material.SAND);
		this.harvestMaterials.add(Material.SNOW);
		this.harvestMaterials.add(Material.CLAY);
	}
}
