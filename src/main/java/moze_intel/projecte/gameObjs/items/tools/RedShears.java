package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class RedShears extends DarkShears
{
	public RedShears(Builder builder)
	{
		super(builder, "rm_shears", (byte) 3, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.toolClasses.add(ToolType.get("shears"));
		this.harvestMaterials.add(Material.WEB);
		this.harvestMaterials.add(Material.CIRCUITS);
	}
}
