package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.block.material.Material;

public class RedShears extends DarkShears
{
	public RedShears(Properties props)
	{
		super(props, (byte) 3, new String[]{});
		this.peToolMaterial = EnumMatterType.RED_MATTER;
		this.harvestMaterials.add(Material.WEB);
		this.harvestMaterials.add(Material.CIRCUITS);
	}
}
