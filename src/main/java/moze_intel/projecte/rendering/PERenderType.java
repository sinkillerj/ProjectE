package moze_intel.projecte.rendering;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class PERenderType extends RenderType {

	//Ignored
	private PERenderType(String name, VertexFormat format, int drawMode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable setupTask, Runnable clearTask) {
		super(name, format, drawMode, bufferSize, useDelegate, needsSorting, setupTask, clearTask);
	}

	public static RenderType spriteRenderer(ResourceLocation resourceLocation) {
		RenderType.State state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(resourceLocation, false, false))//Texture state
				.lightmap(LIGHTMAP_DISABLED)//disableLighting
				.alpha(HALF_ALPHA)//alpha
				.build(true);
		return makeType("sprite_renderer", DefaultVertexFormats.POSITION_TEX, 7, 256, true, false, state);
	}

	public static RenderType yeuRenderer(ResourceLocation resourceLocation) {
		RenderType.State state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(resourceLocation, false, false))//Texture state
				.lightmap(LIGHTMAP_DISABLED)//disableLighting
				.alpha(HALF_ALPHA)//alpha
				.build(true);
		return makeType("yeu_renderer", DefaultVertexFormats.POSITION_TEX_COLOR, 7, 256, true, false, state);
	}

	public static RenderType transmutationOverlay() {
		RenderType.State state = RenderType.State.getBuilder()
				.transparency(TRANSLUCENT_TRANSPARENCY)//enableBled/blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA)
				.texture(NO_TEXTURE)//disableTexture
				.cull(CULL_DISABLED)//disableCull
				.lightmap(LIGHTMAP_DISABLED)//disableLighting
				.writeMask(COLOR_WRITE)//depthMask(false)
				.build(true);
		return makeType("transmutation_overlay", DefaultVertexFormats.POSITION_COLOR, 7, 256, true, false, state);
	}
}