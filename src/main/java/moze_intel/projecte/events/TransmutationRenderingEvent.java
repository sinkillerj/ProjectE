package moze_intel.projecte.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.rendering.PERenderType;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
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
					TextureAtlasSprite sprite = mc.getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(resultAttributes.getStillTexture());
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder wr = tessellator.getBuffer();
					wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
					wr.pos(0, 0, 0).tex(sprite.getMinU(), sprite.getMinV()).color(red, green, blue, alpha).endVertex();
					wr.pos(0, 16, 0).tex(sprite.getMinU(), sprite.getMaxV()).color(red, green, blue, alpha).endVertex();
					wr.pos(16, 16, 0).tex(sprite.getMaxU(), sprite.getMaxV()).color(red, green, blue, alpha).endVertex();
					wr.pos(16, 0, 0).tex(sprite.getMaxU(), sprite.getMinV()).color(red, green, blue, alpha).endVertex();
					tessellator.draw();
				} else {
					RenderHelper.enableStandardItemLighting();
					IBakedModel model = mc.getBlockRendererDispatcher().getModelForState(transmutationResult);
					mc.getItemRenderer().renderItemModelIntoGUI(new ItemStack(transmutationResult.getBlock()), 0, 0, model);
					RenderHelper.disableStandardItemLighting();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onOverlay(DrawHighlightEvent.HighlightBlock event) {
		//TODO - 1.16: Because this event only fires when we are highlighting something that means when switching to hovering air
		// it doesn't remove the transmutation result properly. Also things like half top slabs render potentially differently than
		// we want to in the draw hud part so we may want to consider adjusting it
		ActiveRenderInfo activeRenderInfo = event.getInfo();
		if (!(activeRenderInfo.getRenderViewEntity() instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) activeRenderInfo.getRenderViewEntity();
		World world = player.getEntityWorld();
		ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
		if (stack.isEmpty()) {
			stack = player.getHeldItem(Hand.OFF_HAND);
		}
		if (stack.isEmpty() || !(stack.getItem() instanceof PhilosophersStone)) {
			transmutationResult = null;
			return;
		}
		PhilosophersStone philoStone = (PhilosophersStone) stack.getItem();
		//Note: We use the philo stone's ray trace instead of the event's ray trace as we want to make sure that we
		// can properly take fluid into account/ignore it when needed
		BlockRayTraceResult rtr = philoStone.getHitBlock(player);
		if (rtr.getType() == RayTraceResult.Type.BLOCK) {
			BlockState current = world.getBlockState(rtr.getPos());
			//TODO: Evaluate making this even smarter and allowing for blocks to flag themselves as being able to propagate
			// to neighboring blocks of different states so that things like fences are easier to convert
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.isSneaking());
			if (transmutationResult != null) {
				Vector3d viewPosition = activeRenderInfo.getProjectedView();
				int charge = philoStone.getCharge(stack);
				byte mode = philoStone.getMode(stack);
				float alpha = ProjectEConfig.client.pulsatingOverlay.get() ? getPulseProportion() * 0.60F : 0.35F;
				IVertexBuilder builder = event.getBuffers().getBuffer(PERenderType.transmutationOverlay());
				MatrixStack matrix = event.getMatrix();
				matrix.push();
				matrix.translate(-viewPosition.x, -viewPosition.y, -viewPosition.z);
				ISelectionContext selectionContext = ISelectionContext.forEntity(player);
				for (BlockPos pos : PhilosophersStone.getAffectedPositions(world, rtr.getPos(), player, rtr.getFace(), mode, charge)) {
					BlockState state = world.getBlockState(pos);
					if (!state.isAir(world, pos)) {
						VoxelShape shape = state.getShape(world, pos, selectionContext);
						if (!shape.isEmpty()) {
							matrix.push();
							matrix.translate(pos.getX(), pos.getY(), pos.getZ());
							Matrix4f matrix4f = matrix.getLast().getMatrix();
							shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> addBox(builder, matrix4f, alpha,
									(float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ));
							matrix.pop();
						}
					}
				}
				matrix.pop();
			}
		} else {
			transmutationResult = null;
		}
	}

	private static void addBox(IVertexBuilder builder, Matrix4f matrix4f, float alpha, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		//Top
		builder.pos(matrix4f, minX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, maxX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, maxX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, minX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();

		//Bottom
		builder.pos(matrix4f, minX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, maxX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, maxX, minY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, minX, minY, maxZ).color(1, 1, 1, alpha).endVertex();

		//Front
		builder.pos(matrix4f, maxX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, minX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, minX, minY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, maxX, minY, maxZ).color(1, 1, 1, alpha).endVertex();

		//Back
		builder.pos(matrix4f, maxX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, minX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, minX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, maxX, maxY, minZ).color(1, 1, 1, alpha).endVertex();

		//Left
		builder.pos(matrix4f, minX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, minX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, minX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, minX, minY, maxZ).color(1, 1, 1, alpha).endVertex();

		//Right
		builder.pos(matrix4f, maxX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, maxX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, maxX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.pos(matrix4f, maxX, minY, maxZ).color(1, 1, 1, alpha).endVertex();
	}

	private static float getPulseProportion() {
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}