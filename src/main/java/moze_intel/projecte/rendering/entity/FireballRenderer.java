package moze_intel.projecte.rendering.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FireballRenderer extends EntitySpriteRenderer<EntityFireProjectile> {

	public FireballRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull EntityFireProjectile entity) {
		return PECore.rl("textures/entity/fireball.png");
	}
}