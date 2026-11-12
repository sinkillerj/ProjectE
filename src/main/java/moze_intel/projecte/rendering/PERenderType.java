package moze_intel.projecte.rendering;

import java.util.function.Function;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public final class PERenderType {

	private PERenderType() {
	}

	public static final Function<Identifier, RenderType> SPRITE_RENDERER = RenderTypes::entityCutout;

	public static final Function<Identifier, RenderType> YEU_RENDERER = RenderTypes::entityTranslucent;

	public static final RenderType TRANSMUTATION_OVERLAY = RenderTypes.lines();
}
