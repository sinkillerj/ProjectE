package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.api.state.enums.EnumMatterType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class RedShears extends DarkShears
{
	public RedShears(Builder builder)
	{
		super(builder, (byte) 3, new String[]{});
		this.peToolMaterial = EnumMatterType.RED_MATTER;
		this.harvestMaterials.add(Material.WEB);
		this.harvestMaterials.add(Material.CIRCUITS);
	}
}
