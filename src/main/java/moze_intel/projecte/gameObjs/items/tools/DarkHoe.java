package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ActionResultType;

import javax.annotation.Nonnull;

public class DarkHoe extends PEToolBase
{
	public DarkHoe(Properties props)
	{
		super(props, (byte)2, new String[]{});
		this.peToolMaterial = EnumMatterType.DARK_MATTER;
	}

	// Only for RedHoe
	protected DarkHoe(Properties props, byte numCharges, String[] modeDesc)
	{
		super(props, numCharges, modeDesc);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		tillAOE(ctx.getItem(), ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getFace(), 0);
		return ActionResultType.SUCCESS;
	}
}
