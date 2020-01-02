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
		RenderType.State state = RenderType.State.func_228694_a_()
				.func_228724_a_(new RenderState.TextureState(resourceLocation, false, false))//Texture state
				.func_228719_a_(field_228529_u_)//disableLighting
				.func_228713_a_(field_228518_j_)//alpha
				.func_228728_a_(true);
		return func_228633_a_("sprite_renderer", DefaultVertexFormats.POSITION_TEX, 7, 256, true, false, state);
	}

	public static RenderType yeuRenderer(ResourceLocation resourceLocation) {
		RenderType.State state = RenderType.State.func_228694_a_()
				.func_228724_a_(new RenderState.TextureState(resourceLocation, false, false))//Texture state
				.func_228719_a_(field_228529_u_)//disableLighting
				.func_228713_a_(field_228518_j_)//alpha
				.func_228728_a_(true);
		return func_228633_a_("yeu_renderer", DefaultVertexFormats.POSITION_TEX_COLOR, 7, 256, true, false, state);
	}
}