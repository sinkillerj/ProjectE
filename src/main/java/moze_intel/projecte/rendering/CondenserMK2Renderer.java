package moze_intel.projecte.rendering;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class CondenserMK2Renderer extends TileEntityRenderer<CondenserMK2Tile>
{
	private final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/blocks/condenser_mk2.png");
	private final ModelChest model = new ModelChest();

	@Override
	public void render(@Nonnull CondenserMK2Tile condenser, double x, double y, double z, float partialTicks, int destroyStage)
	{
		EnumFacing direction = null;
		if (condenser.getWorld() != null && !condenser.isRemoved())
		{
			IBlockState state = condenser.getWorld().getBlockState(condenser.getPos());
			direction = state.getBlock() == ObjHandler.condenserMk2 ? state.get(PEStateProps.FACING) : null;
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
		float adjustedLidAngle = condenser.prevLidAngle + (condenser.lidAngle - condenser.prevLidAngle) * partialTicks;
		adjustedLidAngle = 1.0F - adjustedLidAngle;
		adjustedLidAngle = 1.0F - adjustedLidAngle * adjustedLidAngle * adjustedLidAngle;
		model.getLid().rotateAngleX = -(adjustedLidAngle * (float) Math.PI / 2.0F);
		model.renderAll();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
