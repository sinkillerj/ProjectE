package moze_intel.projecte.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.model.ChestModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CondenserRenderer extends TileEntityRenderer<CondenserTile> {

	private final ResourceLocation texture = new ResourceLocation(PECore.MODID, "textures/blocks/condenser.png");
	private final ChestModel model = new ChestModel();

	@Override
	public void render(@Nonnull CondenserTile condenser, double x, double y, double z, float partialTicks, int destroyStage) {
		Direction direction = null;
		if (condenser.getWorld() != null && !condenser.isRemoved()) {
			BlockState state = condenser.getWorld().getBlockState(condenser.getPos());
			direction = state.getBlock() == ObjHandler.condenser ? state.get(BlockStateProperties.HORIZONTAL_FACING) : null;
		}

		this.bindTexture(texture);
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translated(x, y + 1.0F, z + 1.0F);
		GlStateManager.scalef(1.0F, -1.0F, -1.0F);
		GlStateManager.translatef(0.5F, 0.5F, 0.5F);

		short angle = 0;

		if (direction != null) {
			switch (direction) {
				case NORTH:
					angle = 180;
					break;
				case SOUTH:
					angle = 0;
					break;
				case WEST:
					angle = 90;
					break;
				case EAST:
					angle = -90;
					break;
			}
		}

		GlStateManager.rotatef(angle, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
		float adjustedLidAngle = condenser.getLidAngle(partialTicks);
		adjustedLidAngle = 1.0F - adjustedLidAngle;
		adjustedLidAngle = 1.0F - adjustedLidAngle * adjustedLidAngle * adjustedLidAngle;
		model.getLid().rotateAngleX = -(adjustedLidAngle * (float) Math.PI / 2.0F);
		model.renderAll();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}