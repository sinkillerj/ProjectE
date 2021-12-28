package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class PERenderType extends RenderType {

	//Ignored
	private PERenderType(String name, VertexFormat format, Mode drawMode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable setupTask, Runnable clearTask) {
		super(name, format, drawMode, bufferSize, useDelegate, needsSorting, setupTask, clearTask);
	}

	public static final Function<ResourceLocation, RenderType> SPRITE_RENDERER = Util.memoize(PERenderType::spriteRenderer);

	private static RenderType spriteRenderer(ResourceLocation resourceLocation) {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.POSITION_TEX_SHADER)//TODO - 1.18: Figure this out
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))//Texture state
				.setLightmapState(NO_LIGHTMAP)//disableLighting
				//TODO - 1.18: Re-evaluate
				//.setAlphaState(MIDWAY_ALPHA)//alpha
				.createCompositeState(true);
		return create("projecte_sprite_renderer", DefaultVertexFormat.POSITION_TEX, Mode.QUADS, 256, true, false, state);
	}

	public static final Function<ResourceLocation, RenderType> YEU_RENDERER = Util.memoize(PERenderType::yeuRenderer);

	private static RenderType yeuRenderer(ResourceLocation resourceLocation) {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)//TODO - 1.18: Figure this out
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))//Texture state
				.setLightmapState(NO_LIGHTMAP)//disableLighting
				//TODO - 1.18: Re-evaluate
				//.setAlphaState(MIDWAY_ALPHA)//alpha
				.setCullState(NO_CULL)
				.createCompositeState(true);
		return create("projecte_yeu_renderer", DefaultVertexFormat.POSITION_COLOR_TEX, Mode.QUADS, 256, true, false, state);
	}

	public static final RenderType TRANSMUTATION_OVERLAY = transmutationOverlay();

	private static RenderType transmutationOverlay() {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_SHADER)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)//enableBled/blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA)
				.setTextureState(NO_TEXTURE)//disableTexture
				.setCullState(NO_CULL)//disableCull
				.setLightmapState(NO_LIGHTMAP)//disableLighting
				.setWriteMaskState(COLOR_WRITE)//depthMask(false)
				.setLayeringState(POLYGON_OFFSET_LAYERING)//Offset it so that can render properly
				.createCompositeState(true);
		return create("projecte_transmutation_overlay", DefaultVertexFormat.POSITION_COLOR, Mode.QUADS, 256, true, false, state);
	}
}