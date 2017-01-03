package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DarkHoe extends PEToolBase
{
	public DarkHoe() 
	{
		super("dm_hoe", (byte)2, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.toolClasses.add("hoe");
	}

	// Only for RedHoe
	protected DarkHoe(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing sideHit, float par8, float par9, float par10)
	{
		tillAOE(player.getHeldItem(hand), player, world, pos, sideHit, 0);
		return EnumActionResult.SUCCESS;
	}
}
