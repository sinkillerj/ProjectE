package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.material.Material;

public class RedShears extends DarkShears
{
	public RedShears()
	{
		super("rm_shears", (byte) 3, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "shears";
		this.harvestMaterials.add(Material.web);
		this.harvestMaterials.add(Material.circuits);
	}
}
