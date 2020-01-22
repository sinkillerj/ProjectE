package moze_intel.projecte.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.tiles.ChestTileEmc;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;

//Only used on the client
public class ChestRenderer extends ChestTileEntityRenderer<ChestTileEmc> {

	private final ModelRenderer lid;
	private final ModelRenderer base;
	private final ModelRenderer latch;

	private final Predicate<Block> blockChecker;
	private final ResourceLocation texture;

	public ChestRenderer(TileEntityRendererDispatcher dispatcher, ResourceLocation texture, Predicate<Block> blockChecker) {
		super(dispatcher);
		this.texture = texture;
		this.blockChecker = blockChecker;
		this.base = new ModelRenderer(64, 64, 0, 19);
		this.base.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.lid = new ModelRenderer(64, 64, 0, 0);
		this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.lid.rotationPointY = 9.0F;
		this.lid.rotationPointZ = 1.0F;
		this.latch = new ModelRenderer(64, 64, 0, 0);
		this.latch.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.latch.rotationPointY = 8.0F;
	}

	@Override
	public void render(@Nonnull ChestTileEmc chestTile, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight) {
		matrix.push();
		if (chestTile.getWorld() != null && !chestTile.isRemoved()) {
			BlockState state = chestTile.getWorld().getBlockState(chestTile.getPos());
			if (blockChecker.test(state.getBlock())) {
				matrix.translate(0.5D, 0.5D, 0.5D);
				matrix.rotate(Vector3f.field_229181_d_.func_229187_a_(-state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle()));
				matrix.translate(-0.5D, -0.5D, -0.5D);
			}
		}
		float lidAngle = 1.0F - chestTile.getLidAngle(partialTick);
		lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
		IVertexBuilder builder = renderer.getBuffer(RenderType.entityCutout(texture));
		lid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
		latch.rotateAngleX = lid.rotateAngleX;
		lid.render(matrix, builder, light, overlayLight);
		latch.render(matrix, builder, light, overlayLight);
		base.render(matrix, builder, light, overlayLight);
		matrix.pop();
	}
}