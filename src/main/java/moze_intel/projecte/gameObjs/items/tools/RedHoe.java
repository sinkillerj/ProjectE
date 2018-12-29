package moze_intel.projecte.gameObjs.items.tools;

import net.minecraftforge.common.ToolType;

public class RedHoe extends DarkHoe
{
	public RedHoe(Builder builder)
	{
		super(builder, "rm_hoe", (byte)3, new String[]{});
		this.peToolMaterial = "rm_tools";
		this.toolClasses.add(ToolType.get("hoe"));
	}
}
