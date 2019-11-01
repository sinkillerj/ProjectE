package moze_intel.projecte.rendering.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class RandomizerRenderer extends EntitySpriteRenderer<EntityMobRandomizer> {

	public RandomizerRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityMobRandomizer entity) {
		return new ResourceLocation(PECore.MODID, "textures/entity/randomizer.png");
	}
}