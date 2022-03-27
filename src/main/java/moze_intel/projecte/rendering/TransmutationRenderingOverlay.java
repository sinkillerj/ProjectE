package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidAttributes;
import org.jetbrains.annotations.Nullable;

public class TransmutationRenderingOverlay implements IIngameOverlay {

	private final Minecraft mc = Minecraft.getInstance();
	@Nullable
	private BlockState transmutationResult;
	private long lastGameTime;

	public TransmutationRenderingOverlay() {
		MinecraftForge.EVENT_BUS.addListener(this::onOverlay);
	}

	@Override
	public void render(ForgeIngameGui gui, PoseStack mStack, float partialTicks, int width, int height) {
		if (!mc.options.hideGui && transmutationResult != null) {
			if (transmutationResult.getBlock() instanceof LiquidBlock liquidBlock) {
				FluidAttributes resultAttributes = liquidBlock.getFluid().getAttributes();
				int color = resultAttributes.getColor();
				float red = (color >> 16 & 0xFF) / 255.0F;
				float green = (color >> 8 & 0xFF) / 255.0F;
				float blue = (color & 0xFF) / 255.0F;
				float alpha = (color >> 24 & 0xFF) / 255.0F;
				TextureAtlasSprite sprite = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(resultAttributes.getStillTexture());
				Tesselator tessellator = Tesselator.getInstance();
				BufferBuilder wr = tessellator.getBuilder();
				wr.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
				wr.vertex(0, 0, 0).uv(sprite.getU0(), sprite.getV0()).color(red, green, blue, alpha).endVertex();
				wr.vertex(0, 16, 0).uv(sprite.getU0(), sprite.getV1()).color(red, green, blue, alpha).endVertex();
				wr.vertex(16, 16, 0).uv(sprite.getU1(), sprite.getV1()).color(red, green, blue, alpha).endVertex();
				wr.vertex(16, 0, 0).uv(sprite.getU1(), sprite.getV0()).color(red, green, blue, alpha).endVertex();
				tessellator.end();
			} else {
				//Just render it normally instead of with the given model as some block's don't render properly then as an item
				// for example glass panes
				mc.getItemRenderer().renderGuiItem(new ItemStack(transmutationResult.getBlock()), 0, 0);
			}
			long gameTime = mc.level == null ? 0 : mc.level.getGameTime();
			if (lastGameTime != gameTime) {
				//If the game time changed, so we aren't actually still hovering a block set our
				// result to null. We do this after rendering it just in case there is a single
				// frame where this may actually be valid based on the order the events are fired
				transmutationResult = null;
				lastGameTime = gameTime;
			}
		}
	}

	private void onOverlay(DrawSelectionEvent.HighlightBlock event) {
		Camera activeRenderInfo = event.getCamera();
		if (!(activeRenderInfo.getEntity() instanceof Player player)) {
			return;
		}
		lastGameTime = mc.level == null ? 0 : mc.level.getGameTime();
		Level level = player.getCommandSenderWorld();
		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty()) {
			stack = player.getOffhandItem();
		}
		if (stack.isEmpty() || !(stack.getItem() instanceof PhilosophersStone philoStone)) {
			transmutationResult = null;
			return;
		}
		//Note: We use the philo stone's ray trace instead of the event's ray trace as we want to make sure that we
		// can properly take fluid into account/ignore it when needed
		BlockHitResult rtr = philoStone.getHitBlock(player);
		if (rtr.getType() == HitResult.Type.BLOCK) {
			BlockState current = level.getBlockState(rtr.getBlockPos());
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.isShiftKeyDown());
			if (transmutationResult != null) {
				Vec3 viewPosition = activeRenderInfo.getPosition();
				int charge = philoStone.getCharge(stack);
				byte mode = philoStone.getMode(stack);
				float alpha = ProjectEConfig.client.pulsatingOverlay.get() ? getPulseProportion() * 0.60F : 0.35F;
				VertexConsumer builder = event.getMultiBufferSource().getBuffer(PERenderType.TRANSMUTATION_OVERLAY);
				PoseStack matrix = event.getPoseStack();
				matrix.pushPose();
				matrix.translate(-viewPosition.x, -viewPosition.y, -viewPosition.z);
				CollisionContext selectionContext = CollisionContext.of(player);
				for (BlockPos pos : PhilosophersStone.getChanges(level, rtr.getBlockPos(), player, rtr.getDirection(), mode, charge).keySet()) {
					BlockState state = level.getBlockState(pos);
					if (!state.isAir()) {
						VoxelShape shape = state.getShape(level, pos, selectionContext);
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

	private void addBox(VertexConsumer builder, Matrix4f matrix4f, float alpha, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
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

	private float getPulseProportion() {
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}