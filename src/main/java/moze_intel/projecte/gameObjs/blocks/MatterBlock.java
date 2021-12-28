package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.world.level.block.Block;

public class MatterBlock extends Block implements IMatterBlock {

	public final EnumMatterType matterType;

	public MatterBlock(Properties props, EnumMatterType type) {
		super(props);
		this.matterType = type;
	}

	@Override
	public EnumMatterType getMatterType() {
		return matterType;
	}
}