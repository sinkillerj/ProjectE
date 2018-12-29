package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class RedShovel extends DarkShovel
{
	public RedShovel(Builder builder)
	{
		super(builder, "rm_shovel", (byte)3, new String[]{});
		this.peToolMaterial = "rm_tools";
		this.toolClasses.add(ToolType.SHOVEL);
		this.harvestMaterials.add(Material.GRASS);
		this.harvestMaterials.add(Material.GROUND);
		this.harvestMaterials.add(Material.SAND);
		this.harvestMaterials.add(Material.SNOW);
		this.harvestMaterials.add(Material.CLAY);
	}
}
