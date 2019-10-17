package moze_intel.projecte.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.model.ChestModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ChestRenderer extends TileEntityRenderer<AlchChestTile>
{
	private final ResourceLocation texture = new ResourceLocation(PECore.MODID, "textures/blocks/alchemy_chest.png");
	private final ChestModel model = new ChestModel();
	
	@Override
	public void render(@Nonnull AlchChestTile chestTile, double x, double y, double z, float partialTicks, int destroyStage)
	{
		Direction direction = null;
		if (chestTile.getWorld() != null && !chestTile.isRemoved())
		{
			BlockState state = chestTile.getWorld().getBlockState(chestTile.getPos());
			direction = state.getBlock() == ObjHandler.alchChest ? state.get(BlockStateProperties.HORIZONTAL_FACING) : null;
		}
		
		this.bindTexture(texture);
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translated(x, y + 1.0F, z + 1.0F);
		GlStateManager.scalef(1.0F, -1.0F, -1.0F);
		GlStateManager.translatef(0.5F, 0.5F, 0.5F);

		short angle = 0;

		if (direction != null)
		{
			switch (direction)
			{
				case NORTH: angle = 180; break;
				case SOUTH: angle = 0; break;
				case WEST: angle = 90; break;
				case EAST: angle = -90; break;
			}
		}

		GlStateManager.rotatef(angle, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
		float adjustedLidAngle = chestTile.prevLidAngle + (chestTile.lidAngle - chestTile.prevLidAngle) * partialTicks;
		adjustedLidAngle = 1.0F - adjustedLidAngle;
		adjustedLidAngle = 1.0F - adjustedLidAngle * adjustedLidAngle * adjustedLidAngle;
		model.getLid().rotateAngleX = -(adjustedLidAngle * (float) Math.PI / 2.0F);
		model.renderAll();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
