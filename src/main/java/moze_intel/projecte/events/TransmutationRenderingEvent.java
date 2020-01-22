package moze_intel.projecte.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.rendering.PERenderType;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class TransmutationRenderingEvent {

	private static final Minecraft mc = Minecraft.getInstance();
	private static BlockState transmutationResult;

	@SubscribeEvent
	public static void preDrawHud(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == ElementType.CROSSHAIRS) {
			if (transmutationResult != null) {
				if (transmutationResult.getBlock() instanceof FlowingFluidBlock) {
					FluidAttributes resultAttributes = ((FlowingFluidBlock) transmutationResult.getBlock()).getFluid().getAttributes();
					int color = resultAttributes.getColor();
					float red = (color >> 16 & 0xFF) / 255.0F;
					float green = (color >> 8 & 0xFF) / 255.0F;
					float blue = (color & 0xFF) / 255.0F;
					float alpha = (color >> 24 & 0xFF) / 255.0F;
					TextureAtlasSprite sprite = mc.getTextureGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(resultAttributes.getStillTexture());
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder wr = tessellator.getBuffer();
					wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
					wr.pos(0, 0, 0).tex(sprite.getMinU(), sprite.getMinV()).color(red, green, blue, alpha).endVertex();
					wr.pos(0, 16, 0).tex(sprite.getMinU(), sprite.getMaxV()).color(red, green, blue, alpha).endVertex();
					wr.pos(16, 16, 0).tex(sprite.getMaxU(), sprite.getMaxV()).color(red, green, blue, alpha).endVertex();
					wr.pos(16, 0, 0).tex(sprite.getMaxU(), sprite.getMinV()).color(red, green, blue, alpha).endVertex();
					tessellator.draw();
				} else {
					RenderHelper.func_227780_a_();
					IBakedModel model = mc.getBlockRendererDispatcher().getModelForState(transmutationResult);
					mc.getItemRenderer().renderItemModelIntoGUI(new ItemStack(transmutationResult.getBlock()), 0, 0, model);
					RenderHelper.disableStandardItemLighting();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onOverlay(DrawHighlightEvent.HighlightBlock event) {
		PlayerEntity player = mc.player;
		World world = player.getEntityWorld();
		ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
		if (stack.isEmpty()) {
			stack = player.getHeldItem(Hand.OFF_HAND);
		}
		if (stack.isEmpty() || stack.getItem() != ObjHandler.philosStone) {
			transmutationResult = null;
			return;
		}
		RayTraceResult mop = ((PhilosophersStone) ObjHandler.philosStone).getHitBlock(player);
		if (mop instanceof BlockRayTraceResult) {
			BlockRayTraceResult rtr = (BlockRayTraceResult) mop;
			BlockState current = world.getBlockState(rtr.getPos());
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.func_225608_bj_());
			if (transmutationResult != null) {
				ActiveRenderInfo activeRenderInfo = event.getInfo();
				Vec3d viewPosition = activeRenderInfo.getProjectedView();
				int charge = ((ItemMode) stack.getItem()).getCharge(stack);
				byte mode = ((ItemMode) stack.getItem()).getMode(stack);
				float alpha = ProjectEConfig.client.pulsatingOverlay.get() ? getPulseProportion() * 0.60F : 0.35F;
				IRenderTypeBuffer.Impl impl = Minecraft.getInstance().func_228019_au_().func_228487_b_();
				IVertexBuilder builder = impl.getBuffer(PERenderType.transmutationOverlay());
				MatrixStack matrix = new MatrixStack();
				//Note: Doesn't support roll
				//matrix.rotate(Vector3f.field_229183_f_.func_229187_a_(cameraSetup.getRoll()));
				matrix.rotate(Vector3f.field_229179_b_.func_229187_a_(activeRenderInfo.getPitch()));
				matrix.rotate(Vector3f.field_229181_d_.func_229187_a_(activeRenderInfo.getYaw() + 180.0F));
				matrix.translate(-viewPosition.x, -viewPosition.y, -viewPosition.z);
				for (BlockPos pos : PhilosophersStone.getAffectedPositions(world, rtr.getPos(), player, rtr.getFace(), mode, charge)) {
					matrix.push();
					matrix.translate(pos.getX(), pos.getY(), pos.getZ());
					matrix.scale(1.02F, 1.02F, 1.02F);
					matrix.translate(-0.01, -0.01, -0.01);
					Matrix4f matrix4f = matrix.getLast().getPositionMatrix();
					world.getBlockState(pos).getShape(world, pos).forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
						float bMinX = (float) minX;
						float bMinY = (float) minY;
						float bMinZ = (float) minZ;
						float bMaxX = (float) maxX;
						float bMaxY = (float) maxY;
						float bMaxZ = (float) maxZ;

						//Top
						builder.pos(matrix4f, bMinX, bMaxY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMaxX, bMaxY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMaxX, bMaxY, bMaxZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMinX, bMaxY, bMaxZ).color(1, 1, 1, alpha).endVertex();

						//Bottom
						builder.pos(matrix4f, bMinX, bMinY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMaxX, bMinY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMaxX, bMinY, bMaxZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMinX, bMinY, bMaxZ).color(1, 1, 1, alpha).endVertex();

						//Front
						builder.pos(matrix4f, bMaxX, bMaxY, bMaxZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMinX, bMaxY, bMaxZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMinX, bMinY, bMaxZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMaxX, bMinY, bMaxZ).color(1, 1, 1, alpha).endVertex();

						//Back
						builder.pos(matrix4f, bMaxX, bMinY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMinX, bMinY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMinX, bMaxY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMaxX, bMaxY, bMinZ).color(1, 1, 1, alpha).endVertex();

						//Left
						builder.pos(matrix4f, bMinX, bMaxY, bMaxZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMinX, bMaxY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMinX, bMinY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMinX, bMinY, bMaxZ).color(1, 1, 1, alpha).endVertex();

						//Right
						builder.pos(matrix4f, bMaxX, bMaxY, bMaxZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMaxX, bMaxY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMaxX, bMinY, bMinZ).color(1, 1, 1, alpha).endVertex();
						builder.pos(matrix4f, bMaxX, bMinY, bMaxZ).color(1, 1, 1, alpha).endVertex();
					});
					matrix.pop();
				}
			}
		} else {
			transmutationResult = null;
		}
	}

	private static float getPulseProportion() {
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}