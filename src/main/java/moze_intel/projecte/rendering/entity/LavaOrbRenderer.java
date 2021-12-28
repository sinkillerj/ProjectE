package moze_intel.projecte.rendering.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LavaOrbRenderer extends EntitySpriteRenderer<EntityLavaProjectile> {

	public LavaOrbRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull EntityLavaProjectile entity) {
		return PECore.rl("textures/entity/lava_orb.png");
	}
}