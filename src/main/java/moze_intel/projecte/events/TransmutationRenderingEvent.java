package moze_intel.projecte.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nullable;
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
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
	@Nullable
	private static BlockState transmutationResult;
	private static long lastGameTime;

	@SubscribeEvent
	public static void preDrawHud(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == ElementType.CROSSHAIRS && transmutationResult != null) {
			if (transmutationResult.getBlock() instanceof FlowingFluidBlock) {
				FluidAttributes resultAttributes = ((FlowingFluidBlock) transmutationResult.getBlock()).getFluid().getAttributes();
				int color = resultAttributes.getColor();
				float red = (color >> 16 & 0xFF) / 255.0F;
				float green = (color >> 8 & 0xFF) / 255.0F;
				float blue = (color & 0xFF) / 255.0F;
				float alpha = (color >> 24 & 0xFF) / 255.0F;
				TextureAtlasSprite sprite = mc.getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(resultAttributes.getStillTexture());
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder wr = tessellator.getBuilder();
				wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				wr.vertex(0, 0, 0).uv(sprite.getU0(), sprite.getV0()).color(red, green, blue, alpha).endVertex();
				wr.vertex(0, 16, 0).uv(sprite.getU0(), sprite.getV1()).color(red, green, blue, alpha).endVertex();
				wr.vertex(16, 16, 0).uv(sprite.getU1(), sprite.getV1()).color(red, green, blue, alpha).endVertex();
				wr.vertex(16, 0, 0).uv(sprite.getU1(), sprite.getV0()).color(red, green, blue, alpha).endVertex();
				tessellator.end();
			} else {
				//Just render it normally instead of with the given model as some block's don't render properly then as an item
				// for example glass panes
				RenderHelper.turnBackOn();
				mc.getItemRenderer().renderGuiItem(new ItemStack(transmutationResult.getBlock()), 0, 0);
				RenderHelper.turnOff();
			}
			long gameTime = mc.level == null ? 0 : mc.level.getGameTime();
			if (lastGameTime != gameTime) {
				//If the game time changed so we aren't actually still hovering a block set our
				// result to null. We do this after rendering it just in case there is a single
				// frame where this may actually be valid based on the order the events are fired
				transmutationResult = null;
				lastGameTime = gameTime;
			}
		}
	}

	@SubscribeEvent
	public static void onOverlay(DrawHighlightEvent.HighlightBlock event) {
		ActiveRenderInfo activeRenderInfo = event.getInfo();
		if (!(activeRenderInfo.getEntity() instanceof PlayerEntity)) {
			return;
		}
		lastGameTime = mc.level == null ? 0 : mc.level.getGameTime();
		PlayerEntity player = (PlayerEntity) activeRenderInfo.getEntity();
		World world = player.getCommandSenderWorld();
		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty()) {
			stack = player.getOffhandItem();
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
			BlockState current = world.getBlockState(rtr.getBlockPos());
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.isShiftKeyDown());
			if (transmutationResult != null) {
				Vector3d viewPosition = activeRenderInfo.getPosition();
				int charge = philoStone.getCharge(stack);
				byte mode = philoStone.getMode(stack);
				float alpha = ProjectEConfig.client.pulsatingOverlay.get() ? getPulseProportion() * 0.60F : 0.35F;
				IVertexBuilder builder = event.getBuffers().getBuffer(PERenderType.transmutationOverlay());
				MatrixStack matrix = event.getMatrix();
				matrix.pushPose();
				matrix.translate(-viewPosition.x, -viewPosition.y, -viewPosition.z);
				ISelectionContext selectionContext = ISelectionContext.of(player);
				for (BlockPos pos : PhilosophersStone.getChanges(world, rtr.getBlockPos(), player, rtr.getDirection(), mode, charge).keySet()) {
					BlockState state = world.getBlockState(pos);
					if (!state.isAir(world, pos)) {
						VoxelShape shape = state.getShape(world, pos, selectionContext);
						if (!shape.isEmpty()) {
							matrix.pushPose();
							matrix.translate(pos.getX(), pos.getY(), pos.getZ());
							Matrix4f matrix4f = matrix.last().pose();
							shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> addBox(builder, matrix4f, alpha,
									(float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ));
							matrix.popPose();
						}
					}
				}
				matrix.popPose();
			}
		} else {
			transmutationResult = null;
		}
	}

	private static void addBox(IVertexBuilder builder, Matrix4f matrix4f, float alpha, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		//Top
		builder.vertex(matrix4f, minX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, maxX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, maxX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, minX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();

		//Bottom
		builder.vertex(matrix4f, minX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, maxX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, maxX, minY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, minX, minY, maxZ).color(1, 1, 1, alpha).endVertex();

		//Front
		builder.vertex(matrix4f, maxX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, minX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, minX, minY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, maxX, minY, maxZ).color(1, 1, 1, alpha).endVertex();

		//Back
		builder.vertex(matrix4f, maxX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, minX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, minX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, maxX, maxY, minZ).color(1, 1, 1, alpha).endVertex();

		//Left
		builder.vertex(matrix4f, minX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, minX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, minX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, minX, minY, maxZ).color(1, 1, 1, alpha).endVertex();

		//Right
		builder.vertex(matrix4f, maxX, maxY, maxZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, maxX, maxY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, maxX, minY, minZ).color(1, 1, 1, alpha).endVertex();
		builder.vertex(matrix4f, maxX, minY, maxZ).color(1, 1, 1, alpha).endVertex();
	}

	private static float getPulseProportion() {
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}