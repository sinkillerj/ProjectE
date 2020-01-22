package moze_intel.projecte.rendering;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class PERenderType extends RenderType {

	//Ignored
	public PERenderType(String name, VertexFormat format, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable runnablePre, Runnable runnablePost) {
		super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, runnablePre, runnablePost);
	}

	public static RenderType spriteRenderer(ResourceLocation resourceLocation) {
		RenderType.State state = RenderType.State.builder()
				.texture(new RenderState.TextureState(resourceLocation, false, false))//Texture state
				.lightmap(LIGHTMAP_DISABLED)//disableLighting
				.alpha(HALF_ALPHA)//alpha
				.build(true);
		return get("sprite_renderer", DefaultVertexFormats.POSITION_TEX, 7, 256, true, false, state);
	}

	public static RenderType yeuRenderer(ResourceLocation resourceLocation) {
		RenderType.State state = RenderType.State.builder()
				.texture(new RenderState.TextureState(resourceLocation, false, false))//Texture state
				.lightmap(LIGHTMAP_DISABLED)//disableLighting
				.alpha(HALF_ALPHA)//alpha
				.build(true);
		return get("yeu_renderer", DefaultVertexFormats.POSITION_TEX_COLOR, 7, 256, true, false, state);
	}

	public static RenderType transmutationOverlay() {
		RenderType.State state = RenderType.State.builder()
				.transparency(TRANSLUCENT_TRANSPARENCY)//enableBled/blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA)
				.texture(NO_TEXTURE)//disableTexture
				.cull(CULL_DISABLED)//disableCull
				.lightmap(LIGHTMAP_DISABLED)//disableLighting
				.writeMask(COLOR_WRITE)//depthMask(false)
				.build(true);
		return get("transmutation_overlay", DefaultVertexFormats.POSITION_COLOR, 7, 256, true, false, state);
	}
}