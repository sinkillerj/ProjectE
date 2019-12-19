package moze_intel.projecte.events;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
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
					TextureAtlasSprite sprite = mc.func_228015_a_(PlayerContainer.field_226615_c_).apply(resultAttributes.getStillTexture());
					mc.textureManager.bindTexture(PlayerContainer.field_226615_c_);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder wr = tessellator.getBuffer();
					wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
					wr.func_225582_a_(0, 0, 0).func_225583_a_(sprite.getMinU(), sprite.getMinV()).func_227885_a_(red, green, blue, alpha).endVertex();
					wr.func_225582_a_(0, 16, 0).func_225583_a_(sprite.getMinU(), sprite.getMaxV()).func_227885_a_(red, green, blue, alpha).endVertex();
					wr.func_225582_a_(16, 16, 0).func_225583_a_(sprite.getMaxU(), sprite.getMaxV()).func_227885_a_(red, green, blue, alpha).endVertex();
					wr.func_225582_a_(16, 0, 0).func_225583_a_(sprite.getMaxU(), sprite.getMinV()).func_227885_a_(red, green, blue, alpha).endVertex();
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
	public static void onOverlay(DrawHighlightEvent event) {
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
		//TODO: Decide if this should this be event.getTarget()
		RayTraceResult mop = ((PhilosophersStone) ObjHandler.philosStone).getHitBlock(player);
		if (mop instanceof BlockRayTraceResult) {
			BlockRayTraceResult rtr = (BlockRayTraceResult) mop;
			BlockState current = world.getBlockState(rtr.getPos());
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.func_225608_bj_());

			if (transmutationResult != null) {
				Vec3d viewPosition = event.getInfo().getProjectedView();
				List<AxisAlignedBB> renderList = new ArrayList<>(1);
				int charge = ((ItemMode) stack.getItem()).getCharge(stack);
				byte mode = ((ItemMode) stack.getItem()).getMode(stack);
				for (BlockPos pos : PhilosophersStone.getAffectedPositions(world, rtr.getPos(), player, rtr.getFace(), mode, charge)) {
					double shiftX = pos.getX() - viewPosition.x;
					double shiftY = pos.getY() - viewPosition.y;
					double shiftZ = pos.getZ() - viewPosition.z;
					//TODO: Should this be getRenderShape
					world.getBlockState(pos).getShape(world, pos).forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
						renderList.add(new AxisAlignedBB(minX + shiftX, minY + shiftY, minZ + shiftZ, maxX + shiftX, maxY + shiftY, maxZ + shiftZ).grow(0.01));
					});
				}
				drawAll(renderList);
			}
		} else {
			transmutationResult = null;
		}
	}

	private static void drawAll(List<AxisAlignedBB> renderList) {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.disableTexture();
		RenderSystem.disableCull();
		RenderSystem.disableLighting();
		RenderSystem.depthMask(false);

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder wr = tess.getBuffer();

		//TODO: Hovering seems to make things like water lose their color, probably from the GL stuff (or maybe due to a lack of resetting the color?)
		// Note: only seems to happen when the border is showing
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		float alpha = ProjectEConfig.client.pulsatingOverlay.get() ? getPulseProportion() * 0.60F : 0.35F;
		for (AxisAlignedBB b : renderList) {
			//Top
			wr.func_225582_a_(b.minX, b.maxY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.maxX, b.maxY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.maxX, b.maxY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.minX, b.maxY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();

			//Bottom
			wr.func_225582_a_(b.minX, b.minY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.maxX, b.minY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.maxX, b.minY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.minX, b.minY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();

			//Front
			wr.func_225582_a_(b.maxX, b.maxY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.minX, b.maxY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.minX, b.minY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.maxX, b.minY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();

			//Back
			wr.func_225582_a_(b.maxX, b.minY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.minX, b.minY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.minX, b.maxY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.maxX, b.maxY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();

			//Left
			wr.func_225582_a_(b.minX, b.maxY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.minX, b.maxY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.minX, b.minY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.minX, b.minY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();

			//Right
			wr.func_225582_a_(b.maxX, b.maxY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.maxX, b.maxY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.maxX, b.minY, b.minZ).func_227885_a_(1, 1, 1, alpha).endVertex();
			wr.func_225582_a_(b.maxX, b.minY, b.maxZ).func_227885_a_(1, 1, 1, alpha).endVertex();
		}

		tess.draw();

		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
		RenderSystem.enableLighting();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	private static float getPulseProportion() {
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}