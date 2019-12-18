package moze_intel.projecte.rendering.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class FireballRenderer extends EntitySpriteRenderer<EntityFireProjectile> {

	public FireballRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull EntityFireProjectile entity) {
		return new ResourceLocation(PECore.MODID, "textures/entity/fireball.png");
	}
}