package moze_intel.projecte.rendering;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class PedestalRenderState extends BlockEntityRenderState {

	public ItemStack item = ItemStack.EMPTY;
	public float partialTick;
	public long gameTime;
	@NotNull
	public AABB effectBounds = new AABB(0, 0, 0, 0, 0, 0);
}
