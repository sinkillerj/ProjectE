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

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityLensProjectile entity) {
		return new ResourceLocation(PECore.MODID, "textures/entity/lens_explosive.png");
	}
}