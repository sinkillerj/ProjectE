package moze_intel.projecte.rendering;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class CondenserMK2Renderer extends TileEntitySpecialRenderer<CondenserMK2Tile>
{
	private final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/blocks/condenser_mk2.png");
	private final ModelChest model = new ModelChest();

	@Override
	public void render(@Nonnull CondenserMK2Tile condenser, double x, double y, double z, float partialTicks, int destroyStage, float unknown)
	{
		EnumFacing direction = null;
		if (condenser.getWorld() != null && !condenser.isInvalid())
		{
			IBlockState state = condenser.getWorld().getBlockState(condenser.getPos());
			direction = state.getBlock() == ObjHandler.condenserMk2 ? state.getValue(PEStateProps.FACING) : null;
		}

		this.bindTexture(texture);
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate(x, y + 1.0F, z + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);

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

		GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		float adjustedLidAngle = condenser.prevLidAngle + (condenser.lidAngle - condenser.prevLidAngle) * partialTicks;
		adjustedLidAngle = 1.0F - adjustedLidAngle;
		adjustedLidAngle = 1.0F - adjustedLidAngle * adjustedLidAngle * adjustedLidAngle;
		model.chestLid.rotateAngleX = -(adjustedLidAngle * (float) Math.PI / 2.0F);
		model.renderAll();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
