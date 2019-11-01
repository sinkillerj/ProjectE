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

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntitySWRGProjectile entity) {
		return new ResourceLocation(PECore.MODID, "textures/entity/lightning.png");
	}
}