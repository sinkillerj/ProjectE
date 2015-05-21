package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DarkHoe extends PEToolBase
{
	public DarkHoe() 
	{
		super("dm_hoe", (byte)2, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.pePrimaryToolClass = "hoe";
	}

	// Only for RedHoe
	protected DarkHoe(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing sideHit, float par8, float par9, float par10)
	{
		tillAOE(stack, player, world, pos, sideHit, 0);
		return true;
	}
}
