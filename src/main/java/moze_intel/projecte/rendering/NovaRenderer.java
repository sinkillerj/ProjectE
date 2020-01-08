package moze_intel.projecte.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TNTMinecartRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

//Only used on the client
public class NovaRenderer<T extends TNTEntity> extends EntityRenderer<T> {

	private final Supplier<BlockState> stateSupplier;

	public NovaRenderer(EntityRendererManager manager, Supplier<BlockState> stateSupplier) {
		super(manager);
		this.stateSupplier = stateSupplier;
		this.shadowSize = 0.5F;
	}

	@Override
	public void func_225623_a_(@Nonnull T entity, float entityYaw, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light) {
		matrix.func_227860_a_();
		matrix.func_227861_a_(0.0D, 0.5D, 0.0D);
		if ((float) entity.getFuse() - partialTick + 1.0F < 10.0F) {
			float f = 1.0F - ((float) entity.getFuse() - partialTick + 1.0F) / 10.0F;
			f = MathHelper.clamp(f, 0.0F, 1.0F);
			f = f * f;
			f = f * f;
			float f1 = 1.0F + f * 0.3F;
			matrix.func_227862_a_(f1, f1, f1);
		}

		matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-90.0F));
		matrix.func_227861_a_(-0.5D, -0.5D, 0.5D);
		matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(90.0F));
		TNTMinecartRenderer.func_229127_a_(stateSupplier.get(), matrix, renderer, light, entity.getFuse() / 5 % 2 == 0);
		matrix.func_227865_b_();
		super.func_225623_a_(entity, entityYaw, partialTick, matrix, renderer, light);
	}

	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull T entity) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}