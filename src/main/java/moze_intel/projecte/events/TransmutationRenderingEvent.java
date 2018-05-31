package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = PECore.MODID)
public class TransmutationRenderingEvent 
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final List<AxisAlignedBB> renderList = new ArrayList<>();
	private static double playerX;
	private static double playerY;
	private static double playerZ;
	private static IBlockState transmutationResult;

	@SubscribeEvent
	public static void preDrawHud(RenderGameOverlayEvent.Pre event)
	{
		if (event.getType() == ElementType.CROSSHAIRS)
		{
			if (transmutationResult != null)
			{
				if (FluidRegistry.lookupFluidForBlock(transmutationResult.getBlock()) != null)
				{
					TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(FluidRegistry.lookupFluidForBlock(transmutationResult.getBlock()).getFlowing().toString());
					mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
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

					IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(transmutationResult);
					Minecraft.getMinecraft().getRenderItem().renderItemModelIntoGUI(ItemHelper.stateToDroppedStack(transmutationResult, 1), 0, 0, model);

					RenderHelper.disableStandardItemLighting();
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onOverlay(DrawBlockHighlightEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = player.getEntityWorld();
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

		if (stack.isEmpty())
			stack = player.getHeldItem(EnumHand.OFF_HAND);
		
		if (stack.isEmpty() || stack.getItem() != ObjHandler.philosStone)
		{
			transmutationResult = null;
			return;
		}
		
		playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) event.getPartialTicks();
		playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) event.getPartialTicks();
		playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) event.getPartialTicks();
		
		RayTraceResult mop = ((PhilosophersStone) ObjHandler.philosStone).getHitBlock(player);
		
		if (mop != null && mop.typeOfHit == Type.BLOCK)
		{
			IBlockState current = world.getBlockState(mop.getBlockPos());
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.isSneaking());

			if (transmutationResult != null)
			{
				int charge = ((ItemMode) stack.getItem()).getCharge(stack);
				byte mode = ((ItemMode) stack.getItem()).getMode(stack);

				for (BlockPos pos : PhilosophersStone.getAffectedPositions(world, mop.getBlockPos(), player, mop.sideHit, mode, charge))
				{
					addBlockToRenderList(world, pos);
				}
				
				drawAll();
				renderList.clear();
			}
		}
		else
		{
			transmutationResult = null;
		}
	}
	
	private static void drawAll()
	{
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);

		GlStateManager.color(1.0f, 1.0f, 1.0f, ProjectEConfig.misc.pulsatingOverlay ? getPulseProportion() * 0.60f : 0.35f);
		
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
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	private static void addBlockToRenderList(World world, BlockPos pos)
	{
		AxisAlignedBB box = world.getBlockState(pos).getSelectedBoundingBox(world, pos).grow(0.02);
		box = box.offset(-playerX, -playerY, -playerZ);
		renderList.add(box);
	}

	private static float getPulseProportion()
	{
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}
