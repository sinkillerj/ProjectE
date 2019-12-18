package moze_intel.projecte.rendering.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class WaterOrbRenderer extends EntitySpriteRenderer<EntityWaterProjectile> {

	public WaterOrbRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull EntityWaterProjectile entity) {
		return new ResourceLocation(PECore.MODID, "textures/entity/water_orb.png");
	}
}