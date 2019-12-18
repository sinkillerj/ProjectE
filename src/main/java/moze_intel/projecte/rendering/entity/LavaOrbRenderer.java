package moze_intel.projecte.rendering.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class LavaOrbRenderer extends EntitySpriteRenderer<EntityLavaProjectile> {

	public LavaOrbRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull EntityLavaProjectile entity) {
		return new ResourceLocation(PECore.MODID, "textures/entity/lava_orb.png");
	}
}