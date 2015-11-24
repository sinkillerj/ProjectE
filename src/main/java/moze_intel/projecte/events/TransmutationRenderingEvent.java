package moze_intel.projecte.events;

import com.google.common.collect.Lists;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsVertexFormat;
import net.minecraft.realms.Tezzelator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class TransmutationRenderingEvent 
{
	private Minecraft mc = Minecraft.getMinecraft();
	private final List<AxisAlignedBB> renderList = Lists.newArrayList();
	private double playerX;
	private double playerY;
	private double playerZ;
	private IBlockState transmutationResult;

	@SubscribeEvent
	public void preDrawHud(RenderGameOverlayEvent.Pre event)
	{
		if (event.type == ElementType.CROSSHAIRS)
		{
			if (transmutationResult != null)
			{
				if (FluidRegistry.lookupFluidForBlock(transmutationResult.getBlock()) != null)
				{
					TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(FluidRegistry.lookupFluidForBlock(transmutationResult.getBlock()).getFlowing().toString());
					mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
					Tessellator tess = Tessellator.getInstance();
					WorldRenderer render = tess.getWorldRenderer();
//					render.startDrawingQuads(); todo 1.8.8
//					render.addVertexWithUV(0, 0, 0, sprite.getMinU(), sprite.getMinV());
//					render.addVertexWithUV(0, 16, 0, sprite.getMinU(), sprite.getMaxV());
//					render.addVertexWithUV(16, 16, 0, sprite.getMaxU(), sprite.getMaxV());
//					render.addVertexWithUV(16, 0, 0, sprite.getMaxU(), sprite.getMinV());
					tess.draw();
				} else
				{
					RenderHelper.enableStandardItemLighting();
					mc.getRenderItem().renderItemIntoGUI(ItemHelper.stateToStack(transmutationResult, 1), 0, 0);
					RenderHelper.disableStandardItemLighting();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onOverlay(DrawBlockHighlightEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = player.worldObj;
		ItemStack stack = player.getHeldItem();
		
		if (stack == null || stack.getItem() != ObjHandler.philosStone || !stack.hasTagCompound())
		{
			transmutationResult = null;
			return;
		}
		
		playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) event.partialTicks;
		playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) event.partialTicks;
		playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) event.partialTicks;
		
		MovingObjectPosition mop = event.target;
		
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
		{
			IBlockState current = world.getBlockState(mop.getBlockPos());
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.isSneaking());

			if (transmutationResult != null)
			{
				byte charge = ((ItemMode) stack.getItem()).getCharge(stack);
				byte mode = ((ItemMode) stack.getItem()).getMode(stack);

				for (BlockPos pos : PhilosophersStone.getAffectedPositions(world, mop.getBlockPos(), player, mop.sideHit, mode, charge))
				{
					addBlockToRenderList(pos);
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
	
	private void drawAll()
	{
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);

		GlStateManager.color(1.0f, 1.0f, 1.0f, ProjectEConfig.pulsatingOverlay ? getPulseProportion() * 0.60f : 0.35f);
		
		Tezzelator tess = new Tezzelator();
		
		for (AxisAlignedBB b : renderList)
		{
			//Top todo 1.8.8
			tess.begin(7, DefaultVertexFormats.field_181707_g);
			tess.vertex(b.minX, b.maxY, b.minZ).endVertex();
//			r.startDrawingQuads();
//			r.addVertex(b.minX, b.maxY, b.minZ);
//			r.addVertex(b.maxX, b.maxY, b.minZ);
//			r.addVertex(b.maxX, b.maxY, b.maxZ);
//			r.addVertex(b.minX, b.maxY, b.maxZ);
//			tessellator.draw();
//
//			//Bottom
//			r.startDrawingQuads();
//			r.addVertex(b.minX, b.minY, b.minZ);
//			r.addVertex(b.maxX, b.minY, b.minZ);
//			r.addVertex(b.maxX, b.minY, b.maxZ);
//			r.addVertex(b.minX, b.minY, b.maxZ);
//			tessellator.draw();
//
//			//Front
//			r.startDrawingQuads();
//			r.addVertex(b.maxX, b.maxY, b.maxZ);
//			r.addVertex(b.minX, b.maxY, b.maxZ);
//			r.addVertex(b.minX, b.minY, b.maxZ);
//			r.addVertex(b.maxX, b.minY, b.maxZ);
//			tessellator.draw();
//
//			//Back
//			r.startDrawingQuads();
//			r.addVertex(b.maxX, b.minY, b.minZ);
//			r.addVertex(b.minX, b.minY, b.minZ);
//			r.addVertex(b.minX, b.maxY, b.minZ);
//			r.addVertex(b.maxX, b.maxY, b.minZ);
//			tessellator.draw();
//
//			//Left
//			r.startDrawingQuads();
//			r.addVertex(b.minX, b.maxY, b.maxZ);
//			r.addVertex(b.minX, b.maxY, b.minZ);
//			r.addVertex(b.minX, b.minY, b.minZ);
//			r.addVertex(b.minX, b.minY, b.maxZ);
//			tessellator.draw();
//
//			//Right
//			r.startDrawingQuads();
//			r.addVertex(b.maxX, b.maxY, b.maxZ);
//			r.addVertex(b.maxX, b.maxY, b.minZ);
//			r.addVertex(b.maxX, b.minY, b.minZ);
//			r.addVertex(b.maxX, b.minY, b.maxZ);
//			tessellator.draw();
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	private void addBlockToRenderList(BlockPos pos)
	{
		AxisAlignedBB box = new AxisAlignedBB(pos.getX() - 0.02f, pos.getY() - 0.02f, pos.getZ() - 0.02f, pos.getX() + 1.02f, pos.getY() + 1.02f, pos.getZ() + 1.02f);
		box = box.offset(-playerX, -playerY, -playerZ);
		renderList.add(box);
	}

	private float getPulseProportion()
	{
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}
