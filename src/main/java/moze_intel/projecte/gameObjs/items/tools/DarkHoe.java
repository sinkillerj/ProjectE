package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.api.state.enums.EnumMatterType;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;

import javax.annotation.Nonnull;

public class DarkHoe extends PEToolBase
{
	public DarkHoe(Builder builder)
	{
		super(builder, (byte)2, new String[]{});
		this.peToolMaterial = EnumMatterType.DARK_MATTER;
	}

	// Only for RedHoe
	protected DarkHoe(Builder builder, byte numCharges, String[] modeDesc)
	{
		super(builder, numCharges, modeDesc);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		tillAOE(ctx.getItem(), ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getFace(), 0);
		return EnumActionResult.SUCCESS;
	}
}
