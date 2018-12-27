package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class RedAxe extends DarkAxe
{
	public RedAxe(Builder builder)
	{
		super(builder, "rm_axe", (byte)3, new String[]{});
		this.peToolMaterial = "rm_tools";
		this.toolClasses.add(ToolType.AXE);
		this.harvestMaterials.add(Material.WOOD);
		this.harvestMaterials.add(Material.PLANTS);
		this.harvestMaterials.add(Material.VINE);
	}
}
