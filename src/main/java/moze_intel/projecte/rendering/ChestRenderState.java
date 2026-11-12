package moze_intel.projecte.rendering;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class ChestRenderState extends BlockEntityRenderState {
    public float open;
    public Direction facing = Direction.SOUTH;
}
