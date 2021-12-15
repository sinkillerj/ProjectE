package moze_intel.projecte.rendering;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class PERenderType extends RenderType {

	//Ignored
	private PERenderType(String name, VertexFormat format, int drawMode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable setupTask, Runnable clearTask) {
		super(name, format, drawMode, bufferSize, useDelegate, needsSorting, setupTask, clearTask);
	}

	public static RenderType spriteRenderer(ResourceLocation resourceLocation) {
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(resourceLocation, false, false))//Texture state
				.setLightmapState(NO_LIGHTMAP)//disableLighting
				.setAlphaState(MIDWAY_ALPHA)//alpha
				.createCompositeState(true);
		return create("sprite_renderer", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, true, false, state);
	}

	public static RenderType yeuRenderer(ResourceLocation resourceLocation) {
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(resourceLocation, false, false))//Texture state
				.setLightmapState(NO_LIGHTMAP)//disableLighting
				.setAlphaState(MIDWAY_ALPHA)//alpha
				.setCullState(NO_CULL)
				.createCompositeState(true);
		return create("yeu_renderer", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, true, false, state);
	}

	public static RenderType transmutationOverlay() {
		RenderType.State state = RenderType.State.builder()
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)//enableBled/blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA)
				.setTextureState(NO_TEXTURE)//disableTexture
				.setCullState(NO_CULL)//disableCull
				.setLightmapState(NO_LIGHTMAP)//disableLighting
				.setWriteMaskState(COLOR_WRITE)//depthMask(false)
				.setLayeringState(POLYGON_OFFSET_LAYERING)//Offset it so that can render properly
				.createCompositeState(true);
		return create("transmutation_overlay", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, true, false, state);
	}
}