package moze_intel.projecte.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.tiles.ChestTileEmc;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.model.ChestModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

//Only used on the client
public class ChestRenderer extends TileEntityRenderer<ChestTileEmc> {

	private final ChestModel model = new ChestModel();
	private final Predicate<Block> blockChecker;
	private final ResourceLocation texture;

	public ChestRenderer(ResourceLocation texture, Predicate<Block> blockChecker) {
		this.texture = texture;
		this.blockChecker = blockChecker;
	}

	@Override
	public void render(@Nonnull ChestTileEmc chestTile, double x, double y, double z, float partialTicks, int destroyStage) {
		Direction direction = null;
		if (chestTile.getWorld() != null && !chestTile.isRemoved()) {
			BlockState state = chestTile.getWorld().getBlockState(chestTile.getPos());
			direction = blockChecker.test(state.getBlock()) ? state.get(BlockStateProperties.HORIZONTAL_FACING) : null;
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
		float adjustedLidAngle = chestTile.getLidAngle(partialTicks);
		adjustedLidAngle = 1.0F - adjustedLidAngle;
		adjustedLidAngle = 1.0F - adjustedLidAngle * adjustedLidAngle * adjustedLidAngle;
		model.getLid().rotateAngleX = -(adjustedLidAngle * (float) Math.PI / 2.0F);
		model.renderAll();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}