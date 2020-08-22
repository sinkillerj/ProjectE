package moze_intel.projecte.rendering.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class LightningRenderer extends EntitySpriteRenderer<EntitySWRGProjectile> {

	public LightningRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull EntitySWRGProjectile entity) {
		return PECore.rl("textures/entity/lightning.png");
	}
}