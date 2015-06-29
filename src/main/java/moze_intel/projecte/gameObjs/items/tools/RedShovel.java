package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.material.Material;

public class RedShovel extends DarkShovel
{
	public RedShovel() 
	{
		super("rm_shovel", (byte)3, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "shovel";
		this.harvestMaterials.add(Material.grass);
		this.harvestMaterials.add(Material.ground);
		this.harvestMaterials.add(Material.sand);
		this.harvestMaterials.add(Material.snow);
		this.harvestMaterials.add(Material.clay);
	}
}
