package moze_intel.projecte.rendering.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WaterOrbRenderer extends EntitySpriteRenderer<EntityWaterProjectile> {

	public WaterOrbRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull EntityWaterProjectile entity) {
		return PECore.rl("textures/entity/water_orb.png");
	}
}