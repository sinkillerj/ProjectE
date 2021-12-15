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

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull EntityMobRandomizer entity) {
		return PECore.rl("textures/entity/randomizer.png");
	}
}