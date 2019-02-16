package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.block.material.Material;

public class RedShovel extends DarkShovel
{
	public RedShovel(Properties props)
	{
		super(props, (byte)3, new String[]{});
		this.peToolMaterial = EnumMatterType.RED_MATTER;
		this.harvestMaterials.add(Material.GRASS);
		this.harvestMaterials.add(Material.GROUND);
		this.harvestMaterials.add(Material.SAND);
		this.harvestMaterials.add(Material.SNOW);
		this.harvestMaterials.add(Material.CLAY);
	}
}
