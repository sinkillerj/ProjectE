package moze_intel.projecte.gameObjs.items.tools;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class DarkHoe extends PEToolBase {

	public DarkHoe(Properties props) {
		this(props, (byte) 2, EnumMatterType.DARK_MATTER);
	}

	// Only for RedHoe
	protected DarkHoe(Properties props, byte numCharges, EnumMatterType matterType) {
		super(props, numCharges, new String[]{});
		this.peToolMaterial = matterType;
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		tillAOE(ctx.getHand(), ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getFace(), 0);
		return ActionResultType.SUCCESS;
	}
}