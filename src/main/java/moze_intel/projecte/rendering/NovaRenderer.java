package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.Supplier;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

//Only used on the client
public class NovaRenderer<T extends PrimedTnt> extends EntityRenderer<T> {

	private final BlockRenderDispatcher blockRenderer;
	private final Supplier<BlockState> stateSupplier;

	public NovaRenderer(EntityRendererProvider.Context context, Supplier<BlockState> stateSupplier) {
		super(context);
		this.blockRenderer = context.getBlockRenderDispatcher();
		this.stateSupplier = stateSupplier;
		this.shadowRadius = 0.5F;
	}

	@Override
	public void render(@NotNull T entity, float entityYaw, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light) {
		matrix.pushPose();
		matrix.translate(0.0D, 0.5D, 0.0D);
		int fuse = entity.getFuse();
		if ((float) fuse - partialTick + 1.0F < 10.0F) {
			float f = 1.0F - ((float) fuse - partialTick + 1.0F) / 10.0F;
			f = Mth.clamp(f, 0.0F, 1.0F);
			f = f * f;
			f = f * f;
			float f1 = 1.0F + f * 0.3F;
			matrix.scale(f1, f1, f1);
		}

		matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));
		matrix.translate(-0.5D, -0.5D, 0.5D);
		matrix.mulPose(Axis.YP.rotationDegrees(90.0F));
		TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, stateSupplier.get(), matrix, renderer, light, fuse / 5 % 2 == 0);
		matrix.popPose();
		super.render(entity, entityYaw, partialTick, matrix, renderer, light);
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull T entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}