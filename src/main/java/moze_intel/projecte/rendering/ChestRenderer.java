package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.block_entities.ChestTileEmc;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

//Only used on the client
public class ChestRenderer implements BlockEntityRenderer<ChestTileEmc> {

	private final ModelPart lid;
	private final ModelPart bottom;
	private final ModelPart lock;

	private final Predicate<Block> blockChecker;
	private final ResourceLocation texture;

	public ChestRenderer(BlockEntityRendererProvider.Context context, ResourceLocation texture, Supplier<BlockRegistryObject<?, ?>> type) {
		this.texture = texture;
		this.blockChecker = block -> block == type.get().getBlock();
		ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
		this.bottom = modelpart.getChild("bottom");
		this.lid = modelpart.getChild("lid");
		this.lock = modelpart.getChild("lock");
	}

	@Override
	public void render(@Nonnull ChestTileEmc chestTile, float partialTick, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource renderer, int light, int overlayLight) {
		matrix.pushPose();
		if (chestTile.getLevel() != null && !chestTile.isRemoved()) {
			BlockState state = chestTile.getLevel().getBlockState(chestTile.getBlockPos());
			if (blockChecker.test(state.getBlock())) {
				matrix.translate(0.5D, 0.5D, 0.5D);
				matrix.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
				matrix.translate(-0.5D, -0.5D, -0.5D);
			}
		}
		float lidAngle = 1.0F - chestTile.getLidAngle(partialTick);
		lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
		VertexConsumer builder = renderer.getBuffer(RenderType.entityCutout(texture));
		lid.xRot = -(lidAngle * ((float) Math.PI / 2F));
		lock.xRot = lid.xRot;
		lid.render(matrix, builder, light, overlayLight);
		lock.render(matrix, builder, light, overlayLight);
		bottom.render(matrix, builder, light, overlayLight);
		matrix.popPose();
	}
}