package moze_intel.projecte.rendering.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class ExplosiveLensRenderer extends EntitySpriteRenderer<EntityLensProjectile> {

	public ExplosiveLensRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull EntityLensProjectile entity) {
		return PECore.rl("textures/entity/lens_explosive.png");
	}
}