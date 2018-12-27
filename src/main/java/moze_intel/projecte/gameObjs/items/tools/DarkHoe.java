package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DarkHoe extends PEToolBase
{
	public DarkHoe(Builder builder)
	{
		super(builder, "dm_hoe", (byte)2, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.toolClasses.add("hoe");
	}

	// Only for RedHoe
	protected DarkHoe(Builder builder, String name, byte numCharges, String[] modeDesc)
	{
		super(builder, name, numCharges, modeDesc);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		tillAOE(ctx.getItem(), ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getFace(), 0);
		return EnumActionResult.SUCCESS;
	}
}
