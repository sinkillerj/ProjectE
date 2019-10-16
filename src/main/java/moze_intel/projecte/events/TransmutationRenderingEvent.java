package moze_intel.projecte.events;

import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class TransmutationRenderingEvent
{
	private static final Minecraft mc = Minecraft.getInstance();
	private static BlockState transmutationResult;

	@SubscribeEvent
	public static void preDrawHud(RenderGameOverlayEvent.Pre event)
	{
		if (event.getType() == ElementType.CROSSHAIRS)
		{
			if (transmutationResult != null)
			{
				if (transmutationResult.getBlock() instanceof FlowingFluidBlock)
				{
					ResourceLocation spriteName = ((FlowingFluidBlock) transmutationResult.getBlock()).getFluid().getAttributes().getFlowingTexture();
					TextureAtlasSprite sprite = mc.getTextureMap().getSprite(spriteName);
					mc.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
					BufferBuilder wr = Tessellator.getInstance().getBuffer();
					wr.begin(7, DefaultVertexFormats.POSITION_TEX);
					wr.pos(0, 0, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
					wr.pos(0, 16, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
					wr.pos(16, 16, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
					wr.pos(16, 0, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
					Tessellator.getInstance().draw();
				} else
				{
					RenderHelper.enableStandardItemLighting();

					IBakedModel model = mc.getBlockRendererDispatcher().getModelForState(transmutationResult);
					mc.getItemRenderer().renderItemModelIntoGUI(new ItemStack(transmutationResult.getBlock(), 1), 0, 0, model);

					RenderHelper.disableStandardItemLighting();
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onOverlay(DrawBlockHighlightEvent event)
	{
		PlayerEntity player = mc.player;
		World world = player.getEntityWorld();
		ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);

		if (stack.isEmpty())
			stack = player.getHeldItem(Hand.OFF_HAND);
		
		if (stack.isEmpty() || stack.getItem() != ObjHandler.philosStone)
		{
			transmutationResult = null;
			return;
		}
		
		double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) event.getPartialTicks();
		double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) event.getPartialTicks();
		double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) event.getPartialTicks();
		
		RayTraceResult mop = ((PhilosophersStone) ObjHandler.philosStone).getHitBlock(player);
		
		if (mop instanceof BlockRayTraceResult)
		{
			BlockRayTraceResult rtr = (BlockRayTraceResult) mop;
			BlockState current = world.getBlockState(rtr.getPos());
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.isSneaking());

			if (transmutationResult != null)
			{
				List<AxisAlignedBB> renderList = new ArrayList<>(1);
				int charge = ((ItemMode) stack.getItem()).getCharge(stack);
				byte mode = ((ItemMode) stack.getItem()).getMode(stack);

				for (BlockPos pos : PhilosophersStone.getAffectedPositions(world, rtr.getPos(), player, rtr.getFace(), mode, charge))
				{
					for (AxisAlignedBB bb : world.getBlockState(pos).getShape(world, pos).toBoundingBoxList())
					{
						renderList.add(bb.grow(0.01).offset(pos.getX() - playerX, pos.getY() - playerY, pos.getZ() - playerZ));
					}
				}
				
				drawAll(renderList);
			}
		}
		else
		{
			transmutationResult = null;
		}
	}
	
	private static void drawAll(List<AxisAlignedBB> renderList)
	{
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);

		GlStateManager.color4f(1.0f, 1.0f, 1.0f, ProjectEConfig.misc.pulsatingOverlay.get() ? getPulseProportion() * 0.60f : 0.35f);
		
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder wr = tess.getBuffer();

		wr.begin(7, DefaultVertexFormats.POSITION);

		for (AxisAlignedBB b : renderList)
		{
			//Top
			wr.pos(b.minX, b.maxY, b.minZ).endVertex();
			wr.pos(b.maxX, b.maxY, b.minZ).endVertex();
			wr.pos(b.maxX, b.maxY, b.maxZ).endVertex();
			wr.pos(b.minX, b.maxY, b.maxZ).endVertex();

			//Bottom
			wr.pos(b.minX, b.minY, b.minZ).endVertex();
			wr.pos(b.maxX, b.minY, b.minZ).endVertex();
			wr.pos(b.maxX, b.minY, b.maxZ).endVertex();
			wr.pos(b.minX, b.minY, b.maxZ).endVertex();

			//Front
			wr.pos(b.maxX, b.maxY, b.maxZ).endVertex();
			wr.pos(b.minX, b.maxY, b.maxZ).endVertex();
			wr.pos(b.minX, b.minY, b.maxZ).endVertex();
			wr.pos(b.maxX, b.minY, b.maxZ).endVertex();

			//Back
			wr.pos(b.maxX, b.minY, b.minZ).endVertex();
			wr.pos(b.minX, b.minY, b.minZ).endVertex();
			wr.pos(b.minX, b.maxY, b.minZ).endVertex();
			wr.pos(b.maxX, b.maxY, b.minZ).endVertex();

			//Left
			wr.pos(b.minX, b.maxY, b.maxZ).endVertex();
			wr.pos(b.minX, b.maxY, b.minZ).endVertex();
			wr.pos(b.minX, b.minY, b.minZ).endVertex();
			wr.pos(b.minX, b.minY, b.maxZ).endVertex();

			//Right
			wr.pos(b.maxX, b.maxY, b.maxZ).endVertex();
			wr.pos(b.maxX, b.maxY, b.minZ).endVertex();
			wr.pos(b.maxX, b.minY, b.minZ).endVertex();
			wr.pos(b.maxX, b.minY, b.maxZ).endVertex();
		}

		tess.draw();

		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	private static float getPulseProportion()
	{
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}
