package moze_intel.projecte.api.state;

import moze_intel.projecte.api.state.enums.EnumFuelType;
import moze_intel.projecte.api.state.enums.EnumMatterType;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;

public final class PEStateProps
{

    public static final IProperty<EnumFacing> FACING = BlockHorizontal.HORIZONTAL_FACING;

    private PEStateProps() {}

}
