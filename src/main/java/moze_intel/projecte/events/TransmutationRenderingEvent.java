package moze_intel.projecte.events;

import com.google.common.collect.Lists;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
				RenderHelper.enableStandardItemLighting();
				mc.getRenderItem().renderItemIntoGUI(ItemHelper.stateToStack(transmutationResult, 1), 0, 0);
				RenderHelper.disableStandardItemLighting();
			}
		}
	}
	
	@SubscribeEvent
	public void onOverlay(DrawBlockHighlightEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = player.worldObj;
		ItemStack stack = player.getHeldItem();
		
		if (stack == null || stack.getItem() != ObjHandler.philosStone)
		{
			if (transmutationResult != null)
			{
				transmutationResult = null;
			}
			
			return;
		}
		
		playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) event.partialTicks;
		playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) event.partialTicks;
		playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) event.partialTicks;
		
		MovingObjectPosition mop = event.target;
		
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
		{
			EnumFacing orientation = mop.sideHit;

			IBlockState current = world.getBlockState(mop.getBlockPos());
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.isSneaking());

			int blockX = mop.getBlockPos().getX();
			int blockY = mop.getBlockPos().getY();
			int blockZ = mop.getBlockPos().getZ();

			if (transmutationResult != null)
			{
				byte charge = ((ItemMode) stack.getItem()).getCharge(stack);

				switch (((ItemMode) stack.getItem()).getMode(stack))
				{
					case 0:
					{
						for (int x = blockX - charge; x <= blockX + charge; x++)
							for (int y = blockY - charge; y <= blockY + charge; y++)
								for (int z = blockZ - charge; z <= blockZ + charge; z++)
								{
									addBlockToRenderList(world, current, new BlockPos(x, y, z));
								}
						
						break;
					}
					case 1:
					{
						if (orientation == EnumFacing.UP || orientation == EnumFacing.DOWN)
						{
							for (int x = blockX - charge; x <= blockX + charge; x++)
								for (int z = blockZ - charge; z <= blockZ + charge; z++)
								{
									addBlockToRenderList(world, current, new BlockPos(x, blockY, z));
								}
						}
						else if (orientation == EnumFacing.EAST || orientation == EnumFacing.WEST)
						{
							for (int y = blockY - charge; y <= blockY + charge; y++)
								for (int z = blockZ - charge; z <= blockZ + charge; z++)
								{
									addBlockToRenderList(world, current, new BlockPos(blockX, y, z));
								}
						}
						else if (orientation == EnumFacing.SOUTH || orientation == EnumFacing.NORTH)
						{
							for (int x = blockX - charge; x <= blockX + charge; x++)
								for (int y = blockY - charge; y <= blockY + charge; y++)
								{
									addBlockToRenderList(world, current, new BlockPos(x, y, blockZ));
								}
						}
						
						break;
					}
					case 2:
					{
						EnumFacing playerFacing = player.getHorizontalFacing();
						int side = orientation.getAxis() == EnumFacing.Axis.X ? 0 : orientation.getAxis() == EnumFacing.Axis.Z ? 1 : playerFacing == EnumFacing.NORTH || playerFacing == EnumFacing.SOUTH ? 0 : 1; // TODO 1.8 rework to be clearer
						
						if (side == 0)
						{
							for (int z = blockZ - charge; z <= blockZ + charge; z++)
							{
								addBlockToRenderList(world, current, new BlockPos(blockX, blockY, z));
							}
						}
						else 
						{
							for (int x = blockX - charge; x <= blockX + charge; x++)
							{
								addBlockToRenderList(world, current, new BlockPos(x, blockY, blockZ));
							}
						}
						
						break;
					}
				}
				
				drawAll();
				renderList.clear();
			}
		}
		else if (transmutationResult != null)
		{
			transmutationResult = null;
		}
	}
	
	private void drawAll()
	{
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);

		GlStateManager.color(1.0f, 1.0f, 1.0f, 0.35f);
		
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer r = tessellator.getWorldRenderer();
		
		float colorR = 1.0f;
		float colorG = 1.0f;
		float colorB = 1.0f;
		
		for (AxisAlignedBB b : renderList)
		{
			//Top
			r.startDrawingQuads();
			r.addVertex(b.minX, b.maxY, b.minZ);
			r.addVertex(b.maxX, b.maxY, b.minZ);
			r.addVertex(b.maxX, b.maxY, b.maxZ);
			r.addVertex(b.minX, b.maxY, b.maxZ);
			tessellator.draw();
			
			//Bottom 
			r.startDrawingQuads();
			r.addVertex(b.minX, b.minY, b.minZ);
			r.addVertex(b.maxX, b.minY, b.minZ);
			r.addVertex(b.maxX, b.minY, b.maxZ);
			r.addVertex(b.minX, b.minY, b.maxZ);
			tessellator.draw();
			
			//Front
			r.startDrawingQuads();
			r.addVertex(b.maxX, b.maxY, b.maxZ);
			r.addVertex(b.minX, b.maxY, b.maxZ);
			r.addVertex(b.minX, b.minY, b.maxZ);
			r.addVertex(b.maxX, b.minY, b.maxZ);
			tessellator.draw();
			
			//Back
			r.startDrawingQuads();
			r.addVertex(b.maxX, b.minY, b.minZ);
			r.addVertex(b.minX, b.minY, b.minZ);
			r.addVertex(b.minX, b.maxY, b.minZ);
			r.addVertex(b.maxX, b.maxY, b.minZ);
			tessellator.draw();
			
			//Left
			r.startDrawingQuads();
			r.addVertex(b.minX, b.maxY, b.maxZ);
			r.addVertex(b.minX, b.maxY, b.minZ);
			r.addVertex(b.minX, b.minY, b.minZ);
			r.addVertex(b.minX, b.minY, b.maxZ);
			tessellator.draw();
			
			//Right
			r.startDrawingQuads();
			r.addVertex(b.maxX, b.maxY, b.maxZ);
			r.addVertex(b.maxX, b.maxY, b.minZ);
			r.addVertex(b.maxX, b.minY, b.minZ);
			r.addVertex(b.maxX, b.minY, b.maxZ);
			tessellator.draw();
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	private void addBlockToRenderList(World world, IBlockState current, BlockPos pos)
	{
		if (world.getBlockState(pos) == current)
		{
			AxisAlignedBB box = new AxisAlignedBB(pos.getX() - 0.02f, pos.getY() - 0.02f, pos.getZ() - 0.02f, pos.getX() + 1.02f, pos.getY() + 1.02f, pos.getZ() + 1.02f);
			box = box.offset(-playerX, -playerY, -playerZ);
			renderList.add(box);
		}
	}
}
