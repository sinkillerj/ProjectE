package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.material.Material;

public class RedHammer extends DarkHammer
{
	public RedHammer() 
	{
		super("rm_hammer", (byte)3, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "hammer";
		this.harvestMaterials.add(Material.IRON);
		this.harvestMaterials.add(Material.ANVIL);
		this.harvestMaterials.add(Material.ROCK);

		this.secondaryClasses.add("pickaxe");
		this.secondaryClasses.add("chisel");
	}
}
