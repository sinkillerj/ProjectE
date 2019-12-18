package moze_intel.projecte.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.tiles.ChestTileEmc;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

//Only used on the client
public class ChestRenderer extends ChestTileEntityRenderer<ChestTileEmc> {

	private final ModelRenderer field_228862_a_;
	private final ModelRenderer field_228863_c_;
	private final ModelRenderer field_228864_d_;
	private final ModelRenderer field_228865_e_;
	private final ModelRenderer field_228866_f_;
	private final ModelRenderer field_228867_g_;
	private final ModelRenderer field_228868_h_;
	private final ModelRenderer field_228869_i_;
	private final ModelRenderer field_228870_j_;

	private final Predicate<Block> blockChecker;
	private final ResourceLocation texture;

	public ChestRenderer(ResourceLocation texture, Predicate<Block> blockChecker) {
		super(TileEntityRendererDispatcher.instance);
		this.texture = texture;
		this.blockChecker = blockChecker;

		//TODO: 1.15, can we get rid of the vanilla copy and somehow just extend ChestTileEntityRenderer though the material is "hardcoded"
		this.field_228863_c_ = new ModelRenderer(64, 64, 0, 19);
		this.field_228863_c_.func_228301_a_(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.field_228862_a_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228862_a_.func_228301_a_(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.field_228862_a_.rotationPointY = 9.0F;
		this.field_228862_a_.rotationPointZ = 1.0F;
		this.field_228864_d_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228864_d_.func_228301_a_(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.field_228864_d_.rotationPointY = 8.0F;
		this.field_228866_f_ = new ModelRenderer(64, 64, 0, 19);
		this.field_228866_f_.func_228301_a_(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.field_228865_e_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228865_e_.func_228301_a_(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.field_228865_e_.rotationPointY = 9.0F;
		this.field_228865_e_.rotationPointZ = 1.0F;
		this.field_228867_g_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228867_g_.func_228301_a_(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.field_228867_g_.rotationPointY = 8.0F;
		this.field_228869_i_ = new ModelRenderer(64, 64, 0, 19);
		this.field_228869_i_.func_228301_a_(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.field_228868_h_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228868_h_.func_228301_a_(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.field_228868_h_.rotationPointY = 9.0F;
		this.field_228868_h_.rotationPointZ = 1.0F;
		this.field_228870_j_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228870_j_.func_228301_a_(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.field_228870_j_.rotationPointY = 8.0F;
	}

	@Override
	public void func_225616_a_(@Nonnull ChestTileEmc chestTile, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int otherLight) {
		//TODO: 1.15 FIXME
		/*Direction direction = null;
		if (chestTile.getWorld() != null && !chestTile.isRemoved()) {
			BlockState state = chestTile.getWorld().getBlockState(chestTile.getPos());
			direction = blockChecker.test(state.getBlock()) ? state.get(BlockStateProperties.HORIZONTAL_FACING) : null;
		}

		field_228858_b_.textureManager.bindTexture(texture);
		RenderSystem.pushMatrix();
		RenderSystem.enableRescaleNormal();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.translated(x, y + 1.0F, z + 1.0F);
		RenderSystem.scalef(1.0F, -1.0F, -1.0F);
		RenderSystem.translatef(0.5F, 0.5F, 0.5F);

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

		RenderSystem.rotatef(angle, 0.0F, 1.0F, 0.0F);
		RenderSystem.translatef(-0.5F, -0.5F, -0.5F);
		float adjustedLidAngle = chestTile.getLidAngle(partialTick);
		adjustedLidAngle = 1.0F - adjustedLidAngle;
		adjustedLidAngle = 1.0F - adjustedLidAngle * adjustedLidAngle * adjustedLidAngle;
		model.getLid().rotateAngleX = -(adjustedLidAngle * (float) Math.PI / 2.0F);
		model.renderAll();
		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);*/
	}

	//TODO: 1.15 FIXME
	/*@Override
	public void func_225616_a_(@Nonnull ChestTileEmc chestTile, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int otherLight) {
		World world = chestTile.getWorld();
		boolean hasWorld = world != null;
		BlockState blockstate = hasWorld ? chestTile.getBlockState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		matrix.func_227860_a_();
		float f = blockstate.get(ChestBlock.FACING).getHorizontalAngle();
		matrix.func_227861_a_(0.5D, 0.5D, 0.5D);
		matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-f));
		matrix.func_227861_a_(-0.5D, -0.5D, -0.5D);
		TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> icallbackwrapper;
		if (hasWorld) {
			icallbackwrapper = abstractchestblock.func_225536_a_(blockstate, world, chestTile.getPos(), true);
		} else {
			icallbackwrapper = TileEntityMerger.ICallback::func_225537_b_;
		}

		float f1 = icallbackwrapper.apply(ChestBlock.func_226917_a_(chestTile)).get(partialTick);
		f1 = 1.0F - f1;
		f1 = 1.0F - f1 * f1 * f1;
		int i = icallbackwrapper.apply(new DualBrightnessCallback<>()).applyAsInt(light);
		Material material = Atlases.func_228771_a_(chestTile, chesttype, false);
		IVertexBuilder ivertexbuilder = material.func_229311_a_(renderer, RenderType::func_228638_b_);
		this.func_228871_a_(matrix, ivertexbuilder, this.field_228862_a_, this.field_228864_d_, this.field_228863_c_, f1, i, otherLight);

		matrix.func_227865_b_();
	}*/

	private void func_228871_a_(MatrixStack matrix, IVertexBuilder renderer, ModelRenderer p_228871_3_, ModelRenderer p_228871_4_, ModelRenderer p_228871_5_, float p_228871_6_, int p_228871_7_, int otherLight) {
		p_228871_3_.rotateAngleX = -(p_228871_6_ * ((float) Math.PI / 2F));
		p_228871_4_.rotateAngleX = p_228871_3_.rotateAngleX;
		p_228871_3_.func_228308_a_(matrix, renderer, p_228871_7_, otherLight);
		p_228871_4_.func_228308_a_(matrix, renderer, p_228871_7_, otherLight);
		p_228871_5_.func_228308_a_(matrix, renderer, p_228871_7_, otherLight);
	}
}